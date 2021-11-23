package com.example.bedashingapp.views.GoodsReciveng.Fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bedashingapp.BaseFragment
import com.example.bedashingapp.MainActivity
import com.example.bedashingapp.R
import com.example.bedashingapp.data.model.remote.PurchaseOder
import com.example.bedashingapp.utils.Status
import com.example.bedashingapp.views.GoodsReciveng.Adapters.PurchaseOrderAdapter
import kotlinx.android.synthetic.main.fragment_purchase_order_listing.*


class PurchaseOrderListingFragment : BaseFragment() {

    lateinit var poAdapter: PurchaseOrderAdapter
    private var poList: ArrayList<PurchaseOder> = ArrayList()
    override fun getLayout(): Int {
        return R.layout.fragment_purchase_order_listing
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        if ((context as MainActivity).checkSessionConnection("")) {
        var  userBranch=  (context as MainActivity).sessionManager!!.getUserBranch()
            var headOfficeCardCode = (context as MainActivity).sessionManager!!.getUserHeadOfficeCardCode()
            var  company=  (context as MainActivity).sessionManager!!.getCompany()
            getPurchaseOrders()
        }
    }

    private fun getPurchaseOrders() {
        (context as MainActivity).mainActivityViewModel.getPO(
            (context as MainActivity).sessionManager!!.getBaseURL(),
            (context as MainActivity).sessionManager!!.getSessionId(),
            (context as MainActivity).sessionManager!!.getCompany(),
            (context as MainActivity).sessionManager!!.getUserBranch(),
            (context as MainActivity).sessionManager!!.getUserHeadOfficeCardCode()
        ).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideProgressBar()
                        poList.addAll(it.data!!.value)
                        setRecyclerView()
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

    private fun setRecyclerView() {
        poAdapter = PurchaseOrderAdapter(
            requireContext(), poList
        )
        rv_purchase_oder.adapter = poAdapter
        rv_purchase_oder.setHasFixedSize(true)
        rv_purchase_oder.layoutManager = LinearLayoutManager(context)
    }

}