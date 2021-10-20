package com.example.bedashingapp.views.transferorder.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bedashingapp.R
import com.example.bedashingapp.data.model.remote.TaskItem
import com.example.bedashingapp.utils.OnItemClickListener

class TransferSummaryItemAdapter(
    private var transferItemsList: List<TaskItem>,
    private var context: Context,
    private var mOnItemClickListener: OnItemClickListener<TaskItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_transfer_summary_single_item, parent, false)
        return ViewHolder(view!!)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int = transferItemsList.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        private var itemIDTextView = mView.findViewById<TextView>(R.id.tv_item_id)
        private var itemNameTextView = mView.findViewById<TextView>(R.id.tv_item_name)
        private var qtyPickedTextView = mView.findViewById<TextView>(R.id.tv_item_qty)
        private var deleteBtn = mView.findViewById<ImageView>(R.id.iv_delete)

        fun bind(result: Int) {
            val data = transferItemsList[result]



            itemIDTextView.text = data.ProductID
            itemNameTextView.text = data.ProductDescription

            var qtyPicked = 0.0
            for(bin in data.Bins){
                qtyPicked += bin.PickedQuantity
            }
            qtyPickedTextView.text = String.format("%.1f", qtyPicked) + " ${data.UOM}"

            deleteBtn.setOnClickListener {
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