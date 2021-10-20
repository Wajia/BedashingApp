package com.example.bedashingapp.views.goodsreceiving.purchaseorder.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bedashingapp.R
import com.example.bedashingapp.data.model.db.LogisticEntity
import com.example.bedashingapp.utils.OnItemClickListener
import com.google.android.material.card.MaterialCardView

class POBinAdapter(
    private var binsList: List<LogisticEntity>,
    private var selectedBinCode: String?=null,
    private var quantityReceived: Double = 0.0,
    private var context: Context,
    private var mOnItemClickListener: OnItemClickListener<LogisticEntity>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_po_bins_single_item, parent, false)
        return ViewHolder(view!!)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int = binsList.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        private var binIDTextView = mView.findViewById<TextView>(R.id.tv_bin_id)
//        private var binNameTextView = mView.findViewById<TextView>(R.id.tv_bin_name)
        private var binQtyTextView = mView.findViewById<TextView>(R.id.tv_bin_quantity)
        private var cardView = mView.findViewById<MaterialCardView>(R.id.main_card_view)

        fun bind(result: Int) {
            val data = binsList[result]


            if(data.isSelected){
                cardView.strokeColor = context.resources.getColor(R.color.primvar)
                cardView.strokeWidth = 3
            }else{
                cardView.strokeColor = context.resources.getColor(R.color.unselected_item_stroke_color)
                cardView.strokeWidth = 1
            }

            binIDTextView.text = data.ID
            binQtyTextView.text = "0"

            selectedBinCode?.let{
                if(data.ID == selectedBinCode){
                    binQtyTextView.text = String.format("%.2f", quantityReceived)
                }
            }

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

    fun updateList(list: List<LogisticEntity>) {
        binsList = list
        notifyDataSetChanged()
    }
}