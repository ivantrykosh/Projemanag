package com.ivantrykosh.udemy_course.android14.projemanag.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.ivantrykosh.udemy_course.android14.projemanag.R
import com.ivantrykosh.udemy_course.android14.projemanag.model.Board

open class BoardItemsAdapter(private val context: Context, private val list: MutableList<Board>):
    RecyclerView.Adapter<ViewHolder>() {

    inner class BoardViewHolder(view: View): ViewHolder(view)

    private var onClickListener: OnClickListener? = null

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return BoardViewHolder(LayoutInflater.from(context).inflate(R.layout.item_board, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]
        if (holder is BoardViewHolder) {
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(holder.itemView.findViewById(R.id.iv_board_image))
            holder.itemView.findViewById<TextView>(R.id.tv_name).text = model.name
            holder.itemView.findViewById<TextView>(R.id.tv_created_by).text = model.createdBy
            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    interface OnClickListener {
        fun onClick(position: Int, model: Board)
    }
}