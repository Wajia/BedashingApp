package com.example.bedashingapp.views.transferorder.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bedashingapp.R
import com.example.bedashingapp.data.model.remote.TaskItem
import com.example.bedashingapp.utils.OnItemClickListener
import com.google.android.material.card.MaterialCardView

class TransferLineAdapter(
    private var transferItemsList: List<TaskItem>,
    private var context: Context,
    private var mOnItemClickListener: OnItemClickListener<TaskItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_transfer_lines_single_item, parent, false)
        return ViewHolder(view!!)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int = transferItemsList.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        private var itemIDTextView = mView.findViewById<TextView>(R.id.tv_item_id)
        private var qtyProposedTextView = mView.findViewById<TextView>(R.id.tv_proposed_qty)
        private var qtyPickedTextView = mView.findViewById<TextView>(R.id.tv_picked_qty)
        private var cardView = mView.findViewById<MaterialCardView>(R.id.main_card_view)

        fun bind(result: Int) {
            val data = transferItemsList[result]

            if(data.isSelected){
                cardView.setCardBackgroundColor(context.resources.getColor(R.color.selected_item_bg_color))
            }else{
//                cardView.strokeColor = context.resources.getColor(R.color.unselected_item_stroke_color)
                cardView.setCardBackgroundColor(context.resources.getColor(R.color.colorWhite))
            }

            itemIDTextView.text = data.ProductID
            qtyProposedTextView.text = String.format("%.1f", data.OpenQuantity) + " ${data.UOM}"

            var qtyPicked = 0.0
            for(bin in data.Bins){
                qtyPicked += bin.PickedQuantity
            }
            qtyPickedTextView.text = String.format("%.1f", qtyPicked) + " ${data.UOM}"

            itemView.setOnClickListener {
                mOnItemClickListener.onClicked(itemView, position, "", data)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun updateList(list: List<TaskItem>) {
        transferItemsList = list
        notifyDataSetChanged()
    }
}