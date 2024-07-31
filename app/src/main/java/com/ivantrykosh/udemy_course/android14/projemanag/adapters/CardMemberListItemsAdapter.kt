package com.ivantrykosh.udemy_course.android14.projemanag.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ivantrykosh.udemy_course.android14.projemanag.R
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.SelectedMembers

open class CardMemberListItemsAdapter(
    private val context: Context,
    private val list: ArrayList<SelectedMembers>,
    private val assignMembers: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener: OnClickListener? = null

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CardMemberListViewHolder(LayoutInflater.from(context).inflate(R.layout.item_card_selected_member, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is CardMemberListViewHolder) {
            if (position == list.lastIndex && assignMembers) {
                holder.itemView.findViewById<ImageView>(R.id.iv_add_member).visibility = View.VISIBLE
                holder.itemView.findViewById<ImageView>(R.id.iv_selected_member_image).visibility = View.GONE
            } else {
                holder.itemView.findViewById<ImageView>(R.id.iv_add_member).visibility = View.GONE
                holder.itemView.findViewById<ImageView>(R.id.iv_selected_member_image).visibility = View.VISIBLE
                Glide
                    .with(context)
                    .load(model.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(holder.itemView.findViewById(R.id.iv_selected_member_image))
            }
            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick()
                }
            }
        }
    }

    class CardMemberListViewHolder(view: View) : RecyclerView.ViewHolder(view)

    interface OnClickListener {
        fun onClick()
    }
}