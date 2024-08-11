package com.ivantrykosh.udemy_course.android14.projemanag.presenter.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ivantrykosh.udemy_course.android14.projemanag.R
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.SelectedMembers

class CardMemberListItemsAdapter(
    private val context: Context,
    private val list: List<SelectedMembers>,
    private val assignMembers: Boolean
) : RecyclerView.Adapter<CardMemberListItemsAdapter.CardMemberListViewHolder>() {

    class CardMemberListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val addMember: ImageView = view.findViewById(R.id.iv_add_member)
        val selectedMember: ImageView = view.findViewById(R.id.iv_selected_member_image)
    }

    private var onClickListener: OnClickListener? = null

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardMemberListViewHolder {
        return CardMemberListViewHolder(LayoutInflater.from(context).inflate(R.layout.item_card_selected_member, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CardMemberListViewHolder, position: Int) {
        val model = list[position]
        if (position == list.lastIndex && assignMembers) {
            holder.addMember.visibility = View.VISIBLE
            holder.selectedMember.visibility = View.GONE
        } else {
            holder.addMember.visibility = View.GONE
            holder.selectedMember.visibility = View.VISIBLE
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.selectedMember)
        }
        holder.itemView.setOnClickListener {
            onClickListener?.onClick()
        }
    }

    interface OnClickListener {
        fun onClick()
    }
}