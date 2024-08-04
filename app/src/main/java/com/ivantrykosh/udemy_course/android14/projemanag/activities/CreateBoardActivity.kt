package com.ivantrykosh.udemy_course.android14.projemanag.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.ivantrykosh.udemy_course.android14.projemanag.R
import com.ivantrykosh.udemy_course.android14.projemanag.databinding.ActivityCreateBoardBinding
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.firebase.Firestore
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Constants
import com.ivantrykosh.udemy_course.android14.projemanag.utils.FirebaseStorageObjects
import java.io.IOException

class CreateBoardActivity : BaseActivity() {
    private var _binding: ActivityCreateBoardBinding? = null
    private val binding get() = _binding!!

    private var mSelectedImageFileUri: Uri? = null

    private lateinit var mUsername: String
    private var mBoardImageUrl: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupActionBar()
        if (intent.hasExtra(User.FIELDS.NAME)) {
            mUsername = intent.getStringExtra(User.FIELDS.NAME) ?: ""
        }
        binding.ivBoardImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }
        binding.btnCreate.setOnClickListener {
            if (mSelectedImageFileUri != null) {
                uploadBoardImage()
            } else {
                showProgressDialog(getString(R.string.please_wait))
                createBoard()
            }
        }
    }

    private fun uploadBoardImage() {
        showProgressDialog(getString(R.string.please_wait))
        if (mSelectedImageFileUri != null) {
            val sRef = FirebaseStorage.getInstance().reference.child(FirebaseStorageObjects.BOARD_IMAGE + System.currentTimeMillis() + "." + Constants.getFileExtension(
                mSelectedImageFileUri,
                this
            )
            )
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener { taskSnapshot ->
                val downloadUrl = taskSnapshot.metadata!!.reference!!.downloadUrl
                Log.i("Firebase board url:", downloadUrl.toString())
                hideProgressDialog()
                downloadUrl.addOnSuccessListener { uri ->
                    mBoardImageUrl = uri.toString()
                    createBoard()
                }
                    .addOnFailureListener {
                        hideProgressDialog()
                    }
            }
                .addOnFailureListener {
                    Toast.makeText(this@CreateBoardActivity, it.message, Toast.LENGTH_SHORT).show()
                    hideProgressDialog()
                }
        }
    }

    private fun createBoard() {
        val assignedUsersList: MutableList<String> = mutableListOf()
        assignedUsersList.add(getCurrentUserId())

        var board = Board(
            "",
            binding.etBoardName.text.toString(),
            mBoardImageUrl,
            mUsername,
            ArrayList(assignedUsersList)
        )

        Firestore().createBoard({
            Toast.makeText(this@CreateBoardActivity, R.string.board_created_successfully, Toast.LENGTH_LONG).show()
            boardCreatedSuccessfully()
        }, {
            hideProgressDialog()
        }, board)
    }

    private fun boardCreatedSuccessfully() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarCreateBoardActivity)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        binding.toolbarCreateBoardActivity.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            } else {
                Toast.makeText(this, R.string.you_denied_read_storage_permission, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    mSelectedImageFileUri = data.data

                    try {
                        Glide
                            .with(this)
                            .load(mSelectedImageFileUri)
                            .centerCrop()
                            .placeholder(R.drawable.ic_board_place_holder)
                            .into(binding.ivBoardImage)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}