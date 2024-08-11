package com.ivantrykosh.udemy_course.android14.projemanag.presenter.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.ivantrykosh.udemy_course.android14.projemanag.R
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board
import de.hdodenhof.circleimageview.CircleImageView

class BoardItemsAdapter(
    private val context: Context,
    private val list: MutableList<Board>
): RecyclerView.Adapter<BoardItemsAdapter.BoardViewHolder>() {

    class BoardViewHolder(view: View): ViewHolder(view) {
        val boardImage: CircleImageView = view.findViewById(R.id.iv_board_image)
        val boardName: TextView = view.findViewById(R.id.tv_name)
        val boardCreatedBy: TextView = view.findViewById(R.id.tv_created_by)
    }

    private var onClickListener: OnClickListener? = null

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        return BoardViewHolder(LayoutInflater.from(context).inflate(R.layout.item_board, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val model = list[position]
        Glide
            .with(context)
            .load(model.image)
            .centerCrop()
            .placeholder(R.drawable.ic_board_place_holder)
            .into(holder.boardImage)
        holder.boardName.text = model.name
        holder.boardCreatedBy.text = model.createdBy
        holder.itemView.setOnClickListener {
            onClickListener?.onClick(position, model)
        }
    }

    interface OnClickListener {
        fun onClick(position: Int, model: Board)
    }
}