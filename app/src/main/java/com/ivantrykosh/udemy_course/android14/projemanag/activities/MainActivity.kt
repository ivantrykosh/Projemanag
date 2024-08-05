package com.ivantrykosh.udemy_course.android14.projemanag.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.iid.FirebaseInstanceId
import com.ivantrykosh.udemy_course.android14.projemanag.databinding.ActivityMainBinding
import com.ivantrykosh.udemy_course.android14.projemanag.firebase.Firestore
import com.ivantrykosh.udemy_course.android14.projemanag.R
import com.ivantrykosh.udemy_course.android14.projemanag.adapters.BoardItemsAdapter
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.BaseActivity
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.auth.AuthActivity
import com.ivantrykosh.udemy_course.android14.projemanag.utils.AppPreferences
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Constants


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var mUsername: String

    companion object {
        private const val MY_PROFILE_REQUEST_CODE = 11
        private const val CREATE_BOARD_REQUEST_CODE = 12
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
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
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this@MainActivity) {
                updateFcmToken(it.token)
            }
        }

        Firestore().loadUserData({ updateNavigationUserDetails(it) }) { hideProgressDialog() }
        binding.appBarMain.fabCreateBoard.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User.FIELDS.NAME, mUsername)
            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)
        }
    }

    fun populateBoardListToUI(boardList: List<Board>) {
        hideProgressDialog()
        if (boardList.isNotEmpty()) {
            binding.appBarMain.appBarMainContent.rvBoardsList.visibility = View.VISIBLE
            binding.appBarMain.appBarMainContent.tvNoBoardsAvailable.visibility = View.GONE

            binding.appBarMain.appBarMainContent.rvBoardsList.layoutManager = LinearLayoutManager(this)
            binding.appBarMain.appBarMainContent.rvBoardsList.setHasFixedSize(true)
            val adapter = BoardItemsAdapter(this, boardList.toMutableList())
            binding.appBarMain.appBarMainContent.rvBoardsList.adapter = adapter
            adapter.setOnClickListener(object : BoardItemsAdapter.OnClickListener {
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Board.FIELDS.DOCUMENT_ID, model.documentId)
                    startActivity(intent)
                }
            })
        } else {
            binding.appBarMain.appBarMainContent.rvBoardsList.visibility = View.GONE
            binding.appBarMain.appBarMainContent.tvNoBoardsAvailable.visibility = View.VISIBLE
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

    private fun setupActionBar() {
        setSupportActionBar(binding.appBarMain.toolbarMainActivity)
        binding.appBarMain.toolbarMainActivity.setNavigationIcon(R.drawable.ic_action_navigation_main)
        binding.appBarMain.toolbarMainActivity.setNavigationOnClickListener {
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
                startActivityForResult(Intent(this, MyProfileActivity::class.java), MY_PROFILE_REQUEST_CODE)
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
        userHashMap[com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User.FIELDS.FCM_TOKEN] = token
        showProgressDialog()
        Firestore().updateUserProfileData({ tokenUpdateSuccess() }, { hideProgressDialog() }, userHashMap)
    }
}