package com.ivantrykosh.udemy_course.android14.projemanag.presenter.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ivantrykosh.udemy_course.android14.projemanag.R
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Card
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Task
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import java.util.Collections

class TaskListItemAdapter(
    private val context: Context,
    private var list: ArrayList<Task>,
    private val assignedMembersDetails: List<User>
): RecyclerView.Adapter<TaskListItemAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val addTaskList: TextView = view.findViewById(R.id.tv_add_task_list)
        val addTaskListView: CardView = view.findViewById(R.id.cv_add_task_list_name)
        val closeListNameButton: ImageButton = view.findViewById(R.id.ib_close_list_name)
        val taskListNameEdit: EditText = view.findViewById(R.id.et_task_list_name)
        val doneListNameButton: ImageButton = view.findViewById(R.id.ib_done_list_name)
        val taskItemLayout: LinearLayout = view.findViewById(R.id.ll_task_item)
        val titleViewLayout: LinearLayout = view.findViewById(R.id.ll_title_view)
        val taskListTitle: TextView = view.findViewById(R.id.tv_task_list_title)
        val editListNameButton: ImageButton = view.findViewById(R.id.ib_edit_list_name)
        val deleteListButton: ImageButton = view.findViewById(R.id.ib_delete_list)
        val editTaskListNameView: CardView = view.findViewById(R.id.cv_edit_task_list_name)
        val closeEditableViewButton: ImageButton = view.findViewById(R.id.ib_close_editable_view)
        val editTaskListName: EditText = view.findViewById(R.id.et_edit_task_list_name)
        val doneEditListNameButton: ImageButton = view.findViewById(R.id.ib_done_edit_list_name)
        val cardList: RecyclerView = view.findViewById(R.id.rv_card_list)
        val addCardView: CardView = view.findViewById(R.id.cv_add_card)
        val closeCardNameButton: ImageButton = view.findViewById(R.id.ib_close_card_name)
        val cardNameEdit: EditText = view.findViewById(R.id.et_card_name)
        val doneCardNameButton: ImageButton = view.findViewById(R.id.ib_done_card_name)
        val addCard: TextView = view.findViewById(R.id.tv_add_card)
    }

    private var mPositionDraggedFrom = -1
    private var mPositionDraggedTo = -1

    private var mCreateTaskListListener: OnCreateTaskListListener? = null
    private var mUpdateTaskListListener: OnUpdateTaskListListener? = null
    private var mAddCardToTaskListListener: OnAddCardToTaskListListener? = null
    private var mCardClickListener: OnCardClickListener? = null
    private var mUpdateCardsListener: OnUpdateCardsListener? = null
    private var mDeleteTaskListListener: OnDeleteTaskListListener? = null

    fun setCreateTaskListListener(createTaskListListener: OnCreateTaskListListener) {
        mCreateTaskListListener = createTaskListListener
    }

    fun setUpdateTaskListListener(updateTaskListListener: OnUpdateTaskListListener) {
        mUpdateTaskListListener = updateTaskListListener
    }

    fun setAddCardToTaskListListener(addCardToTaskListListener: OnAddCardToTaskListListener) {
        mAddCardToTaskListListener = addCardToTaskListListener
    }

    fun setCardClickListener(cardClickListener: OnCardClickListener) {
        mCardClickListener = cardClickListener
    }

    fun setUpdateCardsListener(updateCardsListener: OnUpdateCardsListener) {
        mUpdateCardsListener = updateCardsListener
    }

    fun setDeleteTaskListListener(deleteTaskListListener: OnDeleteTaskListListener) {
        mDeleteTaskListListener = deleteTaskListListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins((15.toDp()).toPx(), 0, (40.toDp()).toPx(), 0)
        view.layoutParams = layoutParams
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val model = list[position]
        if (position == list.size - 1) {
            holder.addTaskList.visibility = View.VISIBLE
            holder.taskItemLayout.visibility = View.GONE
        } else {
            holder.addTaskList.visibility = View.GONE
            holder.taskItemLayout.visibility = View.VISIBLE
        }

        holder.taskListTitle.text = model.title
        holder.addTaskList.setOnClickListener {
            holder.addTaskList.visibility = View.GONE
            holder.addTaskListView.visibility = View.VISIBLE
        }
        holder.closeListNameButton.setOnClickListener {
            holder.addTaskList.visibility = View.VISIBLE
            holder.addTaskListView.visibility = View.GONE
        }
        holder.doneListNameButton.setOnClickListener {
            val listName = holder.taskListNameEdit.text.toString()
            if (listName.isNotEmpty()) {
                mCreateTaskListListener?.onClick(listName)
            } else {
                Toast.makeText(context, R.string.please_enter_list_name, Toast.LENGTH_LONG).show()
            }
        }
        holder.editListNameButton.setOnClickListener {
            holder.editTaskListName.setText(model.title)
            holder.titleViewLayout.visibility = View.GONE
            holder.editTaskListNameView.visibility = View.VISIBLE
        }
        holder.closeEditableViewButton.setOnClickListener {
            holder.titleViewLayout.visibility = View.VISIBLE
            holder.editTaskListNameView.visibility = View.GONE
        }
        holder.doneEditListNameButton.setOnClickListener {
            val listName = holder.editTaskListName.text.toString()
            if (listName.isNotEmpty()) {
                mUpdateTaskListListener?.onClick(position, listName, model)
            } else {
                Toast.makeText(context, R.string.please_enter_list_name, Toast.LENGTH_LONG).show()
            }
        }
        holder.deleteListButton.setOnClickListener {
            alertDialogForDeleteList(position, model.title)
        }

        holder.addCard.setOnClickListener {
            holder.addCard.visibility = View.GONE
            holder.addCardView.visibility = View.VISIBLE
        }
        holder.closeCardNameButton.setOnClickListener {
            holder.addCard.visibility = View.VISIBLE
            holder.addCardView.visibility = View.GONE
        }
        holder.doneCardNameButton.setOnClickListener {
            val cardName = holder.cardNameEdit.text.toString()
            if (cardName.isNotEmpty()) {
                mAddCardToTaskListListener?.onClick(position, cardName)
            } else {
                Toast.makeText(context, R.string.please_enter_card_name, Toast.LENGTH_LONG).show()
            }
        }
        holder.cardList.layoutManager = LinearLayoutManager(context)
        holder.cardList.setHasFixedSize(true)
        val adapter = CardListItemsAdapter(context, model.cards, assignedMembersDetails)
        holder.cardList.adapter = adapter
        adapter.setOnClickListener(object : CardListItemsAdapter.OnClickListener {
            override fun onClick(cardPosition: Int) {
                mCardClickListener?.onClick(holder.adapterPosition, cardPosition)
            }
        })

        val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        holder.cardList.addItemDecoration(dividerItemDecoration)
        val helper = ItemTouchHelper(object : SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
            override fun onMove(
                recyclerView: RecyclerView,
                dragged: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val draggedPosition = dragged.adapterPosition
                val targetPosition = target.adapterPosition

                if (mPositionDraggedFrom == -1) {
                    mPositionDraggedFrom = draggedPosition
                }
                mPositionDraggedTo = targetPosition
                Collections.swap(list[holder.adapterPosition].cards, draggedPosition, targetPosition)
                adapter.notifyItemMoved(draggedPosition, targetPosition)
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) { }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                if (mPositionDraggedFrom != -1 && mPositionDraggedTo != -1 && mPositionDraggedFrom != mPositionDraggedTo) {
                    mUpdateCardsListener?.onClick(holder.adapterPosition, list[holder.adapterPosition].cards)
                }
                mPositionDraggedFrom = -1
                mPositionDraggedTo = -1
            }
        })
        helper.attachToRecyclerView(holder.cardList)
    }

    private fun alertDialogForDeleteList(position: Int, title: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.alert)
        builder.setMessage(context.getString(R.string.you_want_to_delete_task, title))
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton(R.string.yes) { dialogInterface, _ ->
            dialogInterface.dismiss()
            mDeleteTaskListListener?.onClick(position)
        }
        builder.setNegativeButton(R.string.no) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun Int.toDp() = (this / Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx() = (this * Resources.getSystem().displayMetrics.density).toInt()

    interface OnCreateTaskListListener {
        fun onClick(taskListName: String)
    }

    interface OnUpdateTaskListListener {
        fun onClick(position: Int, listName: String, model: Task)
    }

    interface OnAddCardToTaskListListener {
        fun onClick(position: Int, cardName: String)
    }

    interface OnCardClickListener {
        fun onClick(position: Int, cardPosition: Int)
    }

    interface OnUpdateCardsListener {
        fun onClick(position: Int, cards: ArrayList<Card>)
    }

    interface OnDeleteTaskListListener {
        fun onClick(position: Int)
    }
}