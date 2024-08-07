package com.ivantrykosh.udemy_course.android14.projemanag.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.iid.FirebaseInstanceId
import com.ivantrykosh.udemy_course.android14.projemanag.firebase.Firestore
import com.ivantrykosh.udemy_course.android14.projemanag.R
import com.ivantrykosh.udemy_course.android14.projemanag.adapters.BoardItemsAdapter
import com.ivantrykosh.udemy_course.android14.projemanag.databinding.ActivityOldMainBinding
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.BaseActivity
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.auth.AuthActivity
import com.ivantrykosh.udemy_course.android14.projemanag.utils.AppPreferences


class Main_Activity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var _binding: ActivityOldMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var mUsername: String

    private var backPressedTime: Long = 0

    companion object {
        private const val MY_PROFILE_REQUEST_CODE = 11
        private const val CREATE_BOARD_REQUEST_CODE = 12
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOldMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.drawerLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupActionBar()
        onBackPressedDispatcher.addCallback(this) {
            backPressed()
        }
        binding.navView.setNavigationItemSelectedListener(this)

        val tokenUpdated = AppPreferences.fcmTokenUpdated ?: false
        if (tokenUpdated) {
            showProgressDialog()
            Firestore().loadUserData({ updateNavigationUserDetails(it) }) { hideProgressDialog() }
        } else {
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this@Main_Activity) {
                updateFcmToken(it.token)
            }
        }

        Firestore().loadUserData({ updateNavigationUserDetails(it) }) { hideProgressDialog() }
        binding.mainContent.fabCreateBoard.setOnClickListener {
//            val intent = Intent(this, CreateBoardActivity::class.java)
//            intent.putExtra(User.FIELDS.NAME, mUsername)
//            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)
        }
    }

    private fun populateBoardListToUI(boardList: List<Board>) {
        hideProgressDialog()
        if (boardList.isNotEmpty()) {
            binding.mainContent.rvBoardsList.visibility = View.VISIBLE
            binding.mainContent.tvNoBoardsAvailable.visibility = View.GONE

            binding.mainContent.rvBoardsList.layoutManager = LinearLayoutManager(this)
            binding.mainContent.rvBoardsList.setHasFixedSize(true)
            val adapter = BoardItemsAdapter(this, boardList.toMutableList())
            binding.mainContent.rvBoardsList.adapter = adapter
            adapter.setOnClickListener(object : BoardItemsAdapter.OnClickListener {
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@Main_Activity, TaskListActivity::class.java)
                    intent.putExtra(Board.FIELDS.DOCUMENT_ID, model.documentId)
                    startActivity(intent)
                }
            })
        } else {
            binding.mainContent.rvBoardsList.visibility = View.GONE
            binding.mainContent.tvNoBoardsAvailable.visibility = View.VISIBLE
        }
    }

    private fun updateNavigationUserDetails(user: User) {
        hideProgressDialog()
        mUsername = user.name
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding.drawerLayout.findViewById(R.id.nav_user_image))

        binding.drawerLayout.findViewById<TextView>(R.id.tv_username).text = user.name

        showProgressDialog()
        Firestore().getBoardsList({ populateBoardListToUI(it) }, { hideProgressDialog() })
    }

    private fun backPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            doubleBackToExit()
        }
    }

    private fun doubleBackToExit() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finish()
        } else {
            Toast.makeText(this, getString(R.string.please_click_back_again_to_exit), Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.mainContent.toolbarMain)
        binding.mainContent.toolbarMain.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MY_PROFILE_REQUEST_CODE) {
                Firestore().loadUserData({ updateNavigationUserDetails(it) }) { hideProgressDialog() }
            } else if (requestCode == CREATE_BOARD_REQUEST_CODE) {
                showProgressDialog()
                Firestore().getBoardsList({ populateBoardListToUI(it) }, { hideProgressDialog() })
            }
        } else {
            Log.e("Result", "Canceled")
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_profile -> {
//                startActivityForResult(Intent(this, MyProfileActivity::class.java), MY_PROFILE_REQUEST_CODE)
            }
            R.id.nav_sign_out -> {
                Firestore().signOut()
                AppPreferences.clear()
                val intent = Intent(this, AuthActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun tokenUpdateSuccess() {
        hideProgressDialog()
        AppPreferences.fcmTokenUpdated = true
        showProgressDialog()
        Firestore().loadUserData({ updateNavigationUserDetails(it) }) { hideProgressDialog() }
    }

    private fun updateFcmToken(token: String) {
        val userHashMap = HashMap<String, Any>()
        userHashMap[User.FIELDS.FCM_TOKEN] = token
        showProgressDialog()
        Firestore().updateUserProfileData({ tokenUpdateSuccess() }, { hideProgressDialog() }, userHashMap)
    }
}