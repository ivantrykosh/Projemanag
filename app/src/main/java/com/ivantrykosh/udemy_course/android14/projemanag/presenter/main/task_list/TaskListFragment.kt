package com.ivantrykosh.udemy_course.android14.projemanag.presenter.main.task_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ivantrykosh.udemy_course.android14.projemanag.R
import com.ivantrykosh.udemy_course.android14.projemanag.adapters.TaskListItemAdapter
import com.ivantrykosh.udemy_course.android14.projemanag.databinding.FragmentTaskListBinding
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Card
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Task
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.main.MainActivity
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskListFragment : Fragment() {
    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val taskListViewModel: TaskListViewModel by viewModels()

    private lateinit var mainActivity: MainActivity

    private lateinit var mUserId: String
    private lateinit var mBoardId: String
    private lateinit var mBoard: Board
    lateinit var mAssignedMembersDetailList: ArrayList<User>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = requireActivity() as MainActivity
        binding.toolbarTaskList.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        mainActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
        mUserId = arguments?.getString(User.FIELDS.ID) ?: ""
        mBoardId = arguments?.getString(Board.FIELDS.DOCUMENT_ID) ?: ""
        getBoardDetails(mBoardId)
        binding.toolbarTaskList.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_members -> {
                    val bundle = Bundle()
                    bundle.putParcelable(Constants.BOARD_DETAIL, mBoard)
                    findNavController().navigate(R.id.action_task_list_to_members, bundle)
                    return@setOnMenuItemClickListener true
                }
            }
            false
        }
        observeGetBoardState()
        observeGetUsersByIdsState()
        observeUpdateTasksState()
    }

    private fun observeGetBoardState() {
        taskListViewModel.getBoardState.observe(viewLifecycleOwner) { result ->
            when {
                result.loading -> { }
                result.error.isNotEmpty() -> {
                    mainActivity.hideProgressDialog()
                    mainActivity.showErrorSnackBar(result.error)
                }
                else -> {
                    mainActivity.hideProgressDialog()
                    boardDetailsSuccess(result.data!!)
                }
            }
        }
    }

    private fun observeGetUsersByIdsState() {
        taskListViewModel.getUsersByIdsState.observe(viewLifecycleOwner) { result ->
            when {
                result.loading -> { }
                result.error.isNotEmpty() -> {
                    mainActivity.hideProgressDialog()
                    mainActivity.showErrorSnackBar(result.error)
                }
                else -> {
                    mainActivity.hideProgressDialog()
                    boardMembersListSuccess(result.data!!)
                }
            }
        }
    }

    private fun observeUpdateTasksState() {
        taskListViewModel.updateTasksState.observe(viewLifecycleOwner) { result ->
            when {
                result == null -> { }
                result.loading -> { }
                result.error.isNotEmpty() -> {
                    mainActivity.hideProgressDialog()
                    mainActivity.showErrorSnackBar(result.error)
                }
                else -> {
                    mainActivity.hideProgressDialog()
                    getBoardDetails(mBoard.documentId)
                }
            }
        }
    }

    private fun getBoardDetails(id: String) {
        mainActivity.showProgressDialog()
        taskListViewModel.getBoard(id)
    }

    private fun boardDetailsSuccess(board: Board) {
        mBoard = board
        binding.toolbarTaskList.title = mBoard.name
        getAssignedUsers(mBoard.assignedTo)
    }

    private fun getAssignedUsers(assignedUsersIds: List<String>) {
        mainActivity.showProgressDialog()
        taskListViewModel.getUsersByIds(assignedUsersIds)
    }

    private fun boardMembersListSuccess(users: List<User>) {
        mAssignedMembersDetailList = ArrayList(users)

        val addTaskList = Task(getString(R.string.add_list))
        mBoard.taskList.add(addTaskList)

        binding.rvTaskList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvTaskList.setHasFixedSize(true)
        val adapter = TaskListItemAdapter(requireContext(), mBoard.taskList, this)
        binding.rvTaskList.adapter = adapter
    }

    fun createTaskList(taskListName: String) {
        val task = Task(taskListName, mUserId)
        mBoard.taskList.add(0, task)
        mBoard.taskList.removeLast()
        updateTasks()
    }

    fun updateTaskList(position: Int, listName: String, model: Task) {
        val task = Task(listName, model.createdBy, model.cards)
        mBoard.taskList[position] = task
        mBoard.taskList.removeLast()
        updateTasks()
    }

    fun addCardToTaskList(position: Int, cardName: String) {
        mBoard.taskList.removeLast()
        val cardAssignedUsersList = ArrayList<String>()
        cardAssignedUsersList.add(mUserId)
        val card = Card(cardName, mUserId, cardAssignedUsersList)
        val cardList = mBoard.taskList[position].cards
        cardList.add(card)
        val task = Task(
            mBoard.taskList[position].title,
            mBoard.taskList[position].createdBy,
            cardList
        )
        mBoard.taskList[position] = task
        updateTasks()
    }

    fun cardDetails(taskListPosition: Int, cardPosition: Int) {
        val bundle = Bundle()
        bundle.putParcelable(Constants.BOARD_DETAIL, mBoard)
        bundle.putInt(Constants.TASK_LIST_ITEM_POSITION, taskListPosition)
        bundle.putInt(Constants.CARD_LIST_ITEM_POSITION, cardPosition)
        bundle.putParcelableArrayList(Constants.BOARD_MEMBERS_LIST, mAssignedMembersDetailList)
        findNavController().navigate(R.id.action_task_list_to_card_details, bundle)
    }

    fun updateCardsInTaskList(taskListPosition: Int, cards: ArrayList<Card>) {
        mBoard.taskList.removeLast()
        mBoard.taskList[taskListPosition].cards = cards
        updateTasks()
    }

    fun deleteTaskList(position: Int) {
        mBoard.taskList.removeAt(position)
        mBoard.taskList.removeLast()
        updateTasks()
    }

    private fun updateTasks() {
        mainActivity.showProgressDialog()
        taskListViewModel.updateTasks(mBoard.documentId, mBoard.taskList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        taskListViewModel.clearValues()
    }
}