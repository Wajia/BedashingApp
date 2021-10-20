package com.example.bedashingapp.views.transferorder

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bedashingapp.BaseFragment
import com.example.bedashingapp.R
import com.example.bedashingapp.data.api.ApiHelper
import com.example.bedashingapp.data.api.RetrofitBuilder
import com.example.bedashingapp.data.model.remote.SelectedBin
import com.example.bedashingapp.data.model.remote.TaskItem
import com.example.bedashingapp.helper.ViewModelFactory
import com.example.bedashingapp.utils.OnItemClickListener
import com.example.bedashingapp.utils.Status
import com.example.bedashingapp.viewmodel.MainActivityViewModel
import com.example.bedashingapp.views.transferorder.adapter.OutboundBinAdapter
import kotlinx.android.synthetic.main.fragment_outbound_qty_bins.*
import kotlinx.android.synthetic.main.fragment_outbound_qty_bins.edSearch
import kotlinx.android.synthetic.main.list_transfer_lines_single_item.*


/**
 * A simple [Fragment] subclass.
 * Use the [OutboundQtyBinsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OutboundQtyBinsFragment : BaseFragment() {

    private lateinit var mainActivityViewModel: MainActivityViewModel

    private val binsList: ArrayList<SelectedBin> = ArrayList()
    override fun getLayout(): Int {
        return R.layout.fragment_outbound_qty_bins
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

        getData()

        edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString().toLowerCase())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        btn_add.setOnClickListener{
            if(binsList.filter { it.isSelected }.isNotEmpty()) {
                //check if all selected bins have quantity entered
                if (binsList.filter { it.isSelected }.any { it.PickedQuantity == 0.0 }) {
                    showToastLong("Please enter quantity for all selected bins.")
                } else {
                    var pickedQuantity = 0.0
                    for (bin in binsList.filter { it.isSelected }) {
                        pickedQuantity += bin.PickedQuantity
                    }
                    if (pickedQuantity > selectedItem!!.OpenQuantity) {
                        showToastShort("Picked Quantity cannot be greater than proposed quantity")
                    }else if (pickedQuantity < selectedItem!!.OpenQuantity){
                        showToastShort("Picked Quantity must be equal to Proposed Quantity")
                    } else {
                        mainActivityViewModel.setOutboundBins(binsList.filter { it.isSelected } as ArrayList<SelectedBin>)
                        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                            .navigate(R.id.nav_outbound_item_scan, Bundle())
                    }
                }
            }else{
                showToastShort("Please select at least one bin")
            }
        }

    }

    var selectedItem: TaskItem? = null
    private fun getData() {
        //get selected item
        selectedItem = mainActivityViewModel.getTransferItems()
            .find { it.LineID == mainActivityViewModel.selectedLineNum }
        mainActivityViewModel.getOutboundBins(
            selectedItem!!.ProductID,
            mainActivityViewModel.selectedFromWarehouse
        )
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            //filter out bins having qty = 0.0 & empty IDs
                            for (bin in resource.data!!.d.results.filter { b -> b.KCUN_RESTRICTED_STOCK.toDouble() > 0.0 && b.CLOG_AREA_UUID.isNotEmpty() } as ArrayList) {
                                //extract id
                                bin.CLOG_AREA_UUID = bin.CLOG_AREA_UUID.split("/").last()

                                //check if bin was already selected for item or not
                                var pickedQuantity = 0.0
                                var isSelected = false
                                if (selectedItem!!.Bins.find { b -> b.ID == bin.CLOG_AREA_UUID } != null) {
                                    pickedQuantity =
                                        selectedItem!!.Bins.find { b -> b.ID == bin.CLOG_AREA_UUID }!!.PickedQuantity
                                    isSelected = true
                                }
                                binsList.add(
                                    SelectedBin(
                                        bin.CLOG_AREA_UUID,
                                        pickedQuantity,
                                        bin.KCUN_RESTRICTED_STOCK.toDouble(),
                                        isSelected
                                    )
                                )
                            }
                            hideProgressBar()
                            setRecyclerView()
                            setTotalQuantities()
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

    private var adapter: OutboundBinAdapter? = null
    private fun setRecyclerView() {
        adapter = OutboundBinAdapter(binsList, requireContext(), onItemClickListener)
        rv_outbound_bins.adapter = adapter
        rv_outbound_bins.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        rv_outbound_bins.layoutManager = layoutManager
    }

    var onItemClickListener: OnItemClickListener<SelectedBin> =
        object : OnItemClickListener<SelectedBin>() {
            override fun onClicked(view: View?, position: Int, type: String?, data: SelectedBin?) {
                if (type == "select") {
                    binsList[binsList.indexOf(data)].isSelected = true

                    adapter!!.notifyItemChanged(position)
                } else if (type == "unSelect") {
                    binsList[binsList.indexOf(data)].isSelected = false
                    binsList[binsList.indexOf(data)].PickedQuantity = 0.0

                    setTotalQuantities()
                    adapter!!.notifyItemChanged(position)
                }
                else if(type == "update"){
                    setTotalQuantities()
                }


            }

        }

    private fun filter(text: String) {
        if (text.isEmpty()) {
            adapter?.updateList(binsList)
        } else {
            var temp: ArrayList<SelectedBin> = ArrayList()
            for (bin in binsList) {
                if (bin.ID.toLowerCase().contains(text)) {
                    temp.add(bin)
                }
            }
            adapter?.updateList(temp)
        }
    }

    private fun setTotalQuantities() {
        tv_total_proposed_qty.text =
            String.format("%.1f", selectedItem!!.OpenQuantity) + " ${selectedItem!!.UOM}"
        var selectedBinsList = binsList.filter { it.PickedQuantity > 0.0 }
        var qtyReceived = 0.0
        for (bin in selectedBinsList) {
            qtyReceived += bin.PickedQuantity
        }
        tv_total_picked_qty.text = String.format("%.1f", qtyReceived) + " ${selectedItem!!.UOM}"
    }

}