package com.example.bedashingapp.views.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.bedashingapp.BaseActivity
import com.example.bedashingapp.MainActivity
import com.example.bedashingapp.R
import com.example.bedashingapp.data.api.ApiHelper
import com.example.bedashingapp.data.api.RetrofitBuilder
import com.example.bedashingapp.helper.SessionManager
import com.example.bedashingapp.helper.ViewModelFactory
import com.example.bedashingapp.utils.Status
import com.example.bedashingapp.viewmodel.MainActivityViewModel
import com.example.bedashingapp.views.server_settings.ServerSettingsActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    private var globalProgressLayout: RelativeLayout? = null
    private var globalProgressBar: ProgressBar? = null
    private var progressErrorTxt: TextView? = null

    private var sessionManager: SessionManager? = null
    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(applicationContext)

        globalProgressLayout = findViewById(R.id.progress_bar_for_problematic_resync)
        globalProgressBar = findViewById(R.id.progress_bar)
        progressErrorTxt = findViewById(R.id.progress_txt)


        //check if user is logged in or not
        if (sessionManager!!.isLoggedIn()) {
            navigateToDashboard()
        } else {
            setUpViewModel()
        }

        btn_click_me.setOnClickListener {
            if (et_userName.text.toString().isNotEmpty() && et_password.text.toString()
                    .isNotEmpty()
            ) {
                login(et_userName.text.toString(), et_password.text.toString())
            } else {
                showToastShort("Please enter username/ password")
            }
        }

        btn_server_settings.setOnClickListener {
            startActivity(Intent(this, ServerSettingsActivity::class.java))
        }


//       ************************** For Wajeeha *****************************
        btn_test.setOnClickListener{
            //change layout here
            setContentView(R.layout.fragment_update_branch)
        }
    }


    private fun login(username: String, pw: String) {
        if(isConnectedToNetwork()) {
            mainActivityViewModel.login(
                sessionManager!!.getBaseURL(),
                sessionManager!!.getCompany(),
                pw,
                username = username
            ).observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            if(resource.data!!.error == null) {
                                sessionManager!!.setSessionId(resource.data.SessionId)
                                sessionManager!!.setSessionTimeOut(resource.data.SessionTimeout!!)
                                getUserDetails(username, pw)
                            }else{
                                hideProgressBar()
                                showToastLong(resource.data.error!!.message.value)
                            }
                        }
                        Status.LOADING -> {
                            showProgressBar("Signing in...")
                        }
                        Status.ERROR -> {
                            hideProgressBar()
                            if(resource.data?.error == null) {
                                showToastLong(resource.message!!)
                            }else {
                                showToastLong(resource.data.error.message.value)
                            }
                        }
                    }
                }
            })
        }else{
            showToastLong(resources.getString(R.string.network_not_connected_msg))
        }
    }

    private fun getUserDetails(userCode: String, password: String){
        mainActivityViewModel.getUserDetails(
            sessionManager!!.getBaseURL(),
            sessionManager!!.getCompany(),
            sessionManager!!.getSessionId(),
            userCode).observe(
            this,
            Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            hideProgressBar()
                            if (resource.data != null && resource.data.value.isNotEmpty()) {
                                sessionManager!!.putIsLoggedIn(true)

                                val userData = resource.data.value[0]
                                //now saving user details
                                sessionManager!!.putCurrentUserName(userCode)
                                sessionManager!!.putCurrentPassword(password)
                                sessionManager!!.setUserId(userData.InternalKey.toString())
                                sessionManager!!.setUserCode(userData.UserCode)
                                sessionManager!!.setName(userData.UserName)
                                sessionManager!!.setUserEmail(userData.eMail)
                                sessionManager!!.setUserPhone(userData.MobilePhoneNumber)
                                sessionManager!!.setUserSuperUser(userData.Superuser)
                                sessionManager!!.setUserBranch(userData.Branch)
                                sessionManager!!.setUserDepartment(userData.Department)
                                sessionManager!!.setUserLocked(userData.Locked)
                                sessionManager!!.setUserGroup(userData.Group)
                                sessionManager!!.setUserDfltRegion(userData.U_DfltRegn)
                                sessionManager!!.setUserDfltStore(userData.U_DfltStor)


                                //now check if the new user logged in is same as previous user
                                //if same then no syncing required
                                //else syncing required

                                if(sessionManager!!.isPreviousUser()){
                                    sessionManager!!.putIsSynced(sessionManager!!.isSynced())
                                    sessionManager!!.setUserBPLID(sessionManager!!.getUserBplid())
                                    sessionManager!!.putWareHouseID(sessionManager!!.getWareHouseID())
                                }else{
                                    sessionManager!!.putIsSynced(false)
                                    sessionManager!!.setUserBPLID("")
                                    sessionManager!!.putWareHouseID("")
                                    sessionManager!!.putWareHouseName("")
                                    sessionManager!!.setUserHeadOfficeCardCode("")
                                }
                                navigateToDashboard()
                            } else {
                                showToastLong("User is not configured. Please contact Head Office")
                            }
                        }
                        Status.LOADING -> {
                            showProgressBar("Fetching User Details...")
                        }
                        Status.ERROR -> {
                            hideProgressBar()
                            showToastLong(resource.message!!)
                        }
                    }
                }
            })
    }

    private fun navigateToDashboard() {
        startActivity(Intent(applicationContext, MainActivity::class.java))
        finish()
    }


    private fun showProgressBar(message: String) {
        globalProgressBar?.visibility = View.VISIBLE
        progressErrorTxt?.visibility = View.VISIBLE
        progressErrorTxt?.text = message
        if (globalProgressLayout != null) {
            globalProgressLayout!!.visibility = View.VISIBLE
        }
    }

    private fun hideProgressBar() {
        if (globalProgressLayout != null && globalProgressLayout!!.visibility == View.VISIBLE) {
            globalProgressLayout!!.visibility = View.GONE
        }
    }

    private fun setUpViewModel() {
        mainActivityViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelper(RetrofitBuilder.getApiService("dynamic ip here")),
                application
            )
        ).get(MainActivityViewModel::class.java)
    }


}