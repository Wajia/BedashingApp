package com.example.bedashingapp.views.transferorder

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bedashingapp.BaseFragment
import com.example.bedashingapp.R
import com.example.bedashingapp.data.api.ApiHelper
import com.example.bedashingapp.data.api.RetrofitBuilder
import com.example.bedashingapp.data.model.db.ItemEntity
import com.example.bedashingapp.data.model.remote.TaskItem
import com.example.bedashingapp.helper.DateUtilsApp
import com.example.bedashingapp.helper.ViewModelFactory
import com.example.bedashingapp.utils.OnItemClickListener
import com.example.bedashingapp.utils.Status
import com.example.bedashingapp.viewmodel.MainActivityViewModel
import com.example.bedashingapp.views.goodsreceiving.purchaseorder.POItemScanFragment
import com.example.bedashingapp.views.transferorder.adapter.TransferLineAdapter
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import kotlinx.android.synthetic.main.fragment_outbound_item_scan.*
import kotlinx.android.synthetic.main.fragment_outbound_item_scan.btn_enter_item
import kotlinx.android.synthetic.main.fragment_outbound_item_scan.edSearch
import kotlinx.android.synthetic.main.fragment_outbound_item_scan.ed_scan_item
import kotlinx.android.synthetic.main.fragment_outbound_item_scan.rlProgress
import kotlinx.android.synthetic.main.fragment_po_item_scan.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 * Use the [OutboundItemScanFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OutboundItemScanFragment : BaseFragment() {

    private lateinit var mainActivityViewModel: MainActivityViewModel

    private var transferItemsList: ArrayList<TaskItem> = ArrayList()

    override fun getLayout(): Int {
        return R.layout.fragment_outbound_item_scan
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
        mainActivityViewModel.scannedBarcode = ""
        mainActivityViewModel.selectedLineNum = ""

        //getting data
        transferItemsList.clear()
        transferItemsList.addAll(mainActivityViewModel.getTransferItems())
        setRecyclerView()

        et_to_warehouse.setText(mainActivityViewModel.getSelectedTask()!!.SiteID)

        //setting date
        tv_date.setText(DateUtilsApp.getUTCFormattedDateTimeString(SimpleDateFormat("dd/MM/yyyy"), Calendar.getInstance().time))

        //show summary button if atleast one item is received (partially or complete)
        if(transferItemsList.any { it.Bins.size > 0 }){
            btn_summary.visibility = View.VISIBLE
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
                    integrator.captureActivity = POItemScanFragment.PortraitCaptureActivity::class.java
                    integrator.initiateScan()
                    return true;
                }
                return false;
            }
        })


        btn_enter_item.setOnClickListener {
            if (mainActivityViewModel.selectedLineNum.isNotEmpty()) {

                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.nav_outbound_bins, Bundle())
            } else {
                showToastShort("Please scan an item first.")
            }
        }

        btn_summary.setOnClickListener{
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigate(R.id.nav_transfer_summary, Bundle())
        }

    }

    private var adapter: TransferLineAdapter? = null
    private fun setRecyclerView(){
        adapter = TransferLineAdapter(transferItemsList, requireContext(), onItemClickListener)
        rv_transfer_lines.adapter = adapter
        rv_transfer_lines.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        rv_transfer_lines.layoutManager = layoutManager

    }
    
    private var onItemClickListener: OnItemClickListener<TaskItem> = object : OnItemClickListener<TaskItem>(){
        override fun onClicked(view: View?, position: Int, type: String?, data: TaskItem?) {
            mainActivityViewModel.selectedLineNum = data!!.LineID
            for(item in transferItemsList){
                item.isSelected = false
            }
            transferItemsList[transferItemsList.indexOf(data)].isSelected = true
            ed_scan_item.setText(data.ProductID)
            adapter!!.notifyDataSetChanged()
        }

    }

    private fun filter(text: String) {
        if (text.isEmpty()) {
            adapter?.updateList(transferItemsList)
        } else {
            var temp: ArrayList<TaskItem> = ArrayList()
            for (transferItem in transferItemsList) {
                if (transferItem.ProductID.toLowerCase().contains(text)) {
                    temp.add(transferItem)
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
        for (line in transferItemsList) {
            line.isSelected = false
            ids.add(line.ProductID)
        }

        mainActivityViewModel.getItemsBarcode(ids).observe(viewLifecycleOwner, {
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

    private fun checkBarcode(itemList: ArrayList<ItemEntity>, barcode: String) {
        var flag = false
        for ((index, item) in itemList.withIndex()) {

            if (item.PackagingBarcode_KUT.replace("\n", "") == barcode || item.Barcode_KUT.replace("\n", "") == barcode) {
                ed_scan_item.setText(item.InternalID)
                //first check if Task contains similar items or not
                if (transferItemsList.filter { it.ProductID == item.InternalID }.size > 1) {
                    var list = transferItemsList.filter { it.ProductID == item.InternalID }

                    //now select item's where Picked quantity is less than Proposed quantity
                    for ((index, potentialItem) in list.withIndex()) {
                        var quantityReceived = 0.0
                        for(bin in potentialItem.Bins){
                            quantityReceived += bin.PickedQuantity
                        }

                        if (quantityReceived < potentialItem.OpenQuantity) {
                            transferItemsList[transferItemsList.indexOf(potentialItem)].isSelected = true
                            mainActivityViewModel.selectedLineNum =
                                transferItemsList[transferItemsList.indexOf(potentialItem)].LineID
                            break
                        }
                        if (index == list.size - 1) {
                            showToastShort("Entire quantity already received for the item.")
                        }
                    }
                } else {
                    var potentialItem = transferItemsList.find { it.ProductID == item.InternalID }
                    var quantityReceived = 0.0
                    for(bin in potentialItem!!.Bins){
                        quantityReceived += bin.PickedQuantity
                    }

                    if (quantityReceived < potentialItem.OpenQuantity) {
                        transferItemsList[transferItemsList.indexOf(potentialItem)].isSelected = true
                        mainActivityViewModel.selectedLineNum =
                            transferItemsList[transferItemsList.indexOf(potentialItem)].LineID
                    } else {
                        showToastShort("Entire quantity already received for the item.")
                    }
                }
                flag = true

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