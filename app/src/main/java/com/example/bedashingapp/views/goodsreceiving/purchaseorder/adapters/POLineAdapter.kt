package com.example.bedashingapp.views.goodsreceiving.purchaseorder.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bedashingapp.R
import com.example.bedashingapp.data.model.db.ItemEntity
import com.example.bedashingapp.data.model.remote.ItemPO
import com.example.bedashingapp.utils.Constants
import com.example.bedashingapp.utils.OnItemClickListener
import com.google.android.material.card.MaterialCardView

class POLineAdapter(
    private var poItemsList: List<ItemPO>,
    private var poItemDetailsList: List<ItemEntity>,
    private var context: Context,
    private var mOnItemClickListener: OnItemClickListener<ItemPO>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_po_line_single_item, parent, false)
        return ViewHolder(view!!)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int = poItemsList.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        private var poItemIDTextView = mView.findViewById<TextView>(R.id.tv_item_id)
        private var qtyReceivedTextView = mView.findViewById<TextView>(R.id.tv_item_qty_received)
        private var qtyRemainingTextView = mView.findViewById<TextView>(R.id.tv_item_qty_remaining)
        private var cardView = mView.findViewById<MaterialCardView>(R.id.main_card_view)

        fun bind(result: Int) {
            val data = poItemsList[result]
            val itemDetails = poItemDetailsList.find { it.InternalID == data.ProductID }!!

            if(data.isSelected){
                cardView.setCardBackgroundColor(context.resources.getColor(R.color.selected_item_bg_color))
            }else{
//                cardView.strokeColor = context.resources.getColor(R.color.unselected_item_stroke_color)
                cardView.setCardBackgroundColor(context.resources.getColor(R.color.colorWhite))
            }

            poItemIDTextView.text = data.ProductID
            qtyReceivedTextView.text = String.format("%.2f", data.QuantityReceived)

            var qtyReceived = data.QuantityReceived
            if(qtyReceived > 0){
                if(data.UnitCode == Constants.CARTON_UNIT_CODE){
                    qtyReceived *= itemDetails.QuantityConversion[0].Quantity.toDouble()
                }
            }
            var qtyRemaining = (data.Quantity.toDouble() - data.TotalDeliveredQuantity.toDouble()) - qtyReceived
            if(qtyRemaining > 0) {
                qtyRemainingTextView.text = String.format(
                    "%.2f",
                    qtyRemaining
                ) + " " + data.QuantityUnitCodeText
            }else{
                qtyRemainingTextView.text = "0.00" + " " + data.QuantityUnitCodeText
            }


            itemView.setOnClickListener {
                mOnItemClickListener.onClicked(itemView, position, "update", data)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun updateList(list: List<ItemPO>) {
        poItemsList = list
        notifyDataSetChanged()
    }
}