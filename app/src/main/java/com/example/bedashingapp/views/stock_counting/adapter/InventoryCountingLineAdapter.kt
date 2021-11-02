package com.example.bedashingapp.views.stock_counting.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bedashingapp.R
import com.example.bedashingapp.data.model.local.Line
import com.example.bedashingapp.data.model.remote.InventoryCounting
import com.example.bedashingapp.helper.DateUtilsApp
import com.example.bedashingapp.utils.Constants
import com.example.bedashingapp.utils.OnItemClickListener

data class InventoryCountingLineAdapter(
    private var lines: List<Line>,
    private var mOnItemClickListener: OnItemClickListener<Line>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view: View? = null
        view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_inventory_lines_single_item, parent, false)

        return ViewHolder(view!!)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int = lines.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        private var itemNameTextView = mView.findViewById<TextView>(R.id.tv_item_name)
        private var countedQuantityTextView = mView.findViewById<TextView>(R.id.tv_counted_quantity)
        private var uomTextView = mView.findViewById<TextView>(R.id.tv_uom)
        private var editImageView = mView.findViewById<ImageView>(R.id.iv_edit)
        private var deleteImageView = mView.findViewById<ImageView>(R.id.iv_delete)


        fun bind(result: Int) {
            val data = lines[result]

            itemNameTextView.text = data.ItemDescription
            countedQuantityTextView.text = String.format("%.1f", data.CountedQuantity)
            uomTextView.text = data.UoMCode

            editImageView.setOnClickListener {
                mOnItemClickListener.onClicked(itemView, position, Constants.EDIT, data)
            }

            deleteImageView.setOnClickListener {
                mOnItemClickListener.onClicked(itemView, position, Constants.DELETE, data)
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun updateList(list: List<Line>) {
        lines = list
        notifyDataSetChanged()
    }


}
