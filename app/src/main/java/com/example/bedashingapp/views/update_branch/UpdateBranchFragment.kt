package com.example.bedashingapp.views.update_branch

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.bedashingapp.BaseFragment
import com.example.bedashingapp.MainActivity
import com.example.bedashingapp.R
import com.example.bedashingapp.data.api.ApiHelper
import com.example.bedashingapp.data.api.RetrofitBuilder
import com.example.bedashingapp.data.model.local.PreviousUserBranch
import com.example.bedashingapp.data.model.remote.Branch
import com.example.bedashingapp.data.model.remote.Warehouse
import com.example.bedashingapp.helper.SessionManager
import com.example.bedashingapp.helper.ViewModelFactory
import com.example.bedashingapp.utils.Status
import com.example.bedashingapp.viewmodel.MainActivityViewModel
import com.example.bedashingapp.views.login.LoginActivity
import kotlinx.android.synthetic.main.fragment_update_branch.*

class UpdateBranchFragment : BaseFragment() {

    private lateinit var mainActivityViewModel: MainActivityViewModel
    private var sessionManager: SessionManager? = null


    private var branchesList: ArrayList<Branch> = ArrayList()
    private var warehousesList: ArrayList<Warehouse> = ArrayList()
    private var spinnerWarehousesList: ArrayList<Warehouse> = ArrayList()

    private var defaultVendorID: String = ""


    override fun getLayout(): Int {
        return R.layout.fragment_update_branch
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        sessionManager = SessionManager(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (context as MainActivity).checkSessionConnection(this,getString(R.string.get_data))

        spinner_branch.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    spinnerWarehousesList.clear()
                    spinnerWarehousesList.addAll(warehousesList.filter { it.WarehouseCode == null || it.BusinessPlaceID == branchesList[position].BPLID })

                    val adapter =
                        ArrayAdapter(requireContext(), R.layout.spinner_row, spinnerWarehousesList)
                    spinner_warehouse.adapter = adapter
                    spinner_warehouse.setTitle("Warehouses")

                    defaultVendorID = branchesList[position].DefaultVendorID

                    layout_warehouse.visibility = View.VISIBLE
                } else {
                    defaultVendorID = ""
                    layout_warehouse.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        btn_save_branch_details.setOnClickListener {
            if (validate()) {

                val branchCode = branchesList[spinner_branch.selectedItemPosition].BPLID.toString()
                val branchName = branchesList[spinner_branch.selectedItemPosition].BPLName
                val warehouseCode =
                    spinnerWarehousesList[spinner_warehouse.selectedItemPosition].WarehouseCode.toString()
                val warehouseName =
                    spinnerWarehousesList[spinner_warehouse.selectedItemPosition].WarehouseName
                val previousUserBranch = PreviousUserBranch(
                    sessionManager!!.getCurrentUserName(),
                    branchesList[spinner_branch.selectedItemPosition],
                    spinnerWarehousesList[spinner_warehouse.selectedItemPosition]
                )
                sessionManager!!.setPreviousBranch(previousUserBranch)
                sessionManager!!.setUserBPLID(branchCode)
                sessionManager!!.setUserBranch(branchCode)
                sessionManager!!.setUserBranchName(branchName)
                sessionManager!!.putWareHouseID(warehouseCode)
                sessionManager!!.setUserDefaultWhs(warehouseName)
                sessionManager!!.putWareHouseName(warehouseName)
                sessionManager!!.setUserHeadOfficeCardCode(defaultVendorID)

                sessionManager!!.putIsSynced(false)
                (requireActivity() as MainActivity).reloadActivity()
            }
        }
    }

    override fun invoke(purpose: String) {
        when (purpose) {
            requireContext().resources.getString(R.string.get_data) -> {
            getData()
            }
        }
    }


    private fun getData() {
        branchesList.add(Branch(null, "", "Select a Branch", "", "", ""))
        warehousesList.add(Warehouse(null, "Select a Warehouse", -985))

        //fetching branches and warehouses
        getBranches()
    }

    private fun getBranches() {
        mainActivityViewModel.getBranches(
            sessionManager!!.getBaseURL(),
            sessionManager!!.getCompany(),
            sessionManager!!.getSessionId()
        ).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        branchesList.addAll(resource.data?.value as ArrayList)
                        getWarehouses()
                    }
                    Status.LOADING -> {

                    }
                    Status.ERROR -> {
                        hideProgressBar()
                        showToastLong(resource.message!!)
                    }
                }
            }
        })
    }

    private fun getWarehouses() {
        mainActivityViewModel.getWarehouses(
            sessionManager!!.getBaseURL(),
            sessionManager!!.getCompany(),
            sessionManager!!.getSessionId()
        ).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        warehousesList.addAll(resource.data?.value as ArrayList)
                        hideProgressBar()
                        setData()
                    }
                    Status.LOADING -> {

                    }
                    Status.ERROR -> {
                        hideProgressBar()
                        showToastLong(resource.message!!)
                    }
                }
            }
        })
    }


    private fun setData() {
        //setting data if it was saved before
        et_branch.setText(branchesList.find { it.BPLID.toString() == sessionManager!!.getUserBplid() }?.BPLName)
        et_warehouse.setText(sessionManager!!.getWareHouseName())
        et_vendor.setText(sessionManager!!.getUserHeadOfficeCardCode())

        //populating branches spinner
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_row, branchesList)
        spinner_branch.adapter = adapter
        spinner_branch.setTitle("Branches")
    }


    private fun validate(): Boolean {
        if (spinner_branch.selectedItemPosition == -1 || branchesList[spinner_branch.selectedItemPosition].BPLID == null) {
            showToastShort("Please select a branch")
            return false
        }
        if (spinner_warehouse.selectedItemPosition == -1 || spinnerWarehousesList[spinner_warehouse.selectedItemPosition].WarehouseCode == null) {
            showToastShort("Please select a warehouse")
            return false
        }
        if (defaultVendorID.isEmpty()) {
            showToastShort("Branch does not have a default vendor!")
            return false
        }
        return true

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