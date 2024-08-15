package com.ivantrykosh.udemy_course.android14.projemanag.presenter.main.main

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.ivantrykosh.udemy_course.android14.projemanag.R
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.adapters.BoardItemsAdapter
import com.ivantrykosh.udemy_course.android14.projemanag.databinding.FragmentMainBinding
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.main.MainActivity
import com.ivantrykosh.udemy_course.android14.projemanag.utils.AppPreferences
import com.ivantrykosh.udemy_course.android14.projemanag.utils.SwipeToDeleteCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var mUsername: String
    private lateinit var mUserId: String
    private var backPressedTime: Long = 0
    private lateinit var mainActivity: MainActivity

    private val onNavigationItemClickListener = NavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_my_profile -> {
                findNavController().navigate(R.id.action_main_to_my_profile)
            }
            R.id.nav_sign_out -> {
                mainViewModel.signOut()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        true
    }

    private val requestForNotifications = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

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
            val bundle = Bundle()
            bundle.putString(User.FIELDS.ID, mUserId)
            bundle.putString(User.FIELDS.NAME, mUsername)
            findNavController().navigate(R.id.action_main_to_create_board, bundle)
        }
        observeGetCurrentUserState()
        observeGetBoardsState()
        observeUpdateFCMTokenState()
        observeSignOutState()
        observeGetTokenState()
        observeDeleteBoardState()

        checkNotificationPermission()
    }

    private fun checkNotificationPermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> { }
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                showRequestPermissionRationale()
            }
            else -> @RequiresApi(Build.VERSION_CODES.TIRAMISU) {
                requestForNotifications.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun showRequestPermissionRationale() {
        AlertDialog.Builder(context)
            .setTitle(R.string.notification_permission_is_denied)
            .setMessage(R.string.please_allow_notifications_in_settings)
            .setPositiveButton(R.string.ok) { _, _ ->
                navigateToSettings()
            }
            .setNegativeButton(R.string.cancel) { _, _ -> }
            .show()
    }

    private fun navigateToSettings() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context?.packageName)
        startActivity(intent)
    }

    private fun observeGetCurrentUserState() {
        mainViewModel.getCurrentUserState.observe(viewLifecycleOwner) { result ->
            when {
                result.loading -> { }
                result.error.isNotEmpty() -> {
                    mainActivity.hideProgressDialog()
                    mainActivity.showErrorSnackBar(result.error)
                }
                else -> {
                    mainActivity.hideProgressDialog()
                    updateNavigationUserDetails(result.data!!)
                }
            }
        }
    }

    private fun observeGetBoardsState() {
        mainViewModel.getBoardsState.observe(viewLifecycleOwner) { result ->
            when {
                result.loading -> { }
                result.error.isNotEmpty() -> {
                    mainActivity.hideProgressDialog()
                    mainActivity.showErrorSnackBar(result.error)
                }
                else -> {
                    mainActivity.hideProgressDialog()
                    populateBoardListToUI(result.data!!)
                }
            }
        }
    }

    private fun observeUpdateFCMTokenState() {
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

    private fun observeSignOutState() {
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

    private fun observeGetTokenState() {
        mainViewModel.getTokenState.observe(viewLifecycleOwner) { result ->
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

    private fun observeDeleteBoardState() {
        mainViewModel.deleteBoardState.observe(viewLifecycleOwner) { result ->
            when {
                result.loading -> { }
                result.error.isNotEmpty() -> {
                    mainActivity.hideProgressDialog()
                    mainActivity.showErrorSnackBar(result.error)
                }
                else -> {
                    mainViewModel.getBoards()
                }
            }
        }
    }

    private fun loadToken() {
        mainViewModel.getToken()
    }

    private fun loadUser() {
        mainViewModel.getCurrentUser()
    }

    private fun updateNavigationUserDetails(user: User) {
        mUsername = user.name
        mUserId = user.id
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding.drawerLayout.findViewById(R.id.nav_user_image))

        binding.drawerLayout.findViewById<TextView>(R.id.tv_username).text = user.name

        mainActivity.showProgressDialog()
        mainViewModel.getBoards()
    }

    private fun populateBoardListToUI(boardList: List<Board>) {
        if (boardList.isNotEmpty()) {
            binding.mainContent.rvBoardsList.visibility = View.VISIBLE
            binding.mainContent.tvNoBoardsAvailable.visibility = View.GONE

            binding.mainContent.rvBoardsList.layoutManager = LinearLayoutManager(requireContext())
            binding.mainContent.rvBoardsList.setHasFixedSize(true)
            val adapter = BoardItemsAdapter(requireContext(), boardList.toMutableList())
            binding.mainContent.rvBoardsList.adapter = adapter
            adapter.setOnClickListener(object : BoardItemsAdapter.OnClickListener {
                override fun onClick(position: Int, model: Board) {
                    val bundle = Bundle()
                    bundle.putString(Board.FIELDS.DOCUMENT_ID, model.documentId)
                    bundle.putString(User.FIELDS.ID, mUserId)
                    findNavController().navigate(R.id.action_main_to_task_list, bundle)
                }
            })

            val deleteSwipeHandler = object : SwipeToDeleteCallback(requireContext()) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    mainActivity.showProgressDialog()
                    mainViewModel.deleteBoard(boardList[viewHolder.adapterPosition].documentId)
                    val boardsAdapter = binding.mainContent.rvBoardsList.adapter as BoardItemsAdapter
                    boardsAdapter.removeAt(viewHolder.adapterPosition)
                }
            }

            val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
            deleteItemTouchHelper.attachToRecyclerView(binding.mainContent.rvBoardsList)
        } else {
            binding.mainContent.rvBoardsList.visibility = View.GONE
            binding.mainContent.tvNoBoardsAvailable.visibility = View.VISIBLE
        }
    }

    private fun updateFcmToken(token: String) {
        mainViewModel.updateUserFCMToken(token)
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