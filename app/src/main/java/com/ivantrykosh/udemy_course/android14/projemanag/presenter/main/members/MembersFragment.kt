package com.ivantrykosh.udemy_course.android14.projemanag.presenter.main.members

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ivantrykosh.udemy_course.android14.projemanag.R
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.adapters.MemberListItemsAdapter
import com.ivantrykosh.udemy_course.android14.projemanag.databinding.FragmentMembersBinding
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.main.MainActivity
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MembersFragment : Fragment() {
    private var _binding: FragmentMembersBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainActivity: MainActivity

    private val membersViewModel: MembersViewModel by viewModels()

    private lateinit var mAssignedMembersList: ArrayList<User>
    private lateinit var mBoard: Board

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMembersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = requireActivity() as MainActivity
        binding.toolbarMembers.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        mainActivity.onBackPressedDispatcher.addCallback {
            findNavController().popBackStack()
        }
        binding.toolbarMembers.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_add_member -> {
                    dialogSearchMember()
                    return@setOnMenuItemClickListener true
                }
            }
            false
        }
        getBundleData()
        getAssignedMembersDetails()
        observeGetAssignedMembersDetailsState()
        observeGetMemberDetailsState()
        observeAssignMembersToBoard()
    }

    private fun observeGetAssignedMembersDetailsState() {
        membersViewModel.getUsersByIdsState.observe(viewLifecycleOwner) { result ->
            when {
                result.loading -> { }
                result.error.isNotEmpty() -> {
                    mainActivity.hideProgressDialog()
                    mainActivity.showErrorSnackBar(result.error)
                }
                else -> {
                    mainActivity.hideProgressDialog()
                    setupMembersList(result.data!!)
                }
            }
        }
    }

    private fun observeGetMemberDetailsState() {
        membersViewModel.getUserByEmailState.observe(viewLifecycleOwner) { result ->
            when {
                result.loading -> { }
                result.error.isNotEmpty() -> {
                    mainActivity.hideProgressDialog()
                    mainActivity.showErrorSnackBar(result.error)
                }
                else -> {
                    mainActivity.hideProgressDialog()
                    memberDetails(result.data!!)
                }
            }
        }
    }

    private fun observeAssignMembersToBoard() {
        membersViewModel.assignMembersState.observe(viewLifecycleOwner) { result ->
            when {
                result.loading -> { }
                result.error.isNotEmpty() -> {
                    mainActivity.hideProgressDialog()
                    mainActivity.showErrorSnackBar(result.error)
                }
                else -> {
                    mainActivity.hideProgressDialog()
                    memberAssignSuccess()
                }
            }
        }
    }

    private fun dialogSearchMember() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.findViewById<TextView>(R.id.tv_add).setOnClickListener {
            val email = dialog.findViewById<EditText>(R.id.et_email_search_member).text.toString()
            if (email.isNotEmpty()) {
                dialog.dismiss()
                mainActivity.showProgressDialog()
                membersViewModel.getUserByEmail(email)
            } else {
                Toast.makeText(context, R.string.please_enter_email, Toast.LENGTH_SHORT).show()
            }
        }
        dialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    @Suppress("DEPRECATION")
    private fun getBundleData() {
        mBoard = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(Constants.BOARD_DETAIL, Board::class.java)!!
        } else {
            arguments?.getParcelable(Constants.BOARD_DETAIL)!!
        }
    }

    private fun setupMembersList(members: List<User>) {
        mAssignedMembersList = ArrayList(members)
        binding.rvMembersList.layoutManager = LinearLayoutManager(context)
        binding.rvMembersList.setHasFixedSize(true)
        val adapter = MemberListItemsAdapter(requireContext(), members.toMutableList())
        binding.rvMembersList.adapter = adapter
    }

    private fun getAssignedMembersDetails() {
        mainActivity.showProgressDialog()
        membersViewModel.getUsersByIds(mBoard.assignedTo)
    }

    private fun memberDetails(user: User) {
        mBoard.assignedTo.add(user.id)
        mAssignedMembersList.add(user)
        mainActivity.showProgressDialog()
        membersViewModel.assignMembers(mBoard.documentId, mBoard.assignedTo)
    }

    private fun memberAssignSuccess() {
        setupMembersList(mAssignedMembersList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}