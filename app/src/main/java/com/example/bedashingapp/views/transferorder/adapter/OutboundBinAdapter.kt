package com.example.bedashingapp.views.transferorder.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bedashingapp.R
import com.example.bedashingapp.data.model.remote.SelectedBin
import com.example.bedashingapp.utils.OnItemClickListener

class OutboundBinAdapter(
    private var binsList: List<SelectedBin>,
    private var context: Context,
    private var mOnItemClickListener: OnItemClickListener<SelectedBin>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_outbound_bins_single_item, parent, false)
        return ViewHolder(view!!)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int = binsList.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        private var binIDTextView = mView.findViewById<TextView>(R.id.tv_bin_id)
        private var pickedQtyEditText = mView.findViewById<EditText>(R.id.et_picked_qty)
        private var availableQtyTextView = mView.findViewById<TextView>(R.id.tv_bin_available_qty)
        private var actionImageView = mView.findViewById<ImageView>(R.id.iv_action)
        fun bind(result: Int) {
            val data = binsList[result]

            binIDTextView.text = data.ID
            availableQtyTextView.text = String.format("%.1f", data.Quantity)
            if(data.isSelected){
                actionImageView.visibility = View.VISIBLE
                if(data.PickedQuantity > 0.0) {
                    pickedQtyEditText.setText(data.PickedQuantity.toInt().toString())
                }
                pickedQtyEditText.isEnabled = true
            }else{
                actionImageView.visibility = View.INVISIBLE
                pickedQtyEditText.isEnabled = false
            }

            pickedQtyEditText.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(s!!.isNotEmpty()) {
                        data.PickedQuantity = s.toString().toDouble()
                    }else{
                        data.PickedQuantity = 0.0
                    }
                    if(data.PickedQuantity > data.Quantity){
                        Toast.makeText(context, "Picked Quantity cannot be greater than Bin Quantity.", Toast.LENGTH_SHORT).show()
                        data.PickedQuantity = data.Quantity
                        pickedQtyEditText.setText(data.Quantity.toInt().toString())
                    }
                    mOnItemClickListener.onClicked(itemView, position, "update", data)
                }

                override fun afterTextChanged(s: Editable?) {
                }

            })

            itemView.setOnClickListener {
                if(!data.isSelected) {
                    mOnItemClickListener.onClicked(itemView, position, "select", data)
                }else{
                    pickedQtyEditText.text.clear()
                    mOnItemClickListener.onClicked(itemView, position, "unSelect", data)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun updateList(list: List<SelectedBin>) {
        binsList = list
        notifyDataSetChanged()
    }
}