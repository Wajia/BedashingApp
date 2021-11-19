package com.example.bedashingapp.views.GoodsReciveng.Dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.bedashingapp.MainActivity
import com.example.bedashingapp.R
import com.example.bedashingapp.data.model.db.ItemEntity
import com.example.bedashingapp.data.model.db.UOMEntity
import com.example.bedashingapp.views.interfaces.SingleButtonListener

import kotlinx.android.synthetic.main.itemsreceiving.*
import kotlinx.android.synthetic.main.itemsreceiving.view.*


class ItemReceivingDialog(
    var dialogListener: SingleButtonListener,
    var type: String,
    var index: Int,
    var uomsList: ArrayList<UOMEntity> = ArrayList(),
    var selectedItem: ItemEntity?
) : DialogFragment(),
    View.OnClickListener {
    private var uomCode: String = ""
    private var uomAbsEntry: String = ""
    private lateinit var rootView: View


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
        rootView = inflater.inflate(R.layout.itemsreceiving, container, false)
        init()
        return rootView
    }

    fun init() {
        rootView.btn_receive.setOnClickListener(this)
        rootView.et_remaining_qty.setText((context as MainActivity).mainActivityViewModel.getSelectedItems()[index].RemainingOpenQuantity.toString())
        rootView.et_original_qty.setText((context as MainActivity).mainActivityViewModel.getSelectedItems()[index].originalRemainingQuantity.toString())
        setupUomSpinner()
        rootView.spinner_uom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                (context as MainActivity).mainActivityViewModel.getSelectedItems()[index].UoMCode =
                    uomsList[position].Code
                (context as MainActivity).mainActivityViewModel.getSelectedItems()[index].UoMEntry =
                    uomsList[position].AbsEntry.toString()


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

    }


    private fun setupUomSpinner() {
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_row, uomsList)
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        rootView.spinner_uom.adapter = adapter

        if (uomCode.isNotEmpty()) {
            //retain selected uom
            rootView.spinner_uom.setSelection(uomsList.indexOfFirst { it.Code == (context as MainActivity).mainActivityViewModel.getSelectedItems()[index].UoMCode })
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            dialog.window!!
                .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            dialog.window!!
                .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        }
    }


    override fun onClick(p0: View?) {
        if (p0 == btn_receive) {
            if (rootView.et_recieved_qty.text.toString().isNotEmpty()) {
                if ((context as MainActivity).mainActivityViewModel.getSelectedItems()[index].originalRemainingQuantity < rootView.et_recieved_qty.text.toString()
                        .toDouble()
                ) {
                    Toast.makeText(
                        context,
                        "Entered quantity is greater than Remaining Open Quantity of PO!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    (context as MainActivity).mainActivityViewModel.getSelectedItems()[index].RemainingOpenQuantity =
                        ((context as MainActivity).mainActivityViewModel.getSelectedItems()[index].originalRemainingQuantity - et_recieved_qty.text.toString()
                            .toDouble())
                    (context as MainActivity).mainActivityViewModel.getSelectedItems()[index].CountedQuantity =
                        et_recieved_qty.text.toString().toDouble()
                    (context as MainActivity).mainActivityViewModel.getSelectedItems()[index].CostingCode =
                        (context as MainActivity).sessionManager!!.getUserDfltRegion()
                    (context as MainActivity).mainActivityViewModel.getSelectedItems()[index].CostingCode2 =
                        (context as MainActivity).sessionManager!!.getUserDfltStore()
                    (context as MainActivity).mainActivityViewModel.getSelectedItems()[index].BaseType =
                        "22"
                    (context as MainActivity).mainActivityViewModel.getSelectedItems()[index].BaseEntry =
                        (context as MainActivity).mainActivityViewModel.getSelectedItems()[index].DocEntry
                    if (selectedItem!!.ItemsGroupCode == 106) {
                        (context as MainActivity).mainActivityViewModel.getSelectedItems()[index].CostingCode3 =
                            selectedItem!!.U_Deprtmnt!!
                    } else {
                        (context as MainActivity).mainActivityViewModel.getSelectedItems()[index].CostingCode3 =
                            ""
                    }

                    (context as MainActivity).sessionManager!!.getUserDfltStore()
                    dialogListener.onButtonClick("update", index)
                    dismiss()
                }
            } else {
                Toast.makeText(
                    context,
                    "Please Enter Quantity",
                    Toast.LENGTH_LONG
                ).show()

            }

        }
    }
}

