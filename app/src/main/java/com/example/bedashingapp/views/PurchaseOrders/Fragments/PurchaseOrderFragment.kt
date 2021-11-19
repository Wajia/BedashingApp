package com.example.bedashingapp.views.PurchaseOrders.Fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bedashingapp.BaseFragment
import com.example.bedashingapp.MainActivity
import com.example.bedashingapp.R
import com.example.bedashingapp.data.model.db.ItemEntity
import com.example.bedashingapp.data.model.db.UOMEntity
import com.example.bedashingapp.data.model.local.Line
import com.example.bedashingapp.data.model.remote.*
import com.example.bedashingapp.utils.*
import com.example.bedashingapp.views.PurchaseOrders.Adapters.PurchaseOrderItemsAdapter
import com.example.bedashingapp.views.interfaces.SingleButtonListener
import com.example.bedashingapp.views.login.LoginActivity
import com.example.bedashingapp.views.stock_counting.InventoryStatusDialogFragment
import com.example.bedashingapp.views.stock_counting.ItemsDialogFragment
import com.example.bedashingapp.views.stock_counting.StockCountingFragment
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import com.sixlogics.flexspace.wrappers.NavigationWrapper
import kotlinx.android.synthetic.main.fragment_purchase_order.*
import kotlinx.android.synthetic.main.fragment_purchase_order.btn_add_item
import kotlinx.android.synthetic.main.fragment_purchase_order.btn_cancel
import kotlinx.android.synthetic.main.fragment_purchase_order.btn_check_status
import kotlinx.android.synthetic.main.fragment_purchase_order.btn_post
import kotlinx.android.synthetic.main.fragment_purchase_order.et_counted_quantity
import kotlinx.android.synthetic.main.fragment_purchase_order.et_doc_date
import kotlinx.android.synthetic.main.fragment_purchase_order.et_remarks
import kotlinx.android.synthetic.main.fragment_purchase_order.layout_item
import kotlinx.android.synthetic.main.fragment_purchase_order.root
import kotlinx.android.synthetic.main.fragment_purchase_order.spinner_uom
import kotlinx.android.synthetic.main.fragment_purchase_order.tv_selected_item_name
import kotlinx.android.synthetic.main.fragment_stock_counting.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PurchaseOrderFragment : BaseFragment(), View.OnClickListener,
    PurchaseOrderItemsAdapter.OnItemClickListener, SingleButtonListener {


    private var standardAveragePrice: Double = 0.0
    private val uomName: String = ""
    private var costingCode: String = ""
    private var costingCode2: String = ""
    private var costingCode3: String = ""
    private var uomCode: String = ""
    private var itemName: String = ""
    private var itemBarcode: String = ""
    private var itemCode: String = ""
    private var message: String = ""
    private var u_dept: String = ""
    private var u_prod_cat: String = ""
    private var uomAbsEntry: String = ""
    private var parameter: String = ""
    private var docNum: String = ""
    private var availableInStock: Double = 0.0
    lateinit var pOItemsAdapter: PurchaseOrderItemsAdapter
    private var selectedPositionForUpdate: Int = 0
    private var selectedItem: ItemEntity? = null
    private var uomsList: ArrayList<UOMEntity> = ArrayList()
    override fun getLayout(): Int {
        return R.layout.fragment_purchase_order
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        et_doc_date.setOnClickListener(this)
        et_req_date.setOnClickListener(this)
        et_due_date.setOnClickListener(this)
        btn_check_status.setOnClickListener(this)
        btn_add_item.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)
        btn_post.setOnClickListener(this)
        layout_item.setOnClickListener(this)
        iv_item_barcode.setOnClickListener(this)

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
        et_doc_date.setText(getCurrentTime("dd-MM-yyyy"))
        (context as MainActivity).mainActivityViewModel.getSelectedItems().clear()
        setRecyclerView()

    }

    private fun setRecyclerView() {
        pOItemsAdapter = PurchaseOrderItemsAdapter(
            requireContext(),
            (context as MainActivity).mainActivityViewModel.getSelectedItems(),
            this
        )
        rv_purchase_order_lines.adapter = pOItemsAdapter
        rv_purchase_order_lines.setHasFixedSize(true)

        rv_purchase_order_lines.layoutManager = LinearLayoutManager(context)
    }


    override fun onClick(view: View?) {
        when (view) {
            et_req_date -> {
                openDatePickerDialog(requireContext(), et_req_date)
            }
            et_doc_date -> {
                openDatePickerDialog(requireContext(), et_doc_date)
            }
            et_due_date -> {
                openDatePickerDialog(requireContext(), et_due_date)
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
                            pOItemsAdapter!!.notifyDataSetChanged()
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
                            costingCode,
                            costingCode2,
                            costingCode3,
                            uomCode,
                            availableInStock,
                            uomAbsEntry
                        )
                        btn_add_item.text = Constants.TEXT_ADD_ITEM
                        resetSelectedItemDetails()
                        pOItemsAdapter.notifyDataSetChanged()
                    }


                }

            }
            btn_cancel -> {

                requireActivity().onBackPressed()
            }
            btn_post -> {
                showConfirmationAlert(
                    requireContext(),
                    this,
                    resources.getString(R.string.post_doc),
                    "Post Document"
                )

            }
            layout_item -> {
                openItemSelectDialog()
            }
            iv_item_barcode -> {

                val integrator = IntentIntegrator.forSupportFragment(this)
                integrator.setOrientationLocked(true)
                integrator.captureActivity = PotraitCaptureActivityPO::class.java
                integrator.initiateScan()
            }
        }

    }

    private fun getInventoryStatus() {
        (context as MainActivity).mainActivityViewModel.getInventoryStatus(
            (context as MainActivity).sessionManager!!.getBaseURL(),
            (context as MainActivity).sessionManager!!.getCompany(),
            (context as MainActivity).sessionManager!!.getSessionId(),
            selectedItem!!.ItemCode
        ).observe(viewLifecycleOwner, Observer {
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

    private fun resetSelectedItemDetails() {
        selectedItem = null
        tv_selected_item_name.text = resources.getString(R.string.lbl_select_item)
        et_counted_quantity.setText("")
        et_qty.setText("")
        uomCode = ""
        costingCode3 = ""
        btn_check_status.alpha = 0.5f
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
        itemName = item.ItemName
        itemCode = item.ItemCode
        itemBarcode = item.BarCode
        costingCode = (context as MainActivity).sessionManager!!.getUserDfltRegion()
        costingCode2 = (context as MainActivity).sessionManager!!.getUserDfltStore()

        costingCode3 = if (item.ItemsGroupCode == 106) {
            item.U_Deprtmnt!!
        } else {
            ""
        }
        u_dept = item.U_Deprtmnt!!
        u_prod_cat = item.U_PrdctCat!!

        selectedItem = item
        tv_selected_item_name.text = item.ItemName

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


        //get Latest details of item
        checkSessionConnection("fetchQuantity")

    }

    private fun fetchUomsByUomGroupEntry(uomGroupEntry: String) {
        (context as MainActivity).mainActivityViewModel.getUomsByUomGroupEntry(uomGroupEntry)
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

        if (uomCode.isNotEmpty()) {
            //retain selected uom
            spinner_uom.setSelection(uomsList.indexOfFirst { it.Code == uomCode })
        }
    }

    private fun checkSessionConnection(purpose: String) {
        if (isConnectedToNetwork()) {
            (context as MainActivity).mainActivityViewModel.checkConnection(
                (context as MainActivity).sessionManager!!.getBaseURL(),
                (context as MainActivity).sessionManager!!.getCompany(),
                (context as MainActivity).sessionManager!!.getSessionId(),
                (context as MainActivity).sessionManager!!.getUserId()
            ).observe(viewLifecycleOwner, Observer {
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

    private fun getItemQuantityAndDetails() {
        (context as MainActivity).mainActivityViewModel.getItemPO(
            (context as MainActivity).sessionManager!!.getBaseURL(),
            (context as MainActivity).sessionManager!!.getCompany(),
            (context as MainActivity).sessionManager!!.getSessionId(),
            (context as MainActivity).sessionManager!!.getWareHouseID(),
            selectedItem!!.ItemCode
        ).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideProgressBar()

                        if (resource.data!!.value.isNotEmpty()) {
                            val item = resource.data.value.first()

                            standardAveragePrice =
                                item.ItemWarehouseInfoCollection.StandardAveragePrice.toDouble()
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


    class PotraitCaptureActivityPO : CaptureActivity()


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
            .observe(viewLifecycleOwner, Observer {
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

    private fun getItemByItemCode(itemCode: String, addedItem: Line) {
        (context as MainActivity).mainActivityViewModel.getItemByItemCode(itemCode)
            .observe(viewLifecycleOwner, Observer {
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

    private fun retainItemData(item: ItemEntity, addedItem: Line) {
        btn_add_item.text = Constants.TEXT_UPDATE_ITEM

        selectedItem = item
        tv_selected_item_name.text = item.ItemName

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
        fetchUomsByUomGroupEntry(item.UoMGroupEntry)

        //get Latest details of item
        checkSessionConnection("fetchQuantity")
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

            val postPurchaseOrderRequest = PostPurchaseOrderRequest(
                BranchID = (context as MainActivity).sessionManager!!.getUserBplid(),

                et_doc_date.text.toString().changeDateFormat("dd-MM-yyyy", "yyyy-MM-dd"),
                et_req_date.text.toString().changeDateFormat("dd-MM-yyyy", "yyyy-MM-dd"),
                et_due_date.text.toString().changeDateFormat("dd-MM-yyyy", "yyyy-MM-dd"),
                (context as MainActivity).sessionManager!!.getUserBranch(),
                (context as MainActivity).sessionManager!!.getUserBplid(),
                et_remarks.text.toString(),
                poLines,
                (context as MainActivity).sessionManager!!.getUserHeadOfficeCardCode(),
                (context as MainActivity).sessionManager!!.getUserHeadOfficeCardCode()
            )

            (context as MainActivity).mainActivityViewModel.savePurchaseOrderDocument(
                postPurchaseOrderRequest
            )
                .observe(viewLifecycleOwner, Observer {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                postPO(postPurchaseOrderRequest)

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

    fun postPO(payload: PostPurchaseOrderRequest) {


        (context as MainActivity).mainActivityViewModel.postPO(
            (context as MainActivity).sessionManager!!.getBaseURL(),
            (context as MainActivity).sessionManager!!.getCompany(),
            (context as MainActivity).sessionManager!!.getSessionId(),
            payload
        ).observe(viewLifecycleOwner, Observer {
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
                                        response.body()!!.DocEntry.toString()
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

    override fun onItemEditClick(position: Int, item: Line) {
        selectedPositionForUpdate = position
        getItemByItemCode(item.ItemCode!!, item)
    }

    override fun onItemDeleteClick(item: Line) {

        if (btn_add_item.text == Constants.TEXT_UPDATE_ITEM) {
            resetSelectedItemDetails()
        }
        (context as MainActivity).mainActivityViewModel.removeSelectedItem(item)
        pOItemsAdapter.notifyDataSetChanged()
    }

    override fun onButtonClick(type: String, position: Int) {
        when (type) {
            getString(R.string.yes) -> {
                NavigationWrapper.navigateToFragmentDashboard(true)
            }
            getString(R.string.post_document) -> {
                checkSessionConnection(getString(R.string.post_document))
            }
        }
    }
}