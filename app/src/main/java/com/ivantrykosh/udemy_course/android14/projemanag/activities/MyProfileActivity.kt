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
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.ivantrykosh.udemy_course.android14.projemanag.R
import com.ivantrykosh.udemy_course.android14.projemanag.databinding.ActivityMyProfileBinding
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.firebase.Firestore
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.MainViewModel
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Constants.PICK_IMAGE_REQUEST_CODE
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Constants.READ_STORAGE_PERMISSION_CODE
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Constants.getFileExtension
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Constants.showImageChooser
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException

@AndroidEntryPoint
class MyProfileActivity : BaseActivity() {
    private var _binding: ActivityMyProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()

    private var mSelectedImageFileUri: Uri? = null
    private var mDownloadableUrl: String? = null
    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMyProfileBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupActionBar()

        viewModel.getCurrentUser()
        viewModel.getCurrentUserState.observe(this) { getUser ->
            if (getUser.loading) {
                showProgressDialog(getString(R.string.please_wait))
            } else if (getUser.error.isNotEmpty()) {
                hideProgressDialog()
                Toast.makeText(this, getUser.error, Toast.LENGTH_SHORT).show()
            } else {
                hideProgressDialog()
                setUserDataInUI(getUser.data!!)
            }
        }
//        Firestore().loadUserData({ setUserDataInUI(it) }) { }
        binding.ivUserImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                showImageChooser(this)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_STORAGE_PERMISSION_CODE)
            }
        }
        binding.btnUpdate.setOnClickListener {
            if (mSelectedImageFileUri != null) {
                uploadUserImage()
            } else {
                showProgressDialog(getString(R.string.please_wait))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageChooser(this)
            } else {
                Toast.makeText(this, R.string.you_denied_read_storage_permission, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    mSelectedImageFileUri = data.data

                    try {
                        Glide
                            .with(this)
                            .load(mSelectedImageFileUri)
                            .centerCrop()
                            .placeholder(R.drawable.ic_user_place_holder)
                            .into(binding.ivUserImage)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun updateUserProfileData() {
        val userHashmap = HashMap<String, Any>()
        if (!mDownloadableUrl.isNullOrEmpty() && mDownloadableUrl != mUserDetails.image) {
            userHashmap[com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User.FIELDS.IMAGE] = mDownloadableUrl!!
        }
        if (binding.etName.text.toString() != mUserDetails.name) {
            userHashmap[com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User.FIELDS.NAME] = binding.etName.text.toString()
        }
        if (binding.etMobile.text.toString() != mUserDetails.mobile.toString()) {
            userHashmap[com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User.FIELDS.MOBILE] = binding.etMobile.text.toString().toLong()
        }
        Firestore().updateUserProfileData({
            profileUpdateSuccess()
        }, {
            hideProgressDialog(); Toast.makeText(this@MyProfileActivity, it, Toast.LENGTH_SHORT)
            .show()
        }, userHashmap)
    }

    private fun uploadUserImage() {
        showProgressDialog(getString(R.string.please_wait))
        if (mSelectedImageFileUri != null) {
            val sRef = FirebaseStorage.getInstance().reference.child("USER_IMAGE" + System.currentTimeMillis() + "." + getFileExtension(mSelectedImageFileUri, this))
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener { taskSnapshot ->
                val downloadUrl = taskSnapshot.metadata!!.reference!!.downloadUrl
                Log.i("Firebase image url:", downloadUrl.toString())
                hideProgressDialog()
                downloadUrl.addOnSuccessListener { uri ->
                    mDownloadableUrl = uri.toString()
                    updateUserProfileData()
                }
                .addOnFailureListener {
                    hideProgressDialog()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this@MyProfileActivity, it.message, Toast.LENGTH_SHORT).show()
                hideProgressDialog()
            }
        }
    }

    private fun setUserDataInUI(user: User) {
        mUserDetails = user
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding.ivUserImage)

        binding.etName.setText(user.name)
        binding.etEmail.setText(user.email)
        if (user.mobile != 0L) {
            binding.etMobile.setText(user.mobile.toString())
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarMyProfile)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        binding.toolbarMyProfile.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun profileUpdateSuccess() {
        Toast.makeText(this, R.string.user_updated_successfully, Toast.LENGTH_SHORT).show()
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}