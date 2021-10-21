package com.example.bedashingapp.views.server_settings

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.example.bedashingapp.BaseActivity
import com.example.bedashingapp.R
import com.example.bedashingapp.data.api.ApiHelper
import com.example.bedashingapp.data.api.RetrofitBuilder
import com.example.bedashingapp.helper.SessionManager
import com.example.bedashingapp.helper.ViewModelFactory
import com.example.bedashingapp.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_server_settings.*

class ServerSettingsActivity : BaseActivity() {

    private var sessionManager: SessionManager? = null
    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onBackPressed() {
        this.finish()
        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_settings)

        sessionManager = SessionManager(applicationContext)

        setUpViewModel()

        btn_back.setOnClickListener {
            this.finish()
            this.onBackPressed()
        }

        //fetching data if exists
        et_serverip.setText(sessionManager!!.getServer())
        et_port.setText(sessionManager!!.getPort())
        et_company.setText(sessionManager!!.getCompany())


        btn_save_sv_settings.setOnClickListener{
            if(validate()){
                sessionManager!!.setServer(et_serverip.text.toString())
                sessionManager!!.setPort(et_port.text.toString())
                sessionManager!!.setCompany(et_company.text.toString())
                showToastLong("Server configurations successfully saved")
            }
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

    private fun validate(): Boolean{
        if(et_serverip.text.toString().isEmpty() || !et_serverip.text.toString().matches(("^((http://)|(https://))(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\$").toString().toRegex())){
            showToastShort("Please enter valid Server IP")
            return false
        }
        if(et_port.text.toString().isEmpty()){
            showToastShort("Please enter Port")
            return false
        }
        if(et_company.text.toString().isEmpty()){
            showToastShort("Please enter company name")
            return false
        }
        return true
    }
}