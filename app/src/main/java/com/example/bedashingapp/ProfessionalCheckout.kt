package com.example.bedashingapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bedashingapp.data.model.db.ItemEntity
import com.example.bedashingapp.data.model.db.UOMEntity
import com.example.bedashingapp.data.model.local.Line
import com.example.bedashingapp.data.model.remote.AddInventoryCountingResponse
import com.example.bedashingapp.data.model.remote.Customer
import com.example.bedashingapp.data.model.remote.DocumentLine
import com.example.bedashingapp.data.model.remote.PurchaseDeliveryNotesRequest
import com.example.bedashingapp.utils.*
import com.example.bedashingapp.views.PurchaseOrders.Adapters.PurchaseOrderItemsAdapter
import com.example.bedashingapp.views.login.LoginActivity
import com.example.bedashingapp.views.stock_counting.ItemsDialogFragment
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import kotlinx.android.synthetic.main.fragment_goods_receipt.*
import kotlinx.android.synthetic.main.fragment_professional_checkout.*
import kotlinx.android.synthetic.main.fragment_professional_checkout.btn_add_item
import kotlinx.android.synthetic.main.fragment_professional_checkout.btn_cancel
import kotlinx.android.synthetic.main.fragment_professional_checkout.btn_check_status
import kotlinx.android.synthetic.main.fragment_professional_checkout.btn_post
import kotlinx.android.synthetic.main.fragment_professional_checkout.ed_select_item
import kotlinx.android.synthetic.main.fragment_professional_checkout.et_counted_quantity
import kotlinx.android.synthetic.main.fragment_professional_checkout.et_doc_date
import kotlinx.android.synthetic.main.fragment_professional_checkout.et_due_date
import kotlinx.android.synthetic.main.fragment_professional_checkout.et_qty
import kotlinx.android.synthetic.main.fragment_professional_checkout.iv_item_barcode
import kotlinx.android.synthetic.main.fragment_professional_checkout.spinner_uom
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


import java.util.*


class ProfessionalCheckout : BaseFragment(), View.OnClickListener,
    PurchaseOrderItemsAdapter.OnItemClickListener {

    private var customerCode: String = ""
    private var uomAbsEntry: String = ""
    private var selectedPositionForUpdate: Int = 0
    private var itemCode: String = ""
    private var itemName: String = ""
    private var itemBarcode: String = ""
    private var uProductCat: String = ""
    private var uomCode: String = ""
    private var costingCode3: String = ""
    private var availableInStock: Double = 0.0
    val customerList = ArrayList<Customer>()
    private var selectedItem: ItemEntity? = null
    private var uomsList: ArrayList<UOMEntity> = ArrayList()
    private var standardAveragePrice: Double = 0.0
    lateinit var pcItemsAdapter: PurchaseOrderItemsAdapter
    override fun getLayout(): Int {
        return R.layout.fragment_professional_checkout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        et_doc_date.setText(getCurrentTime("dd-MM-yyyy"))
        customerList.add(Customer("", "Please select a customer"))
        customerList.add(Customer("C00001", "DASHING INTERNATIONAL - HO"))
        customerList.add(Customer("C00074", "PROFESSIONAL CHECKOUT-AUH"))
        customerList.add(Customer("C00075", "PROFESSIONAL CHECKOUT-DXB"))


        et_doc_date.setOnClickListener(this)
        et_due_date.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)
        ed_select_item.setOnClickListener(this)
        iv_item_barcode.setOnClickListener(this)
        btn_check_status.setOnClickListener(this)
        btn_add_item.setOnClickListener(this)
        btn_post.setOnClickListener(this)
        setupCustomerSpinner()

        (context as MainActivity).mainActivityViewModel.getSelectedItems().clear()
        spinner_uom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                uomCode = uomsList[position].Code
                uomAbsEntry = uomsList[position].AbsEntry.toString()

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        spinner_customer.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                customerCode = customerList[position].code

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        (context as MainActivity).mainActivityViewModel.clearSelectedItems()
        setRecyclerView()
    }

    private fun setRecyclerView() {
        pcItemsAdapter = PurchaseOrderItemsAdapter(
            requireContext(),
            (context as MainActivity).mainActivityViewModel.getSelectedItems(),
            this
        )
        rv_inventory_counting_lines.adapter = pcItemsAdapter
        rv_inventory_counting_lines.setHasFixedSize(true)

        rv_inventory_counting_lines.layoutManager = LinearLayoutManager(context)
    }

    private fun setupCustomerSpinner() {

        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_row, customerList)
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinner_customer.adapter = adapter
        spinner_customer.setSelection(0)
    }

    override fun onClick(view: View?) {
        when (view) {
            et_doc_date -> {
                openDatePickerDialog(requireContext(), et_doc_date)
            }
            et_due_date -> {
                openDatePickerDialog(requireContext(), et_due_date)
            }
            btn_post -> {
                if (postingValidation())
                    if (postingValidation()) {
                        checkSessionConnection("post_document")
                    }

            }
            btn_cancel -> {
                requireActivity().onBackPressed()
            }
            ed_select_item -> {
                openItemSelectDialog()
            }
            iv_item_barcode -> {
                initiateScanFragment(PortraitCaptureActivityPC::class.java)
            }
            btn_check_status -> {
                if (btn_check_status.alpha == 1.0f) {
                    checkSessionConnection("checkInventoryStatus")
                }
            }
            btn_add_item -> {

                if (validateItemData()) {
                    if (btn_add_item.text == Constants.TEXT_UPDATE_ITEM) {
                        if ((context as MainActivity).mainActivityViewModel.updatePurchaseOrderLine(
                                selectedItem!!,
                                et_qty.text.toString().toDouble(),
                                et_counted_quantity.text.toString().toDouble(),
                                costingCode3,
                                uomCode,
                                availableInStock,
                                selectedPositionForUpdate
                            )
                        ) {
                            //reset details
                            resetSelectedItemDetails()
                            btn_add_item.text = Constants.TEXT_ADD_ITEM
                        } else {
                            showToastShort("Another item already exists with selected item and uom.")
                        }
                    } else {
                        (context as MainActivity).mainActivityViewModel.addPurchaseOrderLine(
                            selectedItem!!,
                            (context as MainActivity).sessionManager!!.getWareHouseID(),
                            standardAveragePrice,
                            et_qty.text.toString().toDouble(),
                            et_counted_quantity.text.toString().toDouble(),
                            (context as MainActivity).sessionManager!!.getUserDfltRegion(),
                            (context as MainActivity).sessionManager!!.getUserDfltStore(),
                            costingCode3,
                            uomCode,
                            availableInStock,
                            uomAbsEntry
                        )
                        btn_add_item.text = Constants.TEXT_ADD_ITEM
                        resetSelectedItemDetails()
                    }
                    pcItemsAdapter.notifyDataSetChanged()

                }

            }
        }
    }

    fun postingValidation(): Boolean {
        if (customerCode.isEmpty()) {
            context?.let { showToastLong(it.getString(R.string.select_customer)) }
            return false
        } else if (et_due_date.text.toString().isEmpty()) {
            context?.let { showToastLong(it.getString(R.string.select_due_date)) }
            return false
        }
        return true
    }

    private fun resetSelectedItemDetails() {
        selectedItem = null
        ed_select_item.setText(resources.getString(R.string.lbl_select_item))
        et_counted_quantity.setText("")
        et_qty.setText("")
        uomCode = ""
        costingCode3 = ""
        btn_check_status.alpha = 0.5f
    }

    private fun validateItemData(): Boolean {
        when {
            itemBarcode.isBlank() || itemName.isBlank() -> {
                showToastShort(getString(R.string.select_item))
                return false
            }
            et_qty.text.toString().isBlank() -> {
                showToastShort(getString(R.string.enter_qty))
                return false
            }
            uomCode.isBlank() -> {
                showToastShort(getString(R.string.select_uom))
                return false
            }

            (context as MainActivity).sessionManager!!.getUserDfltRegion().isBlank() -> {
                showToastLong(resources.getString(R.string.no_default_region_msg))
            }
            (context as MainActivity).sessionManager!!.getUserDfltStore().isBlank() -> {
                showToastLong(resources.getString(R.string.no_default_store_msg))
            }
            else -> return true
        }
        return true
    }

    private fun checkSessionConnection(purpose: String) {
        if (isConnectedToNetwork()) {
            (context as MainActivity).mainActivityViewModel.checkConnection(
                (context as MainActivity).sessionManager!!.getBaseURL(),
                (context as MainActivity).sessionManager!!.getCompany(),
                (context as MainActivity).sessionManager!!.getSessionId(),
                (context as MainActivity).sessionManager!!.getUserId()
            ).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            hideProgressBar()
                            when (purpose) {
                                "checkInventoryStatus" -> {
                                    getInventoryStatus()
                                }
                                "fetchQuantity" -> {
                                    getItemQuantityAndDetails()
                                }
                                getString(R.string.post_document) -> {
                                    saveDocument()
                                }
                            }
                        }
                        Status.LOADING -> {
                            showProgressBar("", "")
                        }
                        Status.ERROR -> {
                            hideProgressBar()
                            (context as MainActivity).sessionManager!!.putIsLoggedIn(false)
                            (context as MainActivity).sessionManager!!.putPreviousPassword(
                                (context as MainActivity).sessionManager!!.getCurrentPassword()
                            )
                            (context as MainActivity).sessionManager!!.putPreviousUserName(
                                (context as MainActivity).sessionManager!!.getCurrentUserName()
                            )

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

    private fun getInventoryStatus() {
        (context as MainActivity).mainActivityViewModel.getInventoryStatus(
            (context as MainActivity).sessionManager!!.getBaseURL(),
            (context as MainActivity).sessionManager!!.getCompany(),
            (context as MainActivity).sessionManager!!.getSessionId(),
            selectedItem!!.ItemCode
        ).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideProgressBar()
                        openInventoryStatusDialog(resource.data!!.value, requireContext())
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
        btn_add_item.text = Constants.TEXT_ADD_ITEM

        selectedItem = item

        itemName = item.ItemName
        itemCode = item.ItemCode
        itemBarcode = item.BarCode
        ed_select_item.setText(item.ItemName)

        btn_check_status.alpha = 1.0f
        et_counted_quantity.setText("")
        et_qty.setText("")

        availableInStock = item.InStock!!

        costingCode3 = if (item.ItemsGroupCode == 106) {
            item.U_Deprtmnt!!
        } else {
            ""
        }

        //set Uoms in spinner
        uomCode = ""
        fetchUomsByUomGroupEntry(item.UoMGroupEntry)
        if (uomsList.isNotEmpty()) {
            setupUomSpinner()
        }
        //get Latest details of item
        checkSessionConnection("fetchQuantity")
    }

    private fun setupUomSpinner() {
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_row, uomsList)
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinner_uom.adapter = adapter

        if (uomCode.isNotEmpty()) {
            //retain selected uom
            spinner_uom.setSelection(uomsList.indexOfFirst { it.Code == uomCode })
        }
    }

    fun fetchUomsByUomGroupEntry(
        uomGroupEntry: String,
    ) {
        uomsList.clear()
        (context as MainActivity).mainActivityViewModel.getUomsByUomGroupEntry(uomGroupEntry)
            .observe(this, androidx.lifecycle.Observer {

                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {

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

    private fun getItemQuantityAndDetails() {
        (context as MainActivity).mainActivityViewModel.getItem(
            (context as MainActivity).sessionManager!!.getBaseURL(),
            (context as MainActivity).sessionManager!!.getCompany(),
            (context as MainActivity).sessionManager!!.getSessionId(),
            (context as MainActivity).sessionManager!!.getWareHouseID(),
            selectedItem!!.ItemCode
        ).observe(this, androidx.lifecycle.Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideProgressBar()

                        if (resource.data!!.value.isNotEmpty()) {
                            val item = resource.data.value.first()


                            //    item.ItemWarehouseInfoCollection.StandardAveragePrice.toDouble()
                            availableInStock = item.ItemWarehouseInfoCollection.InStock

                            if (availableInStock == 0.0) {
                                et_counted_quantity.setText("0")
                            } else {
                                et_counted_quantity.setText(availableInStock.toString())
                            }
                            if (item.Items.ItemsGroupCode == 106) {
                                costingCode3 = item.Items.U_Deprtmnt
                            } else {
                                costingCode3 = ""
                            }
                            uProductCat = item.Items.U_PrdctCat


                        }

                    }
                    Status.LOADING -> {
                        showProgressBar("", "")
                    }
                    Status.ERROR -> {
                        hideProgressBar()
                        showToastLong(resource.message!!)
                    }
                }
            }
        })
    }

    private fun retainItemData(item: ItemEntity, addedItem: Line) {
        btn_add_item.text = Constants.TEXT_UPDATE_ITEM

        selectedItem = item

        btn_check_status.alpha = 1.0f
        et_counted_quantity.setText(addedItem.CountedQuantity.toString())
        et_qty.setText(addedItem.Quantity)

        costingCode3 = if (item.ItemsGroupCode == 106) {
            item.U_Deprtmnt!!
        } else {
            ""
        }

        //set Uoms in spinner
        uomCode = addedItem.UoMCode!!

    }

    override fun onItemEditClick(position: Int, data: Line) {
        selectedPositionForUpdate = position
        getItemByItemCode(data.ItemCode!!, data)
    }

    private fun getItemByItemCode(itemCode: String, addedItem: Line) {
        (context as MainActivity).mainActivityViewModel.getItemByItemCode(itemCode)
            .observe(viewLifecycleOwner, {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            retainItemData(resource.data!!, addedItem)
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

    class PortraitCaptureActivityPC : CaptureActivity()

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

    private fun searchByBarcode(barcode: String) {
        (context as MainActivity).mainActivityViewModel.getItemByBarcode(barcode)
            .observe(viewLifecycleOwner, {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            if (resource.data != null) {
                                setItemData(resource.data)
                            } else {
                                showToastLong("No item found!")
                            }
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

    private fun saveDocument() {
        if (isConnectedToNetwork()) {
            //first create payload

            val poLines: ArrayList<DocumentLine> = ArrayList()
            val temp = (context as MainActivity).mainActivityViewModel.getSelectedItems()
            for (item in temp) {
                poLines.add(
                    DocumentLine(
                        ItemCode = item.ItemCode,
                        WarehouseCode = item.WarehouseCode,
                        Quantity = item.Quantity.toDouble(),
                        CostingCode = (context as MainActivity).sessionManager!!.getUserDfltRegion(),
                        CostingCode2 = (context as MainActivity).sessionManager!!.getUserDfltStore(),
                        CostingCode3 = item.CostingCode3,
                        UoMEntry = item.UoMEntry,
                        UnitPrice = item.UnitPrice.toDouble(),
                        LineStatus = ""
                    )
                )
            }
            val tempItems = (context as MainActivity).mainActivityViewModel.getSelectedItems()
                .filter { it.BaseType == "22" }.toCollection(
                    ArrayList()
                )
            if (tempItems.isEmpty()) {
                showToastLong("Enter at least one Item to receive!")
                return
            }
            val postPurchaseOrderRequest = PurchaseDeliveryNotesRequest(
                DocDate = et_doc_date.text.toString().changeDateFormat("dd-MM-yyyy", "yyyy-MM-dd"),
                DocDueDate = et_due_date.text.toString()
                    .changeDateFormat("dd-MM-yyyy", "yyyy-MM-dd"),
                BPL_IDAssignedToInvoice = (context as MainActivity).sessionManager!!.getUserBplid(),
                tempItems,
                U_DocNo = (context as MainActivity).sessionManager!!.getUserHeadOfficeCardCode(),
                CardCode = (context as MainActivity).sessionManager!!.getUserHeadOfficeCardCode()
            )

            (context as MainActivity).mainActivityViewModel.saveGoodRecieptDocument(
                postPurchaseOrderRequest
            )
                .observe(viewLifecycleOwner, {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                postPC(postPurchaseOrderRequest)

                            }
                            Status.LOADING -> {
                                showProgressBar("", "Posting Document...")
                            }
                            Status.ERROR -> {
                                hideProgressBar()
                                showToastLong(resource.message!!)
                            }
                        }
                    }
                })
        } else {
            showToastLong(resources.getString(R.string.network_not_connected_msg))
        }
    }

    private fun postPC(payload: PurchaseDeliveryNotesRequest) {


        (context as MainActivity).mainActivityViewModel.GoodsReciept(
            (context as MainActivity).sessionManager!!.getBaseURL(),
            (context as MainActivity).sessionManager!!.getCompany(),
            (context as MainActivity).sessionManager!!.getSessionId(),
            (context as MainActivity).sessionManager!!.getUserBranchName(),
            (context as MainActivity).sessionManager!!.getUserHeadOfficeCardCode(),
            payload
        ).observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.enqueue(object : Callback<AddInventoryCountingResponse> {
                            override fun onResponse(
                                call: Call<AddInventoryCountingResponse>,
                                response: Response<AddInventoryCountingResponse>
                            ) {
                                hideProgressBar()
                                if (response.errorBody() == null) {
                                    (requireActivity() as MainActivity).updateStatusOfDocument(
                                        (context as MainActivity).mainActivityViewModel.lastDocumentSavedID,
                                        Constants.SYNCED,
                                        "",
                                        ""
                                    )
                                    showSnackBar(
                                        "Document has been posted successfully.",
                                        root,
                                        R.id.nav_dashboard
                                    )
                                } else {
                                    val jsonObject = JSONObject(response.errorBody()!!.string())

                                    (requireActivity() as MainActivity).updateStatusOfDocument(
                                        (context as MainActivity).mainActivityViewModel.lastDocumentSavedID,
                                        Constants.FAILED,
                                        jsonObject.getJSONObject("error").toString(),
                                        (context as MainActivity).mainActivityViewModel.lastDocumentSavedID
                                    )
                                    showToastLong(
                                        jsonObject.getJSONObject("error").getJSONObject("message")
                                            .getString("value")
                                    )
                                }
                            }

                            override fun onFailure(
                                call: Call<AddInventoryCountingResponse>,
                                t: Throwable
                            ) {
                                hideProgressBar()
                                showToastLong(t.message!!)
                                (requireActivity() as MainActivity).updateStatusOfDocument(
                                    (requireActivity() as MainActivity).mainActivityViewModel.lastDocumentSavedID,
                                    Constants.FAILED,
                                    t.message!!,
                                    (requireActivity() as MainActivity).mainActivityViewModel.lastDocumentSavedID
                                )
                            }
                        })
                    }
                    Status.LOADING -> {
                    }
                    Status.ERROR -> {
                        hideProgressBar()
                        showToastLong(resource.message!!)
                        (requireActivity() as MainActivity).updateStatusOfDocument(
                            (requireActivity() as MainActivity).mainActivityViewModel.lastDocumentSavedID,
                            Constants.FAILED,
                            resource.message,
                            (requireActivity() as MainActivity).mainActivityViewModel.lastDocumentSavedID
                        )
                    }
                }
            }
        })
    }


    override fun onItemDeleteClick(item: Line) {

        if (btn_add_item.text == Constants.TEXT_UPDATE_ITEM) {
            resetSelectedItemDetails()
        }

        (context as MainActivity).mainActivityViewModel.removeSelectedItem(item)
        pcItemsAdapter.notifyDataSetChanged()
    }


}