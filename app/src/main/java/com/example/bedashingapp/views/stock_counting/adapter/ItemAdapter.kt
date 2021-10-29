package com.example.bedashingapp.views.stock_counting.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bedashingapp.R
import com.example.bedashingapp.data.model.db.ItemEntity

class ItemAdapter(
    private var mValues: List<ItemEntity>,
    private val activity: Activity,
    onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //    private var screenWidth: Float
    public var mOnItemClickListener: OnItemClickListener? = null

    init {
        mOnItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_items_single_item, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        var itemNameTextView = mView.findViewById<TextView>(R.id.tv_item_name)
        var itemCodeTextView = mView.findViewById<TextView>(R.id.tv_item_code)


        fun bind(result: Int) {
            val data = mValues[result]

            itemCodeTextView.text = data.ItemCode
            itemNameTextView.text = data.ItemName


            itemView.setOnClickListener {
                mOnItemClickListener?.onItemClick(data)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun updateList(tempList: ArrayList<ItemEntity>) {
        mValues = tempList
        notifyDataSetChanged()
    }


    interface OnItemClickListener {
        fun onItemClick(item: ItemEntity)
    }

}