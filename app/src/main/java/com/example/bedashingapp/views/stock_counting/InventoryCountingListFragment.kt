package com.example.bedashingapp.views.stock_counting

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bedashingapp.BaseFragment
import com.example.bedashingapp.R
import com.example.bedashingapp.data.api.ApiHelper
import com.example.bedashingapp.data.api.RetrofitBuilder
import com.example.bedashingapp.data.model.remote.InventoryCounting
import com.example.bedashingapp.helper.SessionManager
import com.example.bedashingapp.helper.ViewModelFactory
import com.example.bedashingapp.utils.Status
import com.example.bedashingapp.viewmodel.MainActivityViewModel
import com.example.bedashingapp.views.login.LoginActivity
import com.example.bedashingapp.views.stock_counting.adapter.InventoryCountingAdapter
import kotlinx.android.synthetic.main.fragment_inventory_listing.*
import java.util.*
import kotlin.collections.ArrayList

class InventoryCountingListFragment : BaseFragment() {

    private lateinit var mainActivityViewModel: MainActivityViewModel
    private var sessionManager: SessionManager? = null

    private val inventoryCountingList: ArrayList<InventoryCounting> = ArrayList()

    override fun getLayout(): Int {
        return R.layout.fragment_inventory_listing
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(requireContext())
        setUpViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkSessionConnection()


        float_btn_add_inventory.setOnClickListener {
            mainActivityViewModel.clearSelectedItems()
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(
                R.id.nav_inventory_counting, Bundle()
            )
        }
    }

    override fun apiCaller(purpose: String) {
        TODO("Not yet implemented")
    }


    private fun checkSessionConnection() {
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
                            getInventoryCountings()
                        }
                        Status.LOADING -> {
                            showProgressBar("", "Fetching Inventory Counting...")
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


    private fun getInventoryCountings() {
        mainActivityViewModel.getInventoryCountings(
            sessionManager!!.getBaseURL(),
            sessionManager!!.getCompany(),
            sessionManager!!.getSessionId(),
            sessionManager!!.getUserBplid()
        ).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideProgressBar()
                        inventoryCountingList.clear()
                        inventoryCountingList.addAll(resource.data!!.value)
                        setRecyclerView()
                    }
                    Status.LOADING -> {
                        showProgressBar("", "Fetching Inventory Counting...")
                    }
                    Status.ERROR -> {
                        showToastLong(resource.message!!)
                        hideProgressBar()
                    }
                }
            }
        })
    }

    private var adapter: InventoryCountingAdapter? = null
    private fun setRecyclerView() {
        adapter = InventoryCountingAdapter(inventoryCountingList)
        rv_inventory_countings.adapter = adapter
        rv_inventory_countings.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        rv_inventory_countings.layoutManager = layoutManager
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




}