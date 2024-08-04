package com.ivantrykosh.udemy_course.android14.projemanag.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ivantrykosh.udemy_course.android14.projemanag.R
import com.ivantrykosh.udemy_course.android14.projemanag.adapters.TaskListItemAdapter
import com.ivantrykosh.udemy_course.android14.projemanag.databinding.ActivityTaskListBinding
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Card
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Task
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.firebase.Firestore
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.BaseActivity
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Constants

class TaskListActivity : BaseActivity() {
    private var _binding: ActivityTaskListBinding? = null
    private val binding get() = _binding!!

    private lateinit var mBoardDetails: Board
    private lateinit var mBoardDocumentId: String
    lateinit var mAssignedMembersDetailList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTaskListBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mBoardDocumentId = ""
        if (intent.hasExtra(Board.FIELDS.DOCUMENT_ID)) {
            mBoardDocumentId = intent.getStringExtra(Board.FIELDS.DOCUMENT_ID) ?: ""
        }
        showProgressDialog()
        Firestore().getBoardDetails({ boardDetails(it) }, { hideProgressDialog() }, mBoardDocumentId)
    }

    fun boardMembersDetailsList(list: ArrayList<User>) {
        mAssignedMembersDetailList = list
        hideProgressDialog()

        val addTaskList = Task(getString(R.string.add_list))
        mBoardDetails.taskList.add(addTaskList)

        binding.rvTaskList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvTaskList.setHasFixedSize(true)
        val adapter = TaskListItemAdapter(this, mBoardDetails.taskList)
        binding.rvTaskList.adapter = adapter
    }

    fun updateCardsInTaskList(taskListPosition: Int, cards: ArrayList<Card>) {
        mBoardDetails.taskList.removeLast()
        mBoardDetails.taskList[taskListPosition].cards = cards
        showProgressDialog()
        Firestore().addUpdateTaskList({ addUpdateTaskListSuccess() }, { hideProgressDialog() }, mBoardDetails)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MEMBERS_REQUEST_CODE) {
                showProgressDialog()
                Firestore().getBoardDetails({ boardDetails(it) }, { hideProgressDialog() }, mBoardDocumentId)
            } else if (resultCode == CARD_DETAILS_REQUEST_CODE) {
                showProgressDialog()
                Firestore().getBoardDetails({ boardDetails(it) }, { hideProgressDialog() }, mBoardDocumentId)
            }
        }
    }

    fun cardDetails(taskListPosition: Int, cardPosition: Int) {
        val intent = Intent(this, CardDetailsActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION, taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION, cardPosition)
        intent.putExtra(Constants.BOARD_MEMBERS_LIST, mAssignedMembersDetailList)
        startActivityForResult(intent, CARD_DETAILS_REQUEST_CODE)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_members -> {
                val intent = Intent(this, MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
                startActivityForResult(intent, MEMBERS_REQUEST_CODE)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarTaskListActivity)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        supportActionBar!!.title = mBoardDetails.name
        binding.toolbarTaskListActivity.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun addUpdateTaskListSuccess() {
        hideProgressDialog()
        Firestore().getBoardDetails({ boardDetails(it) }, { hideProgressDialog() }, mBoardDetails.documentId)
    }

    fun updateTaskList(position: Int, listName: String, model: Task) {
        val task = Task(listName, model.createdBy)
        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeLast()
        showProgressDialog()
        Firestore().addUpdateTaskList({ addUpdateTaskListSuccess() }, { hideProgressDialog() }, mBoardDetails)
    }

    fun addCardToTaskList(position: Int, cardName: String) {
        mBoardDetails.taskList.removeLast()
        val cardAssignedUsersList = ArrayList<String>()
        cardAssignedUsersList.add(Firestore().getCurrentUserId())
        val card = Card(cardName, Firestore().getCurrentUserId(), cardAssignedUsersList)
        val cardList = mBoardDetails.taskList[position].cards
        cardList.add(card)
        val task = Task(
            mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardList
        )
        mBoardDetails.taskList[position] = task
        showProgressDialog()
        Firestore().addUpdateTaskList({ addUpdateTaskListSuccess() }, { hideProgressDialog() }, mBoardDetails)
    }

    private fun boardDetails(board: Board) {
        mBoardDetails = board
        hideProgressDialog()
        setupActionBar()

        showProgressDialog()
        Firestore().getAssignedMembersDetails( { boardMembersDetailsList(ArrayList(it)) }, { hideProgressDialog() }, mBoardDetails.assignedTo)
    }

    fun createTaskList(taskListName: String) {
        val task = Task(taskListName, Firestore().getCurrentUserId())
        mBoardDetails.taskList.add(0, task)
        mBoardDetails.taskList.removeLast()

        showProgressDialog()
        Firestore().addUpdateTaskList({ addUpdateTaskListSuccess() }, { hideProgressDialog() }, mBoardDetails)
    }

    fun deleteTaskList(position: Int) {
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeLast()

        showProgressDialog()
        Firestore().addUpdateTaskList({ addUpdateTaskListSuccess() }, { hideProgressDialog() }, mBoardDetails)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val MEMBERS_REQUEST_CODE = 13
        const val CARD_DETAILS_REQUEST_CODE = 14
    }
}