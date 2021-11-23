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

import com.example.bedashingapp.data.model.remote.DocumentLine
import com.example.bedashingapp.data.model.remote.PurchaseOder
import com.example.bedashingapp.utils.gone
import com.example.bedashingapp.utils.visible
import com.example.bedashingapp.views.interfaces.SingleButtonListener
import com.sixlogics.flexspace.wrappers.NavigationWrapper

import java.util.*

class OpenPurchaseOrderItemAdapter
    (
    var context: Context,
    var poList: ArrayList<Line>, var singleButtonListener: SingleButtonListener
) :
    RecyclerView.Adapter<OpenPurchaseOrderItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_po_line_single_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = poList[position]
        holder.tvItemCode.text = item.ItemCode
        holder.tvRemOpenQty.text = item.RemainingOpenQuantity.toString()
    //    holder.tvOriginalRemQty.text = item.originalRemainingQuantity.toString()
        holder.tvQty.text = item.Quantity
        holder.tvUomCode.text = item.UoMCode
        holder.imgEdit.setOnClickListener {
            singleButtonListener.onButtonClick("editPO", position)
        }
        holder.imgDelete.setOnClickListener {
            singleButtonListener.onButtonClick("delete", position)
        }
        if (item.BaseType == "22") {
            holder.imgPurchase.visible()
        } else {
            holder.imgPurchase.gone()
        }

    }

    override fun getItemCount(): Int {
        return poList.size
    }

    fun updateList(list: ArrayList<Line>) {
        poList = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvItemCode: TextView = itemView.findViewById(R.id.tv_item_name)
        var tvQty: TextView = itemView.findViewById(R.id.tv_item_qty_received)
        var tvRemOpenQty: TextView = itemView.findViewById(R.id.tv_item_qty_remaining)
     //   var tvOriginalRemQty: TextView = itemView.findViewById(R.id.tv_item_original_remaining)
        var tvUomCode: TextView = itemView.findViewById(R.id.tv_uom)
        var imgPurchase: ImageView = itemView.findViewById(R.id.img_item_changed)
        var imgDelete: ImageView = itemView.findViewById(R.id.iv_delete)
        var imgEdit: ImageView = itemView.findViewById(R.id.iv_edit)
    }


}