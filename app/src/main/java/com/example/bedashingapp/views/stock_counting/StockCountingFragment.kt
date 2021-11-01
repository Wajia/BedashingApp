package com.example.bedashingapp.views.stock_counting

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bedashingapp.BaseFragment
import com.example.bedashingapp.R
import com.example.bedashingapp.data.api.ApiHelper
import com.example.bedashingapp.data.api.RetrofitBuilder
import com.example.bedashingapp.data.model.db.ItemEntity
import com.example.bedashingapp.data.model.db.UOMEntity
import com.example.bedashingapp.data.model.local.Line
import com.example.bedashingapp.data.model.remote.CustomObject
import com.example.bedashingapp.helper.DateUtilsApp
import com.example.bedashingapp.helper.SessionManager
import com.example.bedashingapp.helper.ViewModelFactory
import com.example.bedashingapp.utils.Constants
import com.example.bedashingapp.utils.OnItemClickListener
import com.example.bedashingapp.utils.Status
import com.example.bedashingapp.viewmodel.MainActivityViewModel
import com.example.bedashingapp.views.login.LoginActivity
import com.example.bedashingapp.views.stock_counting.adapter.InventoryCountingLineAdapter
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import kotlinx.android.synthetic.main.fragment_stock_counting.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.cos

class StockCountingFragment : BaseFragment() {

    private lateinit var mainActivityViewModel: MainActivityViewModel
    private var sessionManager: SessionManager? = null

    private var selectedItem: ItemEntity? = null
    private var uomsList: ArrayList<UOMEntity> = ArrayList()


    private var costingCode3: String = ""
    private var availableInStock: Double = 0.0
    private var uomCode: String = ""

    override fun getLayout(): Int {
        return R.layout.fragment_stock_counting
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(requireContext())
        setUpViewModel()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments?.containsKey("fromDraft")!!) {
            //retain data
        } else {
            //initialize new
            setRecyclerView()

            //set today's date
            et_doc_date.setText(
                DateUtilsApp.getUTCFormattedDateTimeString(
                    SimpleDateFormat("dd-MM-yyyy"),
                    Calendar.getInstance().time
                )
            )
        }


        et_doc_date.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                if (event?.action == MotionEvent.ACTION_UP) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        openDatePickerDialog()
                    }
                    return true;
                }
                return false;
            }
        })

        layout_item.setOnClickListener {
            openItemSelectDialog()
        }

        btn_check_status.setOnClickListener {
            if (it.alpha == 1.0f) {
                checkSessionConnection("checkInventoryStatus")
            }
        }

        iv_barcode.setOnClickListener {
            val integrator = IntentIntegrator(requireActivity())
            integrator.setOrientationLocked(true)
            integrator.captureActivity = PortraitCaptureActivity::class.java
            integrator.initiateScan()
        }


        et_counted_quantity.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() == "."){
                    et_counted_quantity.setText("")
                    et_variance.setText("")
                }else if(s.toString().isEmpty()){
                    et_variance.setText("")
                }else{
                    if(selectedItem != null){
                        et_variance.setText(String.format("%.1f", s.toString().toDouble() - availableInStock))
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        spinner_uom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                uomCode = uomsList[position].Code
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        btn_cancel.setOnClickListener {
            requireActivity().onBackPressed()
        }

        btn_add_item.setOnClickListener {
            if(validateAddItem()){
                mainActivityViewModel.addInventoryCountingLine(
                    selectedItem!!,
                    sessionManager!!.getWareHouseID(),
                    et_counted_quantity.text.toString().toDouble(),
                    et_variance.text.toString().toDouble(),
                    sessionManager!!.getUserDfltRegion(),
                    sessionManager!!.getUserDfltStore(),
                    costingCode3,
                    uomCode,
                    availableInStock
                )

                //reset details
                resetSelectedItemDetails()

                adapter!!.notifyDataSetChanged()
            }
        }
    }

    private var adapter: InventoryCountingLineAdapter? = null
    private fun setRecyclerView() {
        adapter = InventoryCountingLineAdapter(mainActivityViewModel.getSelectedItems(), mOnItemClickListener)
        rv_inventory_counting_lines.adapter = adapter
        rv_inventory_counting_lines.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        rv_inventory_counting_lines.layoutManager = layoutManager
    }

    private var mOnItemClickListener: OnItemClickListener<Line> = object : OnItemClickListener<Line>(){
        override fun onClicked(view: View?, position: Int, type: String?, data: Line?) {
            if(type == Constants.DELETE){
                mainActivityViewModel.removeSelectedItem(data!!)
                adapter!!.notifyDataSetChanged()
            }
        }

    }

    private fun resetSelectedItemDetails(){
        selectedItem = null
        tv_selected_item_name.text = resources.getString(R.string.lbl_select_item)
        et_counted_quantity.setText("")
        et_variance.setText("")
        uomCode = ""
        costingCode3 = ""
        btn_check_status.alpha = 0.5f
    }

    private fun checkSessionConnection(purpose: String) {
        if (isConnectedToNetwork()) {
            mainActivityViewModel.checkConnection(
                sessionManager!!.getBaseURL(),
                sessionManager!!.getCompany(),
                sessionManager!!.getSessionId(),
                sessionManager!!.getUserId()
            ).observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            if (purpose == "checkInventoryStatus") {
                                getInventoryStatus()
                            }else{
                                getItemQuantityAndDetails()
                            }
                        }
                        Status.LOADING -> {
                            showProgressBar("", "")
                        }
                        Status.ERROR -> {
                            hideProgressBar()
                            sessionManager!!.putIsLoggedIn(false)
                            sessionManager!!.putPreviousPassword(sessionManager!!.getCurrentPassword())
                            sessionManager!!.putPreviousUserName(sessionManager!!.getCurrentUserName())

                            startActivity(Intent(requireContext(), LoginActivity::class.java))
                            requireActivity().finishAffinity()
                        }
                    }
                }
            })
        } else {
            showToastLong(resources.getString(R.string.network_not_connected_msg))
        }
    }

    private fun getItemQuantityAndDetails(){
        mainActivityViewModel.getItem(
            sessionManager!!.getBaseURL(),
            sessionManager!!.getCompany(),
            sessionManager!!.getSessionId(),
            sessionManager!!.getWareHouseID(),
            selectedItem!!.ItemCode
        ).observe(viewLifecycleOwner, Observer {
            it?.let{resource ->
                when(resource.status){
                    Status.SUCCESS->{
                        hideProgressBar()

                        if(resource.data!!.value.isNotEmpty()){
                            val item = resource.data.value.first()
                            availableInStock = item.ItemWarehouseInfoCollection.InStock

                            if(item.Items.ItemsGroupCode == 106){
                                costingCode3 = item.Items.U_Deprtmnt
                            }else{
                                costingCode3 = ""
                            }
                        }

                    }
                    Status.LOADING->{
                        showProgressBar("","")
                    }
                    Status.ERROR->{
                        hideProgressBar()
                        showToastLong(resource.message!!)
                    }
                }
            }
        })
    }

    private fun getInventoryStatus() {
        mainActivityViewModel.getInventoryStatus(
            sessionManager!!.getBaseURL(),
            sessionManager!!.getCompany(),
            sessionManager!!.getSessionId(),
            selectedItem!!.ItemCode
        ).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideProgressBar()
                        openInventoryStatusDialog(resource.data!!.value)
                    }
                    Status.LOADING -> {
                        showProgressBar("", "Getting Details...")
                    }
                    Status.ERROR -> {
                        hideProgressBar()
                        showToastLong(resource.message!!)
                    }
                }
            }
        })
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

    private fun openInventoryStatusDialog(data: List<CustomObject>) {
        val dialog = InventoryStatusDialogFragment(data)
        dialog.isCancelable = true
        dialog.show(requireActivity().supportFragmentManager, dialog.tag)
    }

    private fun openItemSelectDialog() {
        val itemSelectDialogFragment = ItemsDialogFragment()
        itemSelectDialogFragment.isCancelable = true

        itemSelectDialogFragment.mOnItemClickListener =
            object : ItemsDialogFragment.OnItemClickListener {
                override fun onItemClick(item: ItemEntity) {
                    setItemData(item)
                }
            }
        itemSelectDialogFragment.show(
            requireActivity().supportFragmentManager,
            itemSelectDialogFragment.tag
        )
    }

    private fun setItemData(item: ItemEntity) {
        selectedItem = item
        tv_selected_item_name.text = item.ItemName

        btn_check_status.alpha = 1.0f
        et_counted_quantity.setText("")
        et_variance.setText("")

        availableInStock = item.InStock!!

        costingCode3 = if(item.ItemsGroupCode == 106){
            item.U_Deprtmnt!!
        }else{
            ""
        }

        //set Uoms in spinner
        uomCode = ""
        fetchUomsByUomGroupEntry(item.UoMGroupEntry)

        //get Latest details of item
        checkSessionConnection("fetchQuantity")
    }

    private fun fetchUomsByUomGroupEntry(uomGroupEntry: String) {
        mainActivityViewModel.getUomsByUomGroupEntry(uomGroupEntry)
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            uomsList.clear()
                            uomsList.addAll(resource.data as ArrayList)
                            setupUomSpinner()
                        }
                        Status.LOADING -> {

                        }
                        Status.ERROR -> {
                            showToastLong(resource.message!!)
                        }
                    }
                }
            })
    }

    private fun setupUomSpinner() {
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_row, uomsList)
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinner_uom.adapter = adapter
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun openDatePickerDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val pickerDialog = DatePickerDialog(
            requireContext(),
            R.style.datepicker,
            { view, year, monthOfYear, dayOfMonth ->
                et_doc_date.setText(
                    "${String.format("%02d", dayOfMonth)}-${
                        String.format(
                            "%02d",
                            monthOfYear
                        )
                    }-$year"
                )
            },
            year,
            month,
            day
        )

        pickerDialog.show()
    }


    private fun validateAddItem(): Boolean{
        if(selectedItem == null){
            showToastShort("Please select an item first!")
            return false
        }
        if(et_counted_quantity.text.toString().isEmpty()){
            showToastShort("Please enter counted quantity")
            return false
        }
        if(uomCode.isEmpty()){
            showToastShort("Please select uom")
            return  false
        }
        if(sessionManager!!.getUserDfltRegion().isEmpty()){
            showToastLong(resources.getString(R.string.no_default_region_msg))
            return false
        }
        if(sessionManager!!.getUserDfltStore().isEmpty()){
            showToastLong(resources.getString(R.string.no_default_store_msg))
            return false
        }

        return true
    }

    private fun searchByBarcode(barcode: String) {
        mainActivityViewModel.getItemByBarcode(barcode).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when(resource.status){
                    Status.SUCCESS->{
                        if(resource.data != null){
                            setItemData(resource.data)
                        }else{
                            showToastLong("No item found!")
                        }
                    }
                    Status.LOADING->{

                    }
                    Status.ERROR->{
                        showToastLong(resource.message!!)
                    }
                }
            }
        })
    }


    class PortraitCaptureActivity : CaptureActivity()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                searchByBarcode(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


}