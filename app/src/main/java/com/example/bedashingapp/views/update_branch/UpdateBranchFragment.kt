package com.example.bedashingapp.views.update_branch

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.bedashingapp.BaseFragment
import com.example.bedashingapp.R
import com.example.bedashingapp.data.api.ApiHelper
import com.example.bedashingapp.data.api.RetrofitBuilder
import com.example.bedashingapp.helper.SessionManager
import com.example.bedashingapp.helper.ViewModelFactory
import com.example.bedashingapp.utils.Status
import com.example.bedashingapp.viewmodel.MainActivityViewModel
import com.example.bedashingapp.views.login.LoginActivity

class UpdateBranchFragment : BaseFragment() {

    private lateinit var mainActivityViewModel: MainActivityViewModel
    private var sessionManager: SessionManager? = null

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

        checkSessionConnection()
    }



    private fun checkSessionConnection(){
        if(isConnectedToNetwork()) {
            mainActivityViewModel.checkConnection(
                sessionManager!!.getBaseURL(),
                sessionManager!!.getCompany(),
                sessionManager!!.getSessionId(),
                sessionManager!!.getUserId()
            ).observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {

                        }
                        Status.LOADING-> {
                            showProgressBar("", "")
                        }
                        Status.ERROR->{
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
        }else{
            showToastLong(resources.getString(R.string.network_not_connected_msg))
        }
    }


    private fun getData(){

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