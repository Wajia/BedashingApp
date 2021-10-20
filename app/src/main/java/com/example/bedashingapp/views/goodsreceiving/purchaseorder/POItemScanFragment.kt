package com.example.bedashingapp.views.goodsreceiving.purchaseorder

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bedashingapp.BaseFragment
import com.example.bedashingapp.MainActivity
import com.example.bedashingapp.R
import com.example.bedashingapp.data.api.ApiHelper
import com.example.bedashingapp.data.api.RetrofitBuilder
import com.example.bedashingapp.data.model.db.ItemEntity
import com.example.bedashingapp.data.model.remote.*
import com.example.bedashingapp.helper.DateUtilsApp
import com.example.bedashingapp.helper.ViewModelFactory
import com.example.bedashingapp.utils.Constants
import com.example.bedashingapp.utils.OnItemClickListener
import com.example.bedashingapp.utils.Status
import com.example.bedashingapp.viewmodel.MainActivityViewModel
import com.example.bedashingapp.views.goodsreceiving.purchaseorder.adapters.POLineAdapter
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import kotlinx.android.synthetic.main.fragment_po_item_scan.*
import kotlinx.android.synthetic.main.fragment_po_item_scan.edSearch
import kotlinx.android.synthetic.main.fragment_purchase_orders.*


/**
 * A simple [Fragment] subclass.
 * Use the [POItemScanFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class POItemScanFragment : BaseFragment() {

    private lateinit var mainActivityViewModel: MainActivityViewModel

    private var poItemsList: ArrayList<ItemPO> = ArrayList()
    private var poItemDetailsList: ArrayList<ItemEntity> = ArrayList()

    override fun getLayout(): Int {
        return R.layout.fragment_po_item_scan
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //clearing scanned item
        mainActivityViewModel.clearSelectedItem()
        mainActivityViewModel.scannedBarcode = ""
        mainActivityViewModel.selectedLineNum = ""

        //getting data
        poItemsList.clear()
        poItemsList.addAll(mainActivityViewModel.getPOItems())
        getItemDetailsMasterData()

        //show complete button if atleast one item is received (partially or complete)
        if (poItemsList.any { it.QuantityReceived > 0.0 }) {
            btn_complete.visibility = View.VISIBLE
        }


        edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString().toLowerCase())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })


        ed_scan_item.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                if (event?.action == MotionEvent.ACTION_UP) {
                    val integrator = IntentIntegrator(requireActivity())
                    integrator.setOrientationLocked(true)
                    integrator.captureActivity = PortraitCaptureActivity::class.java
                    integrator.initiateScan()
                    return true;
                }
                return false;
            }
        })

        btn_enter_item.setOnClickListener {
            if (mainActivityViewModel.selectedLineNum.isNotEmpty()) {
                var b = Bundle()
                b.putBoolean("forUpdate", false)
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.nav_po_enter_quantity, b)
            } else {
                showToastShort("Please scan an item first.")
            }
        }

        btn_complete.setOnClickListener {
            if (isConnectedToNetwork()) {
                //get crf token
//                mainActivityViewModel.getCRFTokenInboundDelivery().observe(
//                    viewLifecycleOwner,
//                    Observer {
//                        it?.let { resource ->
//                            when (resource.status) {
//                                Status.SUCCESS -> {
//                                    resource.data?.enqueue(object :
//                                        Callback<ReceiveGoodsPOResponse> {
//                                        override fun onResponse(
//                                            call: Call<ReceiveGoodsPOResponse>,
//                                            response: Response<ReceiveGoodsPOResponse>
//                                        ) {
//                                            //get headers
//                                            val headers = response.headers()
//
//                                            //get cookies
//                                            val cookies = headers.values("set-cookie")
//                                            var cookie = ""
//                                            for (c in cookies) {
//                                                cookie += c.split(" ")[0]
//                                            }
//
//                                            //get crf token from header
//                                            postDocument(headers.get("x-csrf-token")!!, cookie)
//                                        }
//
//                                        override fun onFailure(
//                                            call: Call<ReceiveGoodsPOResponse>,
//                                            t: Throwable
//                                        ) {
//                                            hideProgressBar()
//                                            showToastShort("Unable to fetch CRF Token. Try again later")
//                                        }
//
//                                    })
//                                }
//                                Status.LOADING -> {
//                                    showProgressBar("", "")
//                                }
//                                Status.ERROR -> {
//                                    hideProgressBar()
//                                    showToastLong(resource.message!!)
//                                }
//                            }
//                        }
//                    })
                saveDocument(poItemDetailsList)

            } else {
                showToastShort(resources.getString(R.string.network_not_connected_msg))
            }
        }
    }


    private fun saveDocument(itemDetailsList: ArrayList<ItemEntity>){
        //preparing data for Goods receive PO
        val list = mutableListOf<ItemRequest>()
        for (itemPO in mainActivityViewModel.getPOItems().filter { it.QuantityReceived > 0 }) {

            val itemQtyList = mutableListOf<ItemQuantity>()

            var itemDetail = itemDetailsList.find{it.InternalID == itemPO.ProductID}!!
            var quantity = itemPO.QuantityReceived
            if(itemPO.UnitCode == Constants.CARTON_UNIT_CODE){
                quantity *= itemDetail.QuantityConversion[0].Quantity.toDouble()
            }

            itemQtyList.add(
                ItemQuantity(
                    Quantity = quantity.toString(),
//                    UnitCode = itemPO.UnitCode,
                    UnitCode = Constants.EACH_UNIT_CODE,
                    QuantityRoleCode = "18",
                    QuantityTypeCode = Constants.EACH_UNIT_CODE,
                    LogisticAreaID = itemPO.BinCode
                )
            )
            var itemConditionCode = if (itemPO.ConditionGoods == "Good") {
                "101"
            } else {
                "102"
            }
            var packingConditionCode = if (itemPO.PackingCondition == "Good") {
                "105"
            } else {
                "106"
            }
            var styleMatchCode = if (itemPO.StyleMatch == "Yes") {
                "102"
            } else {
                "103"
            }

            val itemPurchaseOrderReference = ItemPurchaseOrderReference(
                ID = mainActivityViewModel.getSelectedPO()?.ID!!,
                ItemID = itemPO.ID,
                ItemTypeCode = "18",
                TypeCode = "001",
                RelationshipRoleCode = "1",
                GoodsCondition = itemConditionCode,
                PackingCondition = packingConditionCode,
                StyleMatch = styleMatchCode
            )

            val sellerParty = ItemSellerParty(
                PartyID = mainActivityViewModel.getSelectedPO()?.Supplier?.PartyID!!
            )

            val buyerParty = ItemBuyerParty(
                PartyID = mainActivityViewModel.getSelectedPO()?.BuyerParty?.PartyID!!
            )

            list.add(
                ItemRequest(
                    TypeCode = "14",
                    ProductID = itemPO.ProductID,
                    ItemQuantity = itemQtyList,
                    ItemPurchaseOrderReference = itemPurchaseOrderReference,
                    ItemSellerParty = sellerParty,
                    ItemBuyerParty = buyerParty
                )
            )
        }

        val randomNumberForID = "PO-" + DateUtilsApp.getUTCCurrentDateTimeMS().toString()

        val receiveGoodsPORequest = ReceiveGoodsPORequest(
            ID = randomNumberForID,
            ProcessingTypeCode = "SD",
            Item = list
        )


        mainActivityViewModel.saveReceiveGoodsPODocument(receiveGoodsPORequest).observe(viewLifecycleOwner, Observer {
            it?.let{resource ->
                when(resource.status){
                    Status.SUCCESS->{
                        hideProgressBar()
                        (requireActivity() as MainActivity).postDocumentPO(receiveGoodsPORequest)
                        showSnackBar("Document has been saved successfully. Syncing started." ,root, R.id.nav_po_pdf)
                    }
                    Status.LOADING->{
                        showProgressBar("", "")
                    }
                    Status.ERROR->{
                        hideProgressBar()
                        showToastLong(resource.message!!)
                    }
                }
            }
        })
    }



//    private fun postDocument(crfToken: String, cookie: String) {
//        mainActivityViewModel.receiveGoodsPO(crfToken, cookie)
//            .observe(viewLifecycleOwner, Observer {
//                it?.let { resource ->
//                    when (resource.status) {
//                        Status.SUCCESS -> {
//                            //post goods receipt
//                            mainActivityViewModel.postGoodsReceipt(
//                                crfToken,
//                                cookie,
//                                resource.data?.d?.results!!.ObjectID
//                            ).observe(viewLifecycleOwner, Observer {
//                                it?.let { resource ->
//                                    when (resource.status) {
//                                        Status.SUCCESS -> {
//                                            hideProgressBar()
//                                            showSnackBar("All the items for ${resource.data?.d?.results!!.ID} have been received and a receipt has been posted" ,root, R.id.nav_po_pdf)
//                                        }
//                                        Status.LOADING->{
//
//                                        }
//                                        Status.ERROR->{
//                                            hideProgressBar()
//                                            showToastLong(resource.message!!)
//                                        }
//                                    }
//                                }
//                            })
//                        }
//                        Status.LOADING -> {
//
//                        }
//                        Status.ERROR -> {
//                            hideProgressBar()
//                            showToastLong(resource.message!!)
//                        }
//                    }
//                }
//            })
//    }

    private var adapter: POLineAdapter? = null
    private fun setRecyclerView() {
        adapter = POLineAdapter(poItemsList, poItemDetailsList, requireContext(), onItemClickListener)
        rv_po_lines.adapter = adapter
        rv_po_lines.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        rv_po_lines.layoutManager = layoutManager
    }

    private var onItemClickListener: OnItemClickListener<ItemPO> = object : OnItemClickListener<ItemPO>(){
        override fun onClicked(view: View?, position: Int, type: String?, data: ItemPO?) {
            if(data?.QuantityReceived!! > 0){
                var b = Bundle()
                b.putBoolean("forUpdate", true)
                mainActivityViewModel.selectedLineNum = data.ID
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.nav_po_enter_quantity, b)
            }else{
                showToastShort("Quantity for this item has not been entered yet.")
            }
        }

    }

    private fun filter(text: String) {
        if (text.isEmpty()) {
            adapter?.updateList(poItemsList)
        } else {
            var temp: ArrayList<ItemPO> = ArrayList()
            for (poItem in poItemsList) {
                if (poItem.ProductID.toLowerCase().contains(text)) {
                    temp.add(poItem)
                }
            }
            adapter?.updateList(temp)
        }
    }

    private fun searchByBarcode(barcode: String) {
        mainActivityViewModel.selectedLineNum = ""
        mainActivityViewModel.clearSelectedItem()
        //check whether any item in poList contains scanned barcode or not
        var ids: ArrayList<String> = ArrayList()
        for (line in poItemsList) {
            line.isSelected = false
            ids.add(line.ProductID)
        }

        mainActivityViewModel.getItemsBarcode(ids).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        rlProgress.visibility = View.GONE
                        checkBarcode(resource.data as ArrayList<ItemEntity>, barcode)
                    }
                    Status.LOADING -> {
                        rlProgress.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        rlProgress.visibility = View.GONE
                        showToastLong(resource.message!!)
                    }
                }
            }
        })

    }

//    private fun checkBarcode(itemList: ArrayList<ItemEntity>, barcode: String) {
//        var flag = false
//        for ((index, item) in itemList.withIndex()) {
//
//            if (item.GlobalTradeItemNumber.find { it.ID == barcode } != null) {
//                ed_scan_item.setText(item.InternalID)
//                //first check if po contains similar items or not
//                if (poItemsList.filter { it.ProductID == item.InternalID }.size > 1) {
//                    var list = poItemsList.filter { it.ProductID == item.InternalID }
//
//                    //now select item's where received quantity is less than remaining quantity
//                    for ((index, potentialItem) in list.withIndex()) {
//                        if (potentialItem.QuantityReceived < potentialItem.Quantity.toDouble()) {
//                            poItemsList[poItemsList.indexOf(potentialItem)].isSelected = true
//                            mainActivityViewModel.selectedLineNum =
//                                poItemsList[poItemsList.indexOf(potentialItem)].ID
//                            break
//                        }
//                        if (index == list.size - 1) {
//                            showToastShort("Entire quantity already received for the item.")
//                        }
//                    }
//                } else {
//                    var potentialItem = poItemsList.find { it.ProductID == item.InternalID }
//                    if (potentialItem?.QuantityReceived!! < potentialItem.Quantity.toDouble()) {
//                        poItemsList[poItemsList.indexOf(potentialItem)].isSelected = true
//                        mainActivityViewModel.selectedLineNum =
//                            poItemsList[poItemsList.indexOf(potentialItem)].ID
//                    } else {
//                        showToastShort("Entire quantity already received for the item.")
//                    }
//                }
//                flag = true
//
//                mainActivityViewModel.setSelectedItem(item)
//                mainActivityViewModel.scannedBarcode = barcode
//                break
//            }
//        }
//        if (flag) {
//            adapter?.notifyDataSetChanged()
//        } else {
//            adapter?.notifyDataSetChanged()
//            ed_scan_item.setText("")
//            showToastShort("Scanned item is not present in Purchase Order")
//        }
//    }

    private fun checkBarcode(itemList: ArrayList<ItemEntity>, barcode: String) {
        var flag = false
        for ((index, item) in itemList.withIndex()) {

            if (item.PackagingBarcode_KUT.replace("\n", "") == barcode || item.Barcode_KUT.replace("\n", "") == barcode) {
                ed_scan_item.setText(item.InternalID)
                //first check if po contains similar items or not
                if (poItemsList.filter { it.ProductID == item.InternalID }.size > 1) {
                    var list = poItemsList.filter { it.ProductID == item.InternalID }

                    //now select item's where received quantity is less than remaining quantity
                    for ((index, potentialItem) in list.withIndex()) {
                        var quantityReceived = potentialItem.QuantityReceived
                        if(potentialItem.QuantityReceived > 0){
                            if(potentialItem.UnitCode == Constants.CARTON_UNIT_CODE){
                                quantityReceived *= item.QuantityConversion[0].Quantity.toDouble()
                            }
                        }
                        if (quantityReceived < (potentialItem.Quantity.toDouble() - potentialItem.TotalDeliveredQuantity.toDouble())) {
                            poItemsList[poItemsList.indexOf(potentialItem)].isSelected = true
                            mainActivityViewModel.selectedLineNum =
                                poItemsList[poItemsList.indexOf(potentialItem)].ID
                            break
                        }
                        if (index == list.size - 1) {
                            showToastShort("Entire quantity already received for the item.")
                        }
                    }
                } else {
                    var potentialItem = poItemsList.find { it.ProductID == item.InternalID }
                    var quantityReceived = potentialItem?.QuantityReceived!!
                    if(potentialItem.QuantityReceived > 0){
                        if(potentialItem.UnitCode == Constants.CARTON_UNIT_CODE){
                            quantityReceived *= item.QuantityConversion[0].Quantity.toDouble()
                        }
                    }
                    if (quantityReceived < (potentialItem.Quantity.toDouble() - potentialItem.TotalDeliveredQuantity.toDouble())) {
                        poItemsList[poItemsList.indexOf(potentialItem)].isSelected = true
                        mainActivityViewModel.selectedLineNum =
                            poItemsList[poItemsList.indexOf(potentialItem)].ID
                    } else {
                        showToastShort("Entire quantity already received for the item.")
                    }
                }
                flag = true

                mainActivityViewModel.setSelectedItem(item)
                mainActivityViewModel.scannedBarcode = barcode
                break
            }
        }
        if (flag) {
            adapter?.notifyDataSetChanged()
        } else {
            adapter?.notifyDataSetChanged()
            ed_scan_item.setText("")
            showToastShort("Scanned item is not present in Purchase Order")
        }
    }


    private fun getItemDetailsMasterData(){
        var ids: ArrayList<String> = ArrayList()
        for (line in poItemsList) {
            ids.add(line.ProductID)
        }

        mainActivityViewModel.getItemsBarcode(ids).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        rlProgress.visibility = View.GONE
                        poItemDetailsList.clear()
                        poItemDetailsList.addAll(resource.data as ArrayList)
                        setRecyclerView()
                    }
                    Status.LOADING -> {
                        rlProgress.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        rlProgress.visibility = View.GONE
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