package com.ivantrykosh.udemy_course.android14.projemanag.presenter.main.card_details

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ivantrykosh.udemy_course.android14.projemanag.R
import com.ivantrykosh.udemy_course.android14.projemanag.adapters.CardMemberListItemsAdapter
import com.ivantrykosh.udemy_course.android14.projemanag.databinding.FragmentCardDetailsBinding
import com.ivantrykosh.udemy_course.android14.projemanag.dialogs.LabelColorListDialog
import com.ivantrykosh.udemy_course.android14.projemanag.dialogs.MembersListDialog
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Card
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.SelectedMembers
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Task
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.main.MainActivity
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class CardDetailsFragment : Fragment() {
    private var _binding: FragmentCardDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainActivity: MainActivity

    private val cardDetailsViewModel: CardDetailsViewModel by viewModels()

    private lateinit var mBoardDetails: Board
    private var mTaskListPosition: Int = -1
    private var mCardListPosition: Int = -1

    private var mSelectedColor: String = ""
    private lateinit var mMembersDetailList: ArrayList<User>
    private var mSelectedDueDateMillis: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCardDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = requireActivity() as MainActivity
        binding.toolbarCardDetails.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        mainActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
        binding.toolbarCardDetails.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_delete_card -> {
                    alertDialogForDeleteCard(mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].name)
                    return@setOnMenuItemClickListener true
                }
            }
            false
        }
        getBundleData()
        binding.toolbarCardDetails.title = mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].name

        binding.etNameCardDetails.setText(mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].name)
        binding.etNameCardDetails.setSelection(binding.etNameCardDetails.text.toString().length)

        mSelectedColor = mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].labelColor
        if (mSelectedColor.isNotEmpty()) {
            setColor()
        }

        binding.btnUpdateCardDetails.setOnClickListener {
            if (binding.etNameCardDetails.text.toString().isNotEmpty()) {
                updateCardDetails()
            } else {
                Toast.makeText(context, R.string.please_enter_card_name, Toast.LENGTH_SHORT).show()
            }
        }
        binding.tvSelectLabelColor.setOnClickListener {
            labelColorListDialog()
        }
        binding.tvSelectMembers.setOnClickListener {
            membersListDialog()
        }
        setupSelectedMembersList()
        mSelectedDueDateMillis = mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].dueDate
        if (mSelectedDueDateMillis > 0L) {
            binding.tvSelectDueDate.text =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(Date(mSelectedDueDateMillis))
        }
        binding.tvSelectDueDate.setOnClickListener {
            showDataPicker()
        }

        observeUpdateTaskListState()
    }

    private fun observeUpdateTaskListState() {
        cardDetailsViewModel.updateTasksState.observe(viewLifecycleOwner) { result ->
            when {
                result.loading -> { }
                result.error.isNotEmpty() -> {
                    mainActivity.hideProgressDialog()
                    mainActivity.showErrorSnackBar(result.error)
                }
                else -> {
                    mainActivity.hideProgressDialog()
                    taskListUpdatedSuccessfully()
                }
            }
        }
    }

    private fun taskListUpdatedSuccessfully() {
        findNavController().popBackStack()
    }

    @Suppress("DEPRECATION")
    private fun getBundleData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mBoardDetails = arguments?.getParcelable(Constants.BOARD_DETAIL, Board::class.java)!!
            mMembersDetailList = arguments?.getParcelableArrayList(Constants.BOARD_MEMBERS_LIST, User::class.java)!!
        } else {
            mBoardDetails = arguments?.getParcelable(Constants.BOARD_DETAIL)!!
            mMembersDetailList = arguments?.getParcelableArrayList(Constants.BOARD_MEMBERS_LIST)!!
        }
        mTaskListPosition = arguments?.getInt(Constants.TASK_LIST_ITEM_POSITION) ?: -1
        mCardListPosition = arguments?.getInt(Constants.CARD_LIST_ITEM_POSITION) ?: -1
    }

    private fun setColor() {
        binding.tvSelectLabelColor.text = ""
        binding.tvSelectLabelColor.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    private fun alertDialogForDeleteCard(cardName: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.alert)
        builder.setMessage(getString(R.string.confirmation_message_to_delete_card, cardName))
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton(R.string.yes) { dialogInterface, _ ->
            dialogInterface.dismiss()
            deleteCard()
        }
        builder.setNegativeButton(R.string.no) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun updateCardDetails() {
        val card = Card(
            binding.etNameCardDetails.text.toString(),
            mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].assignedTo,
            mSelectedColor,
            mSelectedDueDateMillis
        )
        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeLast()
        mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition] = card
        mainActivity.showProgressDialog()
        cardDetailsViewModel.updateTasks(mBoardDetails.documentId, mBoardDetails.taskList)
    }

    private fun labelColorListDialog() {
        val colorsList = ArrayList(colorsList())
        val listDialog = object : LabelColorListDialog(requireContext(), colorsList, getString(R.string.str_select_label_color), mSelectedColor) {
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }
        }
        listDialog.show()
    }

    private fun membersListDialog() {
        val cardAssignedMembersList = mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].assignedTo
        if (cardAssignedMembersList.size > 0) {
            for (i in mMembersDetailList.indices) {
                for (j in cardAssignedMembersList) {
                    if (mMembersDetailList[i].id == j) {
                        mMembersDetailList[i].selected = true
                    }
                }
            }
        } else {
            for (i in mMembersDetailList.indices) {
                mMembersDetailList[i].selected = false
            }
        }
        val listDialog = object : MembersListDialog(requireContext(), mMembersDetailList, getString(R.string.select_members)) {
            override fun onItemSelected(user: User, action: String) {
                if (action == Constants.SELECT) {
                    if (!mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].assignedTo.contains(user.id)) {
                        mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].assignedTo.add(user.id)
                    }
                } else {
                    mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].assignedTo.remove(user.id)

                    for (i in mMembersDetailList.indices) {
                        if (mMembersDetailList[i].id == user.id) {
                            mMembersDetailList[i].selected = false
                        }
                    }
                }
                setupSelectedMembersList()
            }
        }
        listDialog.show()
    }

    private fun setupSelectedMembersList() {
        val cardAssignedMembersList = mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].assignedTo
        val selectedMembersList = ArrayList<SelectedMembers>()

        for (i in mMembersDetailList.indices) {
            for (j in cardAssignedMembersList) {
                if (mMembersDetailList[i].id == j) {
                    val selectedMembers = SelectedMembers(
                        mMembersDetailList[i].id,
                        mMembersDetailList[i].image
                    )
                    selectedMembersList.add(selectedMembers)
                }
            }
        }

        if (selectedMembersList.size > 0) {
            selectedMembersList.add(SelectedMembers("", ""))
            binding.tvSelectMembers.visibility = View.GONE
            binding.rvSelectedMembersList.visibility = View.VISIBLE
            binding.rvSelectedMembersList.layoutManager = GridLayoutManager(context, 6)
            val adapter = CardMemberListItemsAdapter(requireContext(), selectedMembersList, true)
            binding.rvSelectedMembersList.adapter = adapter
            adapter.setOnClickListener(object : CardMemberListItemsAdapter.OnClickListener{
                override fun onClick() {
                    membersListDialog()
                }
            })
        } else {
            binding.tvSelectMembers.visibility = View.VISIBLE
            binding.rvSelectedMembersList.visibility = View.GONE
        }
    }

    private fun showDataPicker() {
        val c = Calendar.getInstance()
        val currentYear = c.get(Calendar.YEAR)
        val currentMonth = c.get(Calendar.MONTH)
        val currentDay = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(requireContext(),
            { _, year, monthOfYear, dayOfMonth ->
                val sDayOfMonth = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                val sMonthOfYear = if ((monthOfYear + 1) < 10) "0${monthOfYear + 1}" else "0${monthOfYear}"
                val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"
                binding.tvSelectDueDate.text = selectedDate

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val theDate = sdf.parse(selectedDate)
                mSelectedDueDateMillis = theDate!!.time
            }, currentYear, currentMonth, currentDay)
        datePickerDialog.show()
    }

    private fun deleteCard() {
        val cardsList = mBoardDetails.taskList[mTaskListPosition].cards
        cardsList.removeAt(mCardListPosition)
        val taskList = mBoardDetails.taskList
        taskList.removeLast()
        taskList[mTaskListPosition].cards = cardsList
        mainActivity.showProgressDialog()
        cardDetailsViewModel.updateTasks(mBoardDetails.documentId, mBoardDetails.taskList)
    }

    private fun colorsList() = listOf(
        "#FFFFFF",
        "#FFFF00",
        "#FF00FF",
        "#00FFFF",
        "#000000",
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}