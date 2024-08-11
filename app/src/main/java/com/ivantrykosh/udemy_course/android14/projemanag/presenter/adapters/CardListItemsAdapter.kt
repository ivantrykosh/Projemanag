package com.ivantrykosh.udemy_course.android14.projemanag.presenter.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ivantrykosh.udemy_course.android14.projemanag.R
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Card
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.SelectedMembers
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User

class CardListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Card>,
    private val assignedMembersDetails: List<User>,
): RecyclerView.Adapter<CardListItemsAdapter.CardItemViewHolder>() {

    class CardItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val labelColor: View = view.findViewById(R.id.view_label_color)
        val cardName: TextView = view.findViewById(R.id.tv_card_name)
        val selectedMembers: RecyclerView = view.findViewById(R.id.rv_card_selected_members_list)
    }

    private var onClickListener: OnClickListener? = null

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardItemViewHolder {
        return CardItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item_card, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CardItemViewHolder, position: Int) {
        val model = list[position]
        if (model.labelColor.isNotEmpty()) {
            holder.labelColor.visibility = View.VISIBLE
            holder.labelColor.setBackgroundColor(Color.parseColor(model.labelColor))
        } else {
            holder.labelColor.visibility = View.GONE
        }

        holder.cardName.text = model.name

        if (assignedMembersDetails.isNotEmpty()) {
            val selectedMembersList = assignedMembersDetails
                .filter { assignedMember ->
                    model.assignedTo.contains(assignedMember.id)
                }.map { assignedMember ->
                    SelectedMembers(assignedMember.id, assignedMember.image)
                }

            if (selectedMembersList.isNotEmpty()) {
                if (selectedMembersList.size == 1 && selectedMembersList[0].id == model.createdBy) {
                    holder.selectedMembers.visibility = View.GONE
                } else {
                    holder.selectedMembers.visibility = View.VISIBLE
                    holder.selectedMembers.layoutManager = GridLayoutManager(context, 4)
                    val adapter = CardMemberListItemsAdapter(context, selectedMembersList, false)
                    holder.selectedMembers.adapter = adapter
                    adapter.setOnClickListener(object : CardMemberListItemsAdapter.OnClickListener {
                        override fun onClick() {
                            onClickListener?.onClick(holder.adapterPosition)
                        }
                    })
                }
            } else {
                holder.selectedMembers.visibility = View.GONE
            }
        }
        holder.itemView.setOnClickListener {
            onClickListener?.onClick(position)
        }
    }

    interface OnClickListener {
        fun onClick(cardPosition: Int)
    }
}