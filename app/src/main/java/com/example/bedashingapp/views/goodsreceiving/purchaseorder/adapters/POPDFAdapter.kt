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

class POPDFAdapter(
    private var poItemsList: List<ItemPO>,
    private var poItemDetailsList: List<ItemEntity>,
    private var context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_goods_receiving_pdf_single_item, parent, false)
        return ViewHolder(view!!)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int = poItemsList.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        private var poItemIDTextView = mView.findViewById<TextView>(R.id.tv_item_id)
        private var qtyReceivedTextView = mView.findViewById<TextView>(R.id.tv_qty_received)
        private var qtyOrderedTextView = mView.findViewById<TextView>(R.id.tv_qty_ordered)
        private var uomTextView = mView.findViewById<TextView>(R.id.tv_uom)

        fun bind(result: Int) {
            val data = poItemsList[result]
            val itemDetails = poItemDetailsList.find { it.InternalID == data.ProductID }!!

            poItemIDTextView.text = data.ProductID

            var qtyReceived = data.QuantityReceived
            if(qtyReceived > 0){
                if(data.UnitCode == Constants.CARTON_UNIT_CODE){
                    qtyReceived *= itemDetails.QuantityConversion[0].Quantity.toDouble()
                }
            }
            qtyReceivedTextView.text = String.format("%.2f", qtyReceived)

            qtyOrderedTextView.text = String.format("%.2f", data.Quantity.toDouble() - data.TotalDeliveredQuantity.toDouble())
            uomTextView.text = data.QuantityUnitCodeText


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

    fun updateList(list: List<ItemPO>) {
        poItemsList = list
        notifyDataSetChanged()
    }
}