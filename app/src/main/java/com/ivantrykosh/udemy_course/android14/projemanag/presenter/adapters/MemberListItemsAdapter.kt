package com.ivantrykosh.udemy_course.android14.projemanag.presenter.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ivantrykosh.udemy_course.android14.projemanag.R
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import de.hdodenhof.circleimageview.CircleImageView

class MemberListItemsAdapter(
    private val context: Context,
    private var list: MutableList<User>
) : RecyclerView.Adapter<MemberListItemsAdapter.MemberItemViewHolder>() {

    class MemberItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val memberImage: CircleImageView = view.findViewById(R.id.iv_member_image)
        val memberName: TextView = view.findViewById(R.id.tv_member_name)
        val memberEmail: TextView = view.findViewById(R.id.tv_member_email)
        val selectedMemberImage: ImageView = view.findViewById(R.id.iv_selected_member)
    }

    private var onClickListener: OnClickListener? = null

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberItemViewHolder {
        return MemberItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item_member, parent, false))
    }

    override fun onBindViewHolder(holder: MemberItemViewHolder, position: Int) {
        val model = list[position]

        Glide
            .with(context)
            .load(model.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(holder.memberImage)

        holder.memberName.text = model.name
        holder.memberEmail.text = model.email

        if (model.selected) {
            holder.selectedMemberImage.visibility = View.VISIBLE
        } else {
            holder.selectedMemberImage.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            val action = if (model.selected) OnClickListener.Action.SELECT else OnClickListener.Action.UNSELECT
            onClickListener?.onClick(position, model, action)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClickListener {
        fun onClick(position: Int, user: User, action: Action)

        enum class Action {
            SELECT,
            UNSELECT
        }
    }
}