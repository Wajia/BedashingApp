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
            setContentView(R.layout.activity_main)
        }
    }


    private fun login(username: String, pw: String) {
//        if(username == "admin" && pw == "admin"){
//            sessionManager?.putIsSynced(false)
//            sessionManager?.putIsLoggedIn(true)
//            navigateToDashboard()
//        }else{
//            showToastShort("Incorrect username/ password")
//        }

        if (isConnectedToNetwork()) {
            mainActivityViewModel.login().observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val employee = resource.data?.d?.results?.find { employee -> employee.EmployeeCommon[0].UserName_KUT == username && employee.EmployeeCommon[0].Password_KUT == pw }
                            if (employee != null){
                                hideProgressBar()
                                sessionManager?.putEmployeeID(employee.EmployeeID)
                                sessionManager?.putIsSynced(false)
                                sessionManager?.putIsLoggedIn(true)
                                navigateToDashboard()
                            }else{
                                hideProgressBar()
                                showToastShort("Incorrect username/ password")
                            }
                        }
                        Status.LOADING -> {
                            showProgressBar("Signing in")
                        }
                        Status.ERROR -> {
                            hideProgressBar()
                            showToastLong(resource.message!!)
                        }
                    }
                }
            })
        } else {
            showToastLong(resources.getString(R.string.network_not_connected_msg))
        }
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