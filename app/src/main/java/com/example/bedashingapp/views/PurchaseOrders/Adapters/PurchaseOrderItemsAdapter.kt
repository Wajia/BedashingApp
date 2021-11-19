package com.example.bedashingapp.views.PurchaseOrders.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bedashingapp.R
import com.example.bedashingapp.data.model.local.Line

import java.util.*

class PurchaseOrderItemsAdapter(
    var context: Context,
    var itemsList: ArrayList<Line>,
    var onItemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<PurchaseOrderItemsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_inventory_lines_single_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemsList[position]
        holder.itemNameTextView.text = item.ItemDescription
        holder.countedQuantityTextView.text = item.Quantity
        holder.uomTextView.text = item.UoMCode
        holder.editImageView.setOnClickListener {
            onItemClickListener.onItemEditClick(position, item)
        }
        holder.deleteImageView.setOnClickListener {
            onItemClickListener.onItemDeleteClick(item)
        }
    }

    override fun getItemCount(): Int {
        return itemsList.size

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemNameTextView = itemView.findViewById<TextView>(R.id.tv_item_name)
        var countedQuantityTextView = itemView.findViewById<TextView>(R.id.tv_counted_quantity)
        var uomTextView = itemView.findViewById<TextView>(R.id.tv_uom)
        var editImageView = itemView.findViewById<ImageView>(R.id.iv_edit)
        var deleteImageView = itemView.findViewById<ImageView>(R.id.iv_delete)


    }

    interface OnItemClickListener {
        fun onItemEditClick(position: Int, data: Line)
        fun onItemDeleteClick(item: Line)
    }

}