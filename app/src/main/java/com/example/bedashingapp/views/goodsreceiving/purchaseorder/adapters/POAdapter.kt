package com.example.bedashingapp.views.goodsreceiving.purchaseorder.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bedashingapp.R
import com.example.bedashingapp.data.model.remote.ItemPO
import com.example.bedashingapp.data.model.remote.PurchaseOrder
import com.example.bedashingapp.helper.DateUtilsApp
import com.example.bedashingapp.utils.OnItemClickListener

class POAdapter(
    private var poList: List<PurchaseOrder>,
    private var poItemsList: List<ItemPO>,
    private val activity: Activity,
    private var showDate: Boolean = false,
    private var mOnItemClickListener: OnItemClickListener<PurchaseOrder>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view: View? = null
        if (showDate) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_po_single_item_date, parent, false)
        }else{
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_po_single_item_vendor, parent, false)
        }
        return ViewHolder(view!!)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int = poList.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        private var poIDTextView = mView.findViewById<TextView>(R.id.tv_po_id)
        private var supplierTextView = mView.findViewById<TextView>(R.id.tv_supplier_name)
        private var linesTextView = mView.findViewById<TextView>(R.id.tv_lines)
        private var dueDateTextView = mView.findViewById<TextView>(R.id.tv_due_date)


        fun bind(result: Int) {
            val data = poList[result]

            poIDTextView.text = data.ID
            supplierTextView.text = data.Supplier.SupplierName[0].FormattedName
            linesTextView.text = poItemsList.filter { it.ParentObjectID == data.ObjectID && it.DeliveryStatusCode != "3" }.size.toString()

            dueDateTextView.text = (DateUtilsApp.getDateTimeFromMiliSecond(data.CreationDateTime.replace("/", "").replace("Date(", "" ).replace(")", "").toLong() , "dd/MM/yyyy")!!)

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

    fun updateList(list: List<PurchaseOrder>){
        poList = list
        notifyDataSetChanged()
    }


}