package com.example.bedashingapp.views.stock_counting.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bedashingapp.R
import com.example.bedashingapp.data.model.remote.InventoryCounting
import com.example.bedashingapp.helper.DateUtilsApp

class InventoryCountingAdapter(
    private var inventoryCountingList: List<InventoryCounting>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view: View? = null
        view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_inventory_countings_single_item, parent, false)

        return ViewHolder(view!!)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int = inventoryCountingList.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        private var docNumTextView = mView.findViewById<TextView>(R.id.tv_docNum)
        private var docDateTextView = mView.findViewById<TextView>(R.id.tv_doc_date)
        fun bind(result: Int) {
            val data = inventoryCountingList[result]

            docNumTextView.text = data.DocumentNumber.toString()
            docDateTextView.text =
                DateUtilsApp.convertDateFormat(data.CountDate, outputDateFormat = "dd-MM-yyyy")
        }

        init {
            mView.findViewById<TextView>(R.id.tv_doc_type).text = "Stock Counting"
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun updateList(list: List<InventoryCounting>) {
        inventoryCountingList = list
        notifyDataSetChanged()
    }


}