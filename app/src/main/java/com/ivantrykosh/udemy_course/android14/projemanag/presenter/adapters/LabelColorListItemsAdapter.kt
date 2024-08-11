package com.ivantrykosh.udemy_course.android14.projemanag.presenter.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ivantrykosh.udemy_course.android14.projemanag.R

class LabelColorListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<String>,
    private val mSelectedColor: String
): RecyclerView.Adapter<LabelColorListItemsAdapter.ColorItemViewHolder>() {

    class ColorItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val mainView: View = view.findViewById(R.id.view_main)
        val selectedColorImage: ImageView = view.findViewById(R.id.iv_selected_color)
    }


    private var onItemClickListener: OnItemClickListener? = null

    fun setOnClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorItemViewHolder {
        return ColorItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item_label_color, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ColorItemViewHolder, position: Int) {
        val item = list[position]
        holder.mainView.setBackgroundColor(Color.parseColor(item))
        if (item == mSelectedColor) {
            holder.selectedColorImage.visibility = View.VISIBLE
        } else {
            holder.selectedColorImage.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.onClick(position, item)
        }
    }

    interface OnItemClickListener {
        fun onClick(position: Int, color: String)
    }
}