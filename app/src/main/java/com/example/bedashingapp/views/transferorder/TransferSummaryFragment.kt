package com.example.bedashingapp.views.transferorder

import android.os.Bundle
import android.view.View
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
import com.example.bedashingapp.data.model.remote.CreateOutboundDeliveryRequest
import com.example.bedashingapp.data.model.remote.LogisticDetails
import com.example.bedashingapp.data.model.remote.TaskDetails
import com.example.bedashingapp.data.model.remote.TaskItem
import com.example.bedashingapp.helper.DateUtilsApp
import com.example.bedashingapp.helper.ViewModelFactory
import com.example.bedashingapp.utils.OnItemClickListener
import com.example.bedashingapp.utils.Status
import com.example.bedashingapp.viewmodel.MainActivityViewModel
import com.example.bedashingapp.views.goodsreceiving.purchaseorder.bottomsheet.TransferSummaryBottomSheet
import com.example.bedashingapp.views.transferorder.adapter.TransferSummaryItemAdapter
import kotlinx.android.synthetic.main.fragment_transfer_summary.*
import kotlinx.android.synthetic.main.fragment_transfer_summary.btn_complete
import kotlinx.android.synthetic.main.fragment_transfer_summary.tv_date
import java.text.SimpleDateFormat
import java.util.*


class TransferSummaryFragment : BaseFragment(),
    TransferSummaryBottomSheet.TransferSummaryButtonListener {

    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun getLayout(): Int {
        return R.layout.fragment_transfer_summary
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

        tv_from_wh.text = mainActivityViewModel.selectedFromWarehouse
        tv_to_wh.text = mainActivityViewModel.getSelectedTask()!!.SiteID
        tv_date.text = DateUtilsApp.getUTCFormattedDateTimeString(
            SimpleDateFormat("dd/MM/yyyy"),
            Calendar.getInstance().time
        )

        setRecyclerView()

        btn_new_item.setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigate(R.id.nav_outbound_item_scan, Bundle())
        }

        btn_complete.setOnClickListener {
            //check if item(s) received at least = 1
            if (mainActivityViewModel.getTransferItems().filter { it.Bins.size > 0 }.isNotEmpty()) {
                saveDocument()
            } else {
                showToastShort("Please fulfill proposed quantity of at least one item.")
            }
        }
    }

    private var adapter: TransferSummaryItemAdapter? = null
    private fun setRecyclerView() {
        var transferItemsList = mainActivityViewModel.getTransferItems().filter { it.Bins.size > 0 }
        adapter =
            TransferSummaryItemAdapter(transferItemsList, requireContext(), onItemClickListener)
        rv_transfer_summary.adapter = adapter
        rv_transfer_summary.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        rv_transfer_summary.layoutManager = layoutManager

    }

    private var onItemClickListener: OnItemClickListener<TaskItem> =
        object : OnItemClickListener<TaskItem>() {
            override fun onClicked(view: View?, position: Int, type: String?, data: TaskItem?) {
                val dialog = TransferSummaryBottomSheet(
                    this@TransferSummaryFragment,
                    data!!
                )
                dialog.show(childFragmentManager, "")
            }
        }

    override fun onButtonClick(taskItem: TaskItem) {
        mainActivityViewModel.updateTransferItems(taskItem)
        setRecyclerView()
    }


    private fun saveDocument() {
        if(isConnectedToNetwork()) {
            //preparing payload
            val taskDetails = mutableListOf<TaskDetails>()
            for (item in mainActivityViewModel.getTransferItems().filter { it.Bins.size > 0 }) {
                val binsList = mutableListOf<LogisticDetails>()
                for (bin in item.Bins) {
                    val logistic = LogisticDetails(
                        LogisticID = bin.ID,
                        Quantity = bin.PickedQuantity.toString(),
                        UOM = item.UOM
                    )
                    binsList.add(logistic)
                }

                taskDetails.add(
                    TaskDetails(
                        ItemCode = item.ProductID,
                        LogisticDetails = binsList
                    )
                )
            }
            val outboundDeliveryRequest = CreateOutboundDeliveryRequest(
                TaskID = mainActivityViewModel.getSelectedTask()!!.TaskID,
                FromSiteID = mainActivityViewModel.selectedFromWarehouse,
                TaskDetails = taskDetails
            )


            mainActivityViewModel.saveOutboundDeliveryDocument(outboundDeliveryRequest)
                .observe(viewLifecycleOwner, Observer {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                hideProgressBar()
                                (requireActivity() as MainActivity).createOutboundDelivery(
                                    outboundDeliveryRequest
                                )
                                showSnackBar(
                                    "Document has been saved successfully. Syncing started.",
                                    root,
                                    R.id.nav_outbound_tasks
                                )
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
        }else{
            showToastLong(resources.getString(R.string.network_not_connected_msg))
        }

    }

}