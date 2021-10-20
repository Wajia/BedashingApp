package com.example.bedashingapp.views.transferorder.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bedashingapp.R
import com.example.bedashingapp.data.model.remote.SelectedBin
import kotlinx.android.synthetic.main.list_bin_outbound_bottomsheet_single_item.view.*
import java.util.ArrayList

class OutboundBottomSheetBinAdapter(
    var context: Context,
    var list: ArrayList<SelectedBin>,
) : RecyclerView.Adapter<OutboundBottomSheetBinAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_bin_outbound_bottomsheet_single_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvBinId.text = item.ID
        holder.tvProposedQuantity.text = String.format("%.1f",item.PickedQuantity)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvBinId: TextView = itemView.tv_bin_id
        var tvProposedQuantity: TextView = itemView.tv_picked_qty
    }
}