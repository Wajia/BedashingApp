package com.example.bedashingapp.views.goodsreceiving.purchaseorder.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.bedashingapp.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.condition_goods_bottomsheet.*

class ConditionBottomsheet(var forCondition: Boolean = true, var title: String): BottomSheetDialogFragment() {

    var onDialogConfirmListener: OnDialogConfirmListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
            val verificationSheet = BottomSheetBehavior.from<FrameLayout?>(bottomSheet!!)
            verificationSheet.setState(BottomSheetBehavior.STATE_EXPANDED)
        }
        // Do something with your dialog like setContentView() or whatever
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.condition_goods_bottomsheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvTitle.text = title


        if(!forCondition){
            lbl_option_1.text = "Yes"
            lbl_option_2.text = "No"
        }

        layout_option_1.setOnClickListener{
            onDialogConfirmListener?.onSelect(lbl_option_1.text.toString())
        }
        layout_option_2.setOnClickListener{
            onDialogConfirmListener?.onSelect(lbl_option_2.text.toString())
        }

    }


    interface OnDialogConfirmListener {
        fun onSelect(optionSelectedValue: String)
        fun onButton2()
        fun onButton3()
        fun onCancel()
    }
}