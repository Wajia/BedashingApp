package com.example.bedashingapp.views.goodsreceiving.purchaseorder

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bedashingapp.BaseFragment
import com.example.bedashingapp.R
import com.example.bedashingapp.data.api.ApiHelper
import com.example.bedashingapp.data.api.RetrofitBuilder
import com.example.bedashingapp.data.model.remote.ItemPO
import com.example.bedashingapp.data.model.remote.PurchaseOrder
import com.example.bedashingapp.helper.ViewModelFactory
import com.example.bedashingapp.utils.OnItemClickListener
import com.example.bedashingapp.utils.Status
import com.example.bedashingapp.viewmodel.MainActivityViewModel
import com.example.bedashingapp.views.goodsreceiving.purchaseorder.adapters.POAdapter
import kotlinx.android.synthetic.main.fragment_purchase_orders.*


class PurchaseOrderListFragment : BaseFragment() {
    private lateinit var mainActivityViewModel: MainActivityViewModel


    private var layoutExpansionHeight: Int = 0
    private var poList: ArrayList<PurchaseOrder> = ArrayList()
    private var itemListForPOs: ArrayList<ItemPO> = ArrayList()

    override fun getLayout(): Int {
        return R.layout.fragment_purchase_orders
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutExpansionHeight = layout_expansion.layoutParams.height
        layout_expansion.layoutParams.height = 0

        setupObserver()
        mainActivityViewModel.clearPOItems()
        mainActivityViewModel.clearSelectedPO()

        //by default via date is selected
        lbl_via_date.setTextColor(resources.getColor(R.color.colorBlack))
        img_date.setImageResource(R.drawable.ic_filterdate_selected)


        layout_date.setOnClickListener {
            lbl_via_date.setTextColor(resources.getColor(R.color.colorBlack))
            img_date.setImageResource(R.drawable.ic_filterdate_selected)

            lbl_via_vendor.setTextColor(resources.getColor(R.color.colorPrimary))
            img_vendor.setImageResource(R.drawable.ic_filteruser)


            lbl_due_date.visibility = View.VISIBLE
            val param = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.5f
            )
            lbl_lines.layoutParams = param

            toggleExpansionLayout()
            setRecyclerView(true)
        }

        layout_vendor.setOnClickListener {
            lbl_via_date.setTextColor(resources.getColor(R.color.colorPrimary))
            img_date.setImageResource(R.drawable.ic_filterdate)

            lbl_via_vendor.setTextColor(resources.getColor(R.color.colorBlack))
            img_vendor.setImageResource(R.drawable.ic_filteruser_selected)

            lbl_due_date.visibility = View.GONE
            val param = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.5f
            )
            lbl_lines.layoutParams = param

            toggleExpansionLayout()
            setRecyclerView()
        }


        btn_filter.setOnClickListener {
            toggleExpansionLayout()
        }

        edSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString().toLowerCase())
            }

            override fun afterTextChanged(s: Editable?) {
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


    private fun setupObserver() {
        if(isConnectedToNetwork()) {
            mainActivityViewModel.getPurchaseOrders().observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {

                            poList.clear()
                            poList.addAll(resource.data?.d?.results?.filter { po -> po.DeliveryStatusCode == "1" || po.DeliveryStatusCode == "2" } as ArrayList)

                            mainActivityViewModel.getItemCollectionForPOs()
                                .observe(viewLifecycleOwner,
                                    { it1 ->
                                        it1?.let { resource1 ->
                                            when (resource1.status) {
                                                Status.SUCCESS -> {
                                                    itemListForPOs.clear()
                                                    itemListForPOs.addAll(resource1.data?.d?.results as ArrayList)
                                                    setRecyclerView(true)
                                                    hideProgressBar()
                                                }
                                                Status.ERROR -> {
                                                    showToastLong(resource.message!!)
                                                    hideProgressBar()
                                                }
                                            }
                                        }
                                    })
                        }
                        Status.LOADING -> {
                            showProgressBar("", "Fetching Purchase Orders")
                        }
                        Status.ERROR -> {
                            showToastLong(resource.message!!)
                            hideProgressBar()
                        }
                    }
                }
            })
        }else{
            hideProgressBar()
            showToastLong(resources.getString(R.string.network_not_connected_msg))
        }
    }

    private var adapter: POAdapter? = null
    private fun setRecyclerView(showDate: Boolean = false){
        adapter = POAdapter(poList, itemListForPOs, requireActivity(), showDate, onItemClickListener)
        rv_po.adapter = adapter
        rv_po.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        rv_po.layoutManager = layoutManager
    }

    var onItemClickListener: OnItemClickListener<PurchaseOrder> = object : OnItemClickListener<PurchaseOrder>(){
        override fun onClicked(view: View?, position: Int, type: String?, data: PurchaseOrder?) {

            //set selected po
            mainActivityViewModel.setSelectedPO(data!!)
            mainActivityViewModel.setPOItems(itemListForPOs.filter { it.ParentObjectID == data.ObjectID && it.DeliveryStatusCode != "3" } as ArrayList<ItemPO>)

            Navigation.findNavController(activity!!, R.id.nav_host_fragment).navigate(R.id.nav_po_item_scan, Bundle())
        }
    }

    private fun filter(text: String){
        if(text.isEmpty()){
            adapter?.updateList(poList)
        }else{
            var temp: ArrayList<PurchaseOrder> = ArrayList()
            for(po in poList){
                if(po.ID.toLowerCase().contains(text) || po.Supplier.SupplierName[0].FormattedName.toLowerCase().contains(text)){
                    temp.add(po)
                }
            }
            adapter?.updateList(temp)
        }
    }

    private fun toggleExpansionLayout() {
        if (layout_expansion.layoutParams.height == 0) {
            com.example.bedashingapp.utils.AnimationUtils.slideView(
                layout_expansion,
                layout_expansion.layoutParams.height,
                250
            )
        } else {
            com.example.bedashingapp.utils.AnimationUtils.slideView(
                layout_expansion,
                layout_expansion.layoutParams.height,
                0
            )
        }
    }


}