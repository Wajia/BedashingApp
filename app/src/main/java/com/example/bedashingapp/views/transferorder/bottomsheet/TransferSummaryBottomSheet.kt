package com.example.bedashingapp.views.goodsreceiving.purchaseorder.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bedashingapp.R
import com.example.bedashingapp.data.model.remote.TaskItem
import com.example.bedashingapp.views.transferorder.adapter.OutboundBottomSheetBinAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.outbound_bottom_sheet.view.*

class TransferSummaryBottomSheet(
    var transferSummaryButtonListener: TransferSummaryButtonListener,
    var taskItem: TaskItem
) :
    BottomSheetDialogFragment() {
    private lateinit var rootView: View
    private lateinit var tasksBinAdapter: OutboundBottomSheetBinAdapter
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.outbound_bottom_sheet, container, false)
        init()
        return rootView
    }

    private fun init() {
        rootView.btn_delete.setOnClickListener {
            transferSummaryButtonListener.onButtonClick(taskItem)
            this.dismiss()
        }
        rootView.btn_ok.setOnClickListener {
            this.dismiss()
        }
        rootView.tv_item_code.text = taskItem.ProductID
        rootView.tv_uom.text = taskItem.UOM
        rootView.tv_item_name.text = taskItem.ProductDescription
        rootView.tv_quantity.text = getQuantity().toString()
        rootView.rv_bins.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        tasksBinAdapter = OutboundBottomSheetBinAdapter(requireActivity(), taskItem.Bins)
        rootView.rv_bins.adapter = tasksBinAdapter
    }

    private fun getQuantity(): Double {
        var sum = 0.0;
        for (bin in taskItem.Bins) {
            sum += bin.PickedQuantity
        }
        return sum
    }

    interface TransferSummaryButtonListener {
        fun onButtonClick(taskItem: TaskItem)
    }
}