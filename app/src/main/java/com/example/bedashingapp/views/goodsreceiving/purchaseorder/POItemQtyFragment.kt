package com.example.bedashingapp.views.goodsreceiving.purchaseorder

import android.os.Bundle
import android.view.MotionEvent
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.example.bedashingapp.BaseFragment
import com.example.bedashingapp.R
import com.example.bedashingapp.data.api.ApiHelper
import com.example.bedashingapp.data.api.RetrofitBuilder
import com.example.bedashingapp.data.model.remote.GlobalTradeItemNumber
import com.example.bedashingapp.helper.ViewModelFactory
import com.example.bedashingapp.utils.Constants
import com.example.bedashingapp.viewmodel.MainActivityViewModel
import com.example.bedashingapp.views.goodsreceiving.purchaseorder.bottomsheet.ConditionBottomsheet
import com.example.bedashingapp.views.goodsreceiving.purchaseorder.bottomsheet.UOMBottomsheet
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_po_item_qty.*


/**
 * A simple [Fragment] subclass.
 * Use the [POItemQtyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class POItemQtyFragment : BaseFragment() {
    private lateinit var mainActivityViewModel: MainActivityViewModel

    private var selectedUOMCode: String = Constants.CARTON_UNIT_CODE

    override fun getLayout(): Int {
        return R.layout.fragment_po_item_qty
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var scannedBarcode: GlobalTradeItemNumber? = null
        var poItem = mainActivityViewModel.getPOItems()
            .find { it.ID == mainActivityViewModel.selectedLineNum }

        et_qty_remaining.setText(
            String.format(
                "%.3f",
                poItem?.Quantity?.toDouble()!! - poItem?.TotalDeliveredQuantity.toDouble()
            ) + " " + poItem?.QuantityUnitCode
        )

        if(arguments?.getBoolean("forUpdate")!!){
            et_qty.setText(String.format("%.1f", poItem.QuantityReceived))
            et_condition_goods.setText(poItem.ConditionGoods)
            et_packing_condition.setText(poItem.PackingCondition)
            et_style_match.setText(poItem.StyleMatch)
            if(poItem.UnitCode == Constants.EACH_UNIT_CODE){
                et_uom.setText("Each")
                selectedUOMCode = Constants.EACH_UNIT_CODE
            }else{
                et_uom.setText("Carton")
                selectedUOMCode = Constants.CARTON_UNIT_CODE
            }

        }else {

            et_qty.setText(String.format("%.1f", poItem.QuantityReceived + 1))

            var selectedItemDetails = mainActivityViewModel.getSelectedItem()

//            scannedBarcode =
//                selectedItemDetails?.GlobalTradeItemNumber?.find { it.ID == mainActivityViewModel.scannedBarcode }
//            et_uom.setText(scannedBarcode?.QuantityTypeCodeText)

            //by default carton will be selected
            et_uom.setText("Carton")
            selectedUOMCode = Constants.CARTON_UNIT_CODE

        }


        //for quality control
        et_condition_goods.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                if (event?.action == MotionEvent.ACTION_UP) {
                    openBottomsheet(true, et_condition_goods, "Item Status")
                    return true;
                }
                return false;
            }
        })

        et_packing_condition.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                if (event?.action == MotionEvent.ACTION_UP) {
                    openBottomsheet(true, et_packing_condition, "Packing Condition")
                    return true;
                }
                return false;
            }
        })

        et_style_match.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                if (event?.action == MotionEvent.ACTION_UP) {
                    openBottomsheet(false, et_style_match, "Style Match")
                    return true;
                }
                return false;
            }
        })

        et_uom.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                if (event?.action == MotionEvent.ACTION_UP) {
                    openUOMBottomsheet(et_uom)
                    return true;
                }
                return false;
            }
        })

        btn_enter_qty.setOnClickListener {
            if (validate()) {
                if(!arguments?.getBoolean("forUpdate")!!) {
                    mainActivityViewModel.enterQuantity(
                        et_qty.text.toString().toDouble(),
                        selectedUOMCode,
                        et_condition_goods.text.toString(),
                        et_style_match.text.toString(),
                        et_packing_condition.text.toString()
                    )
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_po_bin_selection, Bundle())
                }else{
                    mainActivityViewModel.enterQuantity(
                        et_qty.text.toString().toDouble(),
                        selectedUOMCode,
                        et_condition_goods.text.toString(),
                        et_style_match.text.toString(),
                        et_packing_condition.text.toString()
                    )
                    requireActivity().onBackPressed()
                }

            }
        }

    }

    private fun openBottomsheet(forCondition: Boolean, view: TextInputEditText, title: String) {
        var bottomsheet = ConditionBottomsheet(forCondition, title)
        val onDialogConfirmListener: ConditionBottomsheet.OnDialogConfirmListener = object :
            ConditionBottomsheet.OnDialogConfirmListener {
            override fun onSelect(optionSelectedValue: String) {
                bottomsheet.dismiss()
                view.setText(optionSelectedValue)
            }

            override fun onButton2() {
            }

            override fun onButton3() {
            }

            override fun onCancel() {
            }

        }

        bottomsheet.onDialogConfirmListener = onDialogConfirmListener
        bottomsheet.show(requireActivity().supportFragmentManager, bottomsheet.tag)
    }

    private fun openUOMBottomsheet(view: TextInputEditText) {
        var bottomsheet = UOMBottomsheet()
        val onDialogConfirmListener: UOMBottomsheet.OnDialogConfirmListener = object :
            UOMBottomsheet.OnDialogConfirmListener {
            override fun onSelect(optionSelectedValue: String) {
                bottomsheet.dismiss()
                view.setText(optionSelectedValue)
                selectedUOMCode = if(optionSelectedValue == "Each"){
                    Constants.EACH_UNIT_CODE
                }else{
                    Constants.CARTON_UNIT_CODE
                }
            }

            override fun onButton2() {
            }

            override fun onButton3() {
            }

            override fun onCancel() {
            }

        }

        bottomsheet.onDialogConfirmListener = onDialogConfirmListener
        bottomsheet.show(requireActivity().supportFragmentManager, bottomsheet.tag)
    }

    private fun validate(): Boolean {
        if (et_uom.text.toString().isEmpty()) {
            showToastShort("UOM not found")
            return false
        }
        if (et_qty.text.toString()
                .isEmpty() || et_qty.text.toString() == "." || et_qty.text.toString()
                .toDouble() <= 0.0
        ) {
            showToastShort("Please enter valid quantity")
            return false
        }
        if (et_condition_goods.text.toString().isEmpty()) {
            showToastShort("Please select condition of goods")
            return false
        }
        if (et_style_match.text.toString().isEmpty()) {
            showToastShort("Please select style match")
            return false
        }
        if (et_packing_condition.text.toString().isEmpty()) {
            showToastShort("Please select packing condition")
            return false
        }
        return true
    }

    private fun setUpViewModel() {
        mainActivityViewModel = ViewModelProviders.of(
            requireActivity(),
            ViewModelFactory(
                ApiHelper(RetrofitBuilder.getApiService("dynamic ip here")),
                requireActivity().application
            )
        ).get(MainActivityViewModel::class.java)
    }

}