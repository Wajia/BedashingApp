package com.example.bedashingapp.views.stock_counting.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bedashingapp.R
import com.example.bedashingapp.data.model.remote.CustomObject

class InventoryStatusAdapter(
    private var mValues: List<CustomObject>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_inventory_status_single_item, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        var warehouseCodeTextView = mView.findViewById<TextView>(R.id.tv_warehouse_code)
        var inStockTextView = mView.findViewById<TextView>(R.id.tv_in_stock_quantity)


        fun bind(result: Int) {
            val data = mValues[result]

            warehouseCodeTextView.text = data.ItemWarehouseInfoCollection.WarehouseCode
            inStockTextView.text = String.format("%.1f", data.ItemWarehouseInfoCollection.InStock)


            itemView.setOnClickListener {
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun updateList(tempList: ArrayList<CustomObject>) {
        mValues = tempList
        notifyDataSetChanged()
    }

}