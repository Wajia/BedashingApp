package com.example.bedashingapp.views.GoodsReciveng.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bedashingapp.MainActivity
import com.example.bedashingapp.R
import com.example.bedashingapp.data.model.local.Line
import com.example.bedashingapp.data.model.remote.PurchaseOder
import com.sixlogics.flexspace.wrappers.NavigationWrapper

import java.util.*

class PurchaseOrderAdapter(
    var context: Context,
    var poList: ArrayList<PurchaseOder>,
) :
    RecyclerView.Adapter<PurchaseOrderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_inventory_countings_single_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = poList[position]
        holder.tvDocDate.text = item.DocDate
        holder.tvDocNumber.text = item.DocNum.toString()
        item.DocEntry
        holder.itemView.setOnClickListener {
            (context as MainActivity).mainActivityViewModel.poNumber = item.DocNum
            NavigationWrapper.navigateToFragmentGoodsReceipt(   )
        }
    }

    override fun getItemCount(): Int {
        return poList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvDocNumber: TextView = itemView.findViewById(R.id.tv_docNum)
        var tvDocDate: TextView = itemView.findViewById(R.id.tv_doc_date)

        init {
            itemView.findViewById<TextView>(R.id.tv_doc_type).text = context.getString(R.string.po)
        }
    }


}