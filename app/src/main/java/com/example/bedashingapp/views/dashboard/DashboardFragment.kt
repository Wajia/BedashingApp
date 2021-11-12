package com.example.bedashingapp.views.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.example.bedashingapp.BaseFragment
import com.example.bedashingapp.R
import com.example.bedashingapp.data.api.ApiHelper
import com.example.bedashingapp.data.api.RetrofitBuilder
import com.example.bedashingapp.helper.SessionManager
import com.example.bedashingapp.helper.ViewModelFactory
import com.example.bedashingapp.utils.Status
import com.example.bedashingapp.viewmodel.MainActivityViewModel
import com.example.bedashingapp.views.login.LoginActivity
import com.sixlogics.flexspace.wrappers.NavigationWrapper
import kotlinx.android.synthetic.main.fragment_dashboard.*


/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mainActivityViewModel: MainActivityViewModel
    private var sessionManager: SessionManager? = null

    override fun getLayout(): Int {
        return R.layout.fragment_dashboard
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            sessionManager = SessionManager(requireContext())
            setUpViewModel()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
//            setupObserver()


            tv_welcome.text = "WELCOME, ${sessionManager!!.getUserName()}"

            checkSessionConnection()
            setupObserver()

            btn_stock_counting.setOnClickListener {
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(
                    R.id.nav_inventory_countings, Bundle()
                )
            }
            btn_purchase_orders.setOnClickListener(this)

        }

    }

    private fun setupObserver() {
        mainActivityViewModel.reloadDocumentsFlagLiveData.observe(viewLifecycleOwner, Observer {
            if (it) {
                checkSessionConnection()
                mainActivityViewModel.setReloadDocumentsFlag(false)
            }
        })
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
                            hideProgressBar()
                            getPOCount()
                            getGPROCount()
                            getDeliveryCount()
                            getInventoryCount()
                        }
                        Status.LOADING -> {
                            showProgressBar("", "")
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

    private fun getPOCount() {
        mainActivityViewModel.getPOCount(
            sessionManager!!.getBaseURL(),
            sessionManager!!.getCompany(),
            sessionManager!!.getSessionId(),
            sessionManager!!.getUserBplid(),
            sessionManager!!.getUserHeadOfficeCardCode()
        ).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        tv_po_count.text = resource.data.toString()
                    }
                    Status.LOADING -> {

                    }
                    Status.ERROR -> {
                        showToastLong(resource.message!!)
                        tv_po_count.text = "error"
                    }
                }
            }
        })
    }

    private fun getGPROCount() {
        mainActivityViewModel.getGRPOCount(
            sessionManager!!.getBaseURL(),
            sessionManager!!.getCompany(),
            sessionManager!!.getSessionId(),
            sessionManager!!.getUserBplid(),
            sessionManager!!.getUserHeadOfficeCardCode()
        ).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        tv_goods_receipt_note_count.text = resource.data.toString()
                    }
                    Status.LOADING -> {

                    }
                    Status.ERROR -> {
                        showToastLong(resource.message!!)
                        tv_goods_receipt_note_count.text = "error"
                    }
                }
            }
        })
    }

    private fun getDeliveryCount() {
        mainActivityViewModel.getDeliveryCount(
            sessionManager!!.getBaseURL(),
            sessionManager!!.getCompany(),
            sessionManager!!.getSessionId(),
            sessionManager!!.getUserBplid()
        ).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        tv_delivery_count.text = resource.data.toString()
                    }
                    Status.LOADING -> {

                    }
                    Status.ERROR -> {
                        showToastLong(resource.message!!)
                        tv_delivery_count.text = "error"
                    }
                }
            }
        })
    }

    private fun getInventoryCount() {
        mainActivityViewModel.getInventoryCount(
            sessionManager!!.getBaseURL(),
            sessionManager!!.getCompany(),
            sessionManager!!.getSessionId(),
            sessionManager!!.getUserBplid()
        ).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        tv_stock_count.text = resource.data.toString()
                    }
                    Status.LOADING -> {

                    }
                    Status.ERROR -> {
                        showToastLong(resource.message!!)
                        tv_stock_count.text = "error"
                    }
                }
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

    override fun onClick(view: View?) {
        if (view == btn_purchase_orders) {
            NavigationWrapper.navigateToFragmentUpdatePurchaseOrders()
        }
    }

}