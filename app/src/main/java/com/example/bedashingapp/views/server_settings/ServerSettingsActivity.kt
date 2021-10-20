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