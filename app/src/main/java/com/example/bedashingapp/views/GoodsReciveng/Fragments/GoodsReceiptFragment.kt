package com.example.bedashingapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bedashingapp.data.model.db.ItemEntity
import com.example.bedashingapp.data.model.db.UOMEntity
import com.example.bedashingapp.data.model.local.Line
import com.example.bedashingapp.data.model.remote.*
import com.example.bedashingapp.utils.*

import com.example.bedashingapp.views.GoodsReciveng.Adapters.OpenPurchaseOrderItemAdapter
import com.example.bedashingapp.views.GoodsReciveng.Dialogs.ItemReceivingDialog
import com.example.bedashingapp.views.PurchaseOrders.Adapters.PurchaseOrderItemsAdapter
import com.example.bedashingapp.views.PurchaseOrders.Fragments.PurchaseOrderFragment
import com.example.bedashingapp.views.interfaces.SingleButtonListener
import com.example.bedashingapp.views.login.LoginActivity
import com.example.bedashingapp.views.stock_counting.InventoryStatusDialogFragment
import com.example.bedashingapp.views.stock_counting.ItemsDialogFragment
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import com.sixlogics.flexspace.wrappers.NavigationWrapper
import kotlinx.android.synthetic.main.fragment_goods_receipt.*
import kotlinx.android.synthetic.main.fragment_goods_receipt.btn_cancel
import kotlinx.android.synthetic.main.fragment_goods_receipt.btn_post
import kotlinx.android.synthetic.main.fragment_goods_receipt.et_doc_date
import kotlinx.android.synthetic.main.fragment_goods_receipt.et_due_date
import kotlinx.android.synthetic.main.fragment_goods_receipt.iv_item_barcode
import kotlinx.android.synthetic.main.fragment_goods_receipt.root
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

import kotlin.collections.ArrayList


class GoodsReceiptFragment : BaseFragment(), SingleButtonListener, View.OnClickListener {

    private var index: Int = -1
    lateinit var openPurchaseOrderAdapter: OpenPurchaseOrderItemAdapter
    lateinit var openPo: OpenPurchaseOder
    private var costingCode3: String = ""
    private var uomCode: String = ""


    private var selectedItem: ItemEntity? = null
    private var uomsList: ArrayList<UOMEntity> = ArrayList()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun apiCaller(purpose: String) {
        TODO("Not yet implemented")
    }


    var docNum = -1
    private fun init() {
        docNum = (context as MainActivity).mainActivityViewModel.poNumber

        et_doc_date.setOnClickListener(this)
        et_due_date.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)
        iv_item_barcode.setOnClickListener(this)
        btn_post.setOnClickListener(this)
        ed_select_item.setOnClickListener(this)
        et_Search.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                if (cs.toString().isNotBlank()) {
                    if ((context as MainActivity).mainActivityViewModel.getSelectedItems()
                            .isNotEmpty()
                    ) {
                        filterItems(cs.toString().lowercase(Locale.ROOT))
                    }

                } else {
                    openPurchaseOrderAdapter.updateList((context as MainActivity).mainActivityViewModel.getSelectedItems())
                }
            }


            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
            override fun afterTextChanged(arg0: Editable) {}
        })

        (context as MainActivity).mainActivityViewModel.getSelectedItems().clear()


        if ((context as MainActivity).checkSessionConnection(this ,"")) {
            getOpenPO()
        }
    }

    private fun filterItems(text: String) {
        if (text.isEmpty()) {
            openPurchaseOrderAdapter.updateList((context as MainActivity).mainActivityViewModel.getSelectedItems())
        } else {
            val temp: ArrayList<Line> = ArrayList()
            for (line in (context as MainActivity).mainActivityViewModel.getSelectedItems()) {
                if (line.ItemCode!!.toLowerCase()
                        .contains(text)
                ) {
                    temp.add(line)
                }


            }
            openPurchaseOrderAdapter.updateList(temp)
        }
    }

    private fun getItemByItemCode(itemCode: String, addedItem: Line) {

        (context as MainActivity).mainActivityViewModel.getItemByItemCode(itemCode)
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {

                        Status.SUCCESS -> {
                            index =
                                (context as MainActivity).mainActivityViewModel.getSelectedItems()
                                    .indexOf(addedItem)

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
                               requireContext().resources. getString(R.string.post_document) -> {
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


    private fun postPO(payload: PurchaseDeliveryNotesRequest) {


        (context as MainActivity).mainActivityViewModel.PurchaseDeliveryNotes(
            (context as MainActivity).sessionManager!!.getBaseURL(),
            (context as MainActivity).sessionManager!!.getCompany(),
            (context as MainActivity).sessionManager!!.getSessionId(),
            (context as MainActivity).sessionManager!!.getUserBranchName(),
            (context as MainActivity).sessionManager!!.getUserHeadOfficeCardCode(),
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
                                        docNum.toString()
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

    private fun openInventoryStatusDialog(data: List<CustomObject>) {
        val dialog = InventoryStatusDialogFragment(data)
        dialog.isCancelable = true
        dialog.show(requireActivity().supportFragmentManager, dialog.tag)
    }


    private fun getOpenPO() {
        (context as MainActivity).mainActivityViewModel.getOpenPO(
            (context as MainActivity).sessionManager!!.getBaseURL(),
            (context as MainActivity).sessionManager!!.getSessionId(),
            (context as MainActivity).sessionManager!!.getCompany(),
            docNum.toString()
        ).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideProgressBar()
                        openPo = it.data!!.value[0]

                        openPo.DocumentLines.filter { it.RemainingOpenQuantity > 0 }
                        val temp = openPo.DocumentLines.filter { it.RemainingOpenQuantity > 0 }

                        (context as MainActivity).mainActivityViewModel.setSelectedItems(
                            temp.toCollection(
                                ArrayList()
                            )
                        )
                        fillData()

                    }
                    Status.LOADING -> {
                        showProgressBar("", "")
                    }
                    Status.ERROR -> {
                        hideProgressBar()
                        Log.d("error_retrofit", resource.message!!)
                        showToastLong(resource.message)
                    }
                }
            }
        })
    }

    private fun fillData() {
        et_doc_date.setText(openPo.DocDate.changeDateFormat("yyyy-MM-dd", "dd-MM-yyyy"))
        et_due_date.setText(openPo.DocDueDate.changeDateFormat("yyyy-MM-dd", "dd-MM-yyyy"))
        setRecyclerView()
    }

    override fun getLayout(): Int {
        return R.layout.fragment_goods_receipt
    }


    private fun retainItemData(item: ItemEntity, addedItem: Line) {
        selectedItem = item
        costingCode3 = if (item.ItemsGroupCode == 106) {
            item.U_Deprtmnt!!
        } else {
            ""
        }
        uomCode = addedItem.UoMCode!!
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
                            val item =
                                (context as MainActivity).mainActivityViewModel.getSelectedItems()
                                    .filter { it.ItemCode == selectedItem!!.ItemCode }
                            if (item.isNotEmpty()) {
                                val itemReceivingDialog = ItemReceivingDialog(
                                    this,
                                    "",
                                    (context as MainActivity).mainActivityViewModel.getSelectedItems()
                                        .indexOf(item[0]),
                                    uomsList,
                                    selectedItem
                                )
                                itemReceivingDialog.show(childFragmentManager, "")
                            } else {
                                showToastLong(getString(R.string.selected_item_not_exist))
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

    private fun setRecyclerView() {
        openPurchaseOrderAdapter = OpenPurchaseOrderItemAdapter(
            requireContext(),
            (context as MainActivity).mainActivityViewModel.getSelectedItems(),
            this
        )
        rv_open_po.adapter = openPurchaseOrderAdapter
        rv_open_po.setHasFixedSize(true)
        rv_open_po.layoutManager = LinearLayoutManager(context)
    }

    override fun onButtonClick(type: String, position: Int) {
        when (type) {
            getString(R.string.yes) -> {
                NavigationWrapper.navigateToFragmentDashboard(true)
            }
            getString(R.string.post_document) -> {
                checkSessionConnection(getString(R.string.post_document))
            }
            "editPO" -> {

                getItemByItemCode(
                    openPo.DocumentLines[position].ItemCode!!,
                    openPo.DocumentLines[position]
                )
            }
            "update" -> {
                openPurchaseOrderAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun openItemSelectDialog() {
        val itemSelectDialogFragment = ItemsDialogFragment()
        itemSelectDialogFragment.isCancelable = true

        itemSelectDialogFragment.mOnItemClickListener =
            object : ItemsDialogFragment.OnItemClickListener {
                override fun onItemClick(item: ItemEntity) {
                    val temp = (context as MainActivity).mainActivityViewModel.getSelectedItems()
                        .filter { it.ItemCode == item.ItemCode }
                    if (temp.isNotEmpty()) {
                        retainItemData(item, temp[0])
                    } else {
                        showToastLong(getString(R.string.selected_item_not_exist))
                    }

                }
            }
        itemSelectDialogFragment.show(
            requireActivity().supportFragmentManager,
            itemSelectDialogFragment.tag
        )
    }

    class PortraitCaptureActivityGD : CaptureActivity()

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
                                selectedItem = resource.data
                                fetchUomsByUomGroupEntry(resource.data.UoMGroupEntry)
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

    override fun onClick(view: View?) {

        when (view) {
            et_doc_date -> {
                openDatePickerDialog(requireContext(), et_doc_date)
            }
            et_due_date -> {
                openDatePickerDialog(requireContext(), et_due_date)
            }
            btn_post -> {
                checkSessionConnection(requireContext().resources. getString(R.string.post_document))
            }
            btn_cancel -> {
                requireActivity().onBackPressed()
            }
            ed_select_item -> {
                openItemSelectDialog()
            }
            iv_item_barcode -> {
                initiateScanFragment(PortraitCaptureActivityGD::class.java)
            }
        }


    }

}