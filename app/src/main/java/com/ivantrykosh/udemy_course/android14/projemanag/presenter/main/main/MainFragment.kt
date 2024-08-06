package com.ivantrykosh.udemy_course.android14.projemanag.presenter.main.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.ivantrykosh.udemy_course.android14.projemanag.R
import com.ivantrykosh.udemy_course.android14.projemanag.adapters.BoardItemsAdapter
import com.ivantrykosh.udemy_course.android14.projemanag.databinding.FragmentMainBinding
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.main.MainActivity
import com.ivantrykosh.udemy_course.android14.projemanag.utils.AppPreferences
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var mUsername: String
    private var backPressedTime: Long = 0
    private lateinit var mainActivity: MainActivity

    private val onNavigationItemClickListener = NavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_my_profile -> {
                // todo
//                startActivityForResult(
//                    Intent(mainActivity, MyProfileActivity::class.java),
//                    Main_Activity.MY_PROFILE_REQUEST_CODE
//                )
            }
            R.id.nav_sign_out -> {
                mainViewModel.signOut()
                mainViewModel.signOutState.observe(viewLifecycleOwner) { result ->
                    when {
                        result.loading -> { }
                        result.error.isNotEmpty() -> { mainActivity.showErrorSnackBar(result.error) }
                        else -> {
                            mainActivity.logout()
                        }
                    }
                }
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = requireActivity() as MainActivity
        binding.mainContent.toolbarMain.setNavigationOnClickListener {
            toggleDrawer()
        }
        mainActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPressed()
            }
        })
        binding.navView.setNavigationItemSelectedListener(onNavigationItemClickListener)

        mainActivity.showProgressDialog()
        val tokenUpdated = AppPreferences.fcmTokenUpdated ?: false
        if (tokenUpdated) {
            loadUser()
        } else {
            loadToken()
        }

        binding.mainContent.fabCreateBoard.setOnClickListener {
            // todo
//            val intent = Intent(this, CreateBoardActivity::class.java)
//            intent.putExtra(User.FIELDS.NAME, mUsername)
//            startActivityForResult(intent, Main_Activity.CREATE_BOARD_REQUEST_CODE)
        }
    }

    private fun loadToken() {
        mainViewModel.getCurrentUserId()
        mainViewModel.getCurrentUserIdState.observe(viewLifecycleOwner) { result ->
            when {
                result.loading -> { }
                result.error.isNotEmpty() -> {
                    mainActivity.hideProgressDialog()
                    mainActivity.showErrorSnackBar(result.error)
                }
                else -> {
                    updateFcmToken(result.data!!)
                }
            }
        }
    }

    private fun loadUser() {
        mainViewModel.getCurrentUser()
        mainViewModel.getCurrentUserState.observe(viewLifecycleOwner) { result ->
            when {
                result.loading -> { }
                result.error.isNotEmpty() -> {
                    mainActivity.hideProgressDialog()
                    mainActivity.showErrorSnackBar(result.error)
                }
                else -> {
                    updateNavigationUserDetails(result.data!!)
                }
            }
        }
    }

    private fun updateNavigationUserDetails(user: User) {
        mainActivity.hideProgressDialog()
        mUsername = user.name
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding.drawerLayout.findViewById(R.id.nav_user_image))

        binding.drawerLayout.findViewById<TextView>(R.id.tv_username).text = user.name

        mainActivity.showProgressDialog()
        mainViewModel.getBoards()
        mainViewModel.getBoardsState.observe(viewLifecycleOwner) { result ->
            when {
                result.loading -> { }
                result.error.isNotEmpty() -> {
                    mainActivity.hideProgressDialog()
                    mainActivity.showErrorSnackBar(result.error)
                }
                else -> {
                    populateBoardListToUI(result.data!!)
                }
            }
        }
    }

    private fun populateBoardListToUI(boardList: List<Board>) {
        mainActivity.hideProgressDialog()
        if (boardList.isNotEmpty()) {
            binding.mainContent.rvBoardsList.visibility = View.VISIBLE
            binding.mainContent.tvNoBoardsAvailable.visibility = View.GONE

            binding.mainContent.rvBoardsList.layoutManager = LinearLayoutManager(requireContext())
            binding.mainContent.rvBoardsList.setHasFixedSize(true)
            val adapter = BoardItemsAdapter(requireContext(), boardList.toMutableList())
            binding.mainContent.rvBoardsList.adapter = adapter
            adapter.setOnClickListener(object : BoardItemsAdapter.OnClickListener {
                override fun onClick(position: Int, model: Board) {
                    // todo
//                    val intent = Intent(this@Main_Activity, TaskListActivity::class.java)
//                    intent.putExtra(Board.FIELDS.DOCUMENT_ID, model.documentId)
//                    startActivity(intent)
                }
            })
        } else {
            binding.mainContent.rvBoardsList.visibility = View.GONE
            binding.mainContent.tvNoBoardsAvailable.visibility = View.VISIBLE
        }
    }

    private fun updateFcmToken(token: String) {
        mainViewModel.updateUserFCMToken(token)
        mainViewModel.updateUserFCMTokenState.observe(viewLifecycleOwner) { result ->
            when {
                result.loading -> { }
                result.error.isNotEmpty() -> {
                    mainActivity.hideProgressDialog()
                    mainActivity.showErrorSnackBar(result.error)
                }
                else -> {
                    tokenUpdateSuccess()
                }
            }
        }
    }

    private fun tokenUpdateSuccess() {
        AppPreferences.fcmTokenUpdated = true
        loadUser()
    }

    private fun backPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                mainActivity.doubleBackPressed()
            } else {
                Toast.makeText(context, getString(R.string.please_click_back_again_to_exit), Toast.LENGTH_SHORT).show()
            }
            backPressedTime = System.currentTimeMillis()
        }
    }

    private fun toggleDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}