package com.example.bedashingapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.*
import com.example.bedashingapp.data.api.ApiHelper
import com.example.bedashingapp.data.api.RetrofitBuilder
import com.example.bedashingapp.data.model.remote.AddInventoryCountingResponse
import com.example.bedashingapp.data.model.remote.InventoryCountingRequest
import com.example.bedashingapp.helper.SessionManager
import com.example.bedashingapp.helper.ViewModelFactory
import com.example.bedashingapp.utils.Constants
import com.example.bedashingapp.utils.Status
import com.example.bedashingapp.viewmodel.MainActivityViewModel
import com.example.bedashingapp.views.dashboard.DashboardFragment
import com.example.bedashingapp.views.login.LoginActivity
import com.example.bedashingapp.views.stock_counting.InventoryCountingListFragment
import com.example.bedashingapp.views.stock_counting.StockCountingFragment
import com.example.bedashingapp.views.update_branch.UpdateBranchFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : BaseActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    var navController: NavController? = null


    private lateinit var mainActivityViewModel: MainActivityViewModel
    private var sessionManager: SessionManager? = null

    private var globalProgressLayout: RelativeLayout? = null
    private var globalProgressBar: ProgressBar? = null
    private var progressErrorTxt: TextView? = null

    private var dataCount: Int = 0

    override fun onBackPressed() {
        if (eligibleForHandlingBackPress()) {
            handleBackPress()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
//                sessionManager!!.putIsSynced(false)
                sessionManager!!.putIsLoggedIn(false)
                sessionManager!!.putPreviousPassword(sessionManager!!.getCurrentPassword())
                sessionManager!!.putPreviousUserName(sessionManager!!.getCurrentUserName())

                startActivity(Intent(this, LoginActivity::class.java))
                finishAffinity()
                return true
            }
            R.id.action_resync -> {
                syncingProcess()
            }
            R.id.action_completed_docs -> {
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(
                    R.id.nav_completed_docs, Bundle()
                )
            }
            android.R.id.home -> {
                if (eligibleForHandlingBackPressForToolbar()) {
                    handleBackPressForToolbar()
                    return true
                }
            }
            R.id.action_settings -> {
                navigateToFragmentUpdateBranch()
            }
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(applicationContext)

        globalProgressLayout = findViewById(R.id.progress_bar_for_problematic_resync)
        globalProgressBar = findViewById(R.id.progress_bar)
        progressErrorTxt = findViewById(R.id.progress_txt_1)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        setUpViewModel()


        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }


        //code for enabling side navigation menu.

//        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
//        val navView: NavigationView = findViewById(R.id.nav_view)


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration.Builder(
            navController.graph
        ).build()
        setupActionBarWithNavController(navController, appBarConfiguration)

//        NavigationUI.setupWithNavController(navView, navController)
//        navView.setupWithNavController(navController)


        if (sessionManager!!.isSynced()) {
            //so that view model can work in fragment dashboard because it is start destination  of Navigation Component
            navigateToFragmentDashboard()
        } else {
            //Syncing process

            //check if branch is updated or not
            //if it is updated then start syncing directly
            if (sessionManager!!.getUserBplid().isNotEmpty() && sessionManager!!.getWareHouseID()
                    .isNotEmpty()
            ) {
                //so that view model can work in fragment dashboard because it is start destination  of Navigation Component
                navigateToFragmentDashboard()

                syncingProcess()
            } else {
                navigateToFragmentUpdateBranch()
            }
        }

    }


    private fun syncingProcess() {

        if (isConnectedToNetwork()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            mainActivityViewModel.checkConnection(
                sessionManager!!.getBaseURL(),
                sessionManager!!.getCompany(),
                sessionManager!!.getSessionId(),
                sessionManager!!.getUserId()
            ).observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            dataCount = 0
                            sessionManager!!.putIsSynced(false)
                            showProgressBar("Syncing...")
                            getItemsMaster()
                        }
                        Status.LOADING -> {
                            showProgressBar("")
                        }
                        Status.ERROR -> {
                            hideProgressBar()
                            sessionManager!!.putIsLoggedIn(false)
                            sessionManager!!.putPreviousPassword(sessionManager!!.getCurrentPassword())
                            sessionManager!!.putPreviousUserName(sessionManager!!.getCurrentUserName())

                            startActivity(Intent(this, LoginActivity::class.java))
                            finishAffinity()
                        }
                    }
                }
            })
        } else {
            showToastLong(resources.getString(R.string.network_not_connected_msg))
        }

    }

    private fun getItemsMaster() {
        mainActivityViewModel.getItemsMasterData(
            sessionManager!!.getBaseURL(),
            sessionManager!!.getCompany(),
            sessionManager!!.getSessionId(),
            sessionManager!!.getWareHouseID(),
            dataCount
        ).observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (resource.data!!.value.isNotEmpty()) {
                            dataCount += resource.data.value.size
                            mainActivityViewModel.saveItemsMaster(resource.data.value)
                                .observe(this, Observer { it1 ->
                                    it1?.let { resource1 ->
                                        when (resource1.status) {
                                            Status.SUCCESS -> {
                                                getItemsMaster()
                                            }
                                            Status.LOADING -> {

                                            }
                                            Status.ERROR -> {
                                                showToastLong(resource.message!!)
                                                hideProgressBar()
                                            }
                                        }
                                    }
                                })
                        } else {
                            dataCount = 0
                            getUoms()
                        }
                    }
                    Status.LOADING -> {
                        showProgressBar("Syncing Items...")
                    }
                    Status.ERROR -> {
                        hideProgressBar()
                        showToastLong(resource.message!!)
                    }
                }
            }
        })
    }


    private fun getUoms() {
        mainActivityViewModel.getUoms(
            sessionManager!!.getBaseURL(),
            sessionManager!!.getCompany(),
            sessionManager!!.getSessionId()
        ).observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        mainActivityViewModel.saveUoms(resource.data!!.value)
                            .observe(this, Observer { it1 ->
                                it1?.let { resource1 ->
                                    when (resource1.status) {
                                        Status.SUCCESS -> {
                                            getUomGroups()
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
                    Status.LOADING -> {
                        showProgressBar("Syncing UOMs...")
                    }
                    Status.ERROR -> {
                        hideProgressBar()
                        showToastLong(resource.message!!)
                    }
                }
            }
        })
    }

    private fun getUomGroups() {
        mainActivityViewModel.getUomGroups(
            sessionManager!!.getBaseURL(),
            sessionManager!!.getCompany(),
            sessionManager!!.getSessionId()
        ).observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        mainActivityViewModel.saveUomGroups(resource.data!!.value)
                            .observe(this, Observer { it1 ->
                                it1?.let { resource1 ->
                                    when (resource1.status) {
                                        Status.SUCCESS -> {
                                            getBarcodes()
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
                    Status.LOADING -> {
                        showProgressBar("Syncing UOM Groups...")
                    }
                    Status.ERROR -> {
                        hideProgressBar()
                        showToastLong(resource.message!!)
                    }
                }
            }
        })
    }

    private fun getBarcodes() {
        mainActivityViewModel.getBarcodes(
            sessionManager!!.getBaseURL(),
            sessionManager!!.getCompany(),
            sessionManager!!.getSessionId(),
            dataCount
        ).observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (resource.data!!.value.isNotEmpty()) {
                            dataCount += resource.data.value.size
                            mainActivityViewModel.saveBarcodes(resource.data.value)
                                .observe(this, Observer { it1 ->
                                    it1?.let { resource1 ->
                                        when (resource1.status) {
                                            Status.SUCCESS -> {
                                                getBarcodes()
                                            }
                                            Status.LOADING -> {

                                            }
                                            Status.ERROR -> {
                                                showToastLong(resource.message!!)
                                                hideProgressBar()
                                            }
                                        }
                                    }
                                })
                        } else {
                            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                            sessionManager!!.putIsSynced(true)
                            hideProgressBar()
                            showSnackBar("Database synchronized successfully", drawer_layout)
                        }
                    }
                    Status.LOADING -> {
                        showProgressBar("Syncing Barcodes...")
                    }
                    Status.ERROR -> {
                        hideProgressBar()
                        showToastLong(resource.message!!)
                    }
                }
            }
        })
    }


    //for posting documents




    fun updateStatusOfDocument(id: String, status: String, response: String, newID: String) {
        mainActivityViewModel.updateStatusOfDocument(id, status, response, newID).observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        mainActivityViewModel.setReloadDocumentsFlag(true)
                    }
                }
            }
        })
    }


    override fun onSupportNavigateUp(): Boolean {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return (NavigationUI.navigateUp(navController!!, appBarConfiguration)
                || super.onSupportNavigateUp())
    }


    private fun navigateToFragmentDashboard() {
        var bundle = Bundle()
        bundle.putBoolean("ready", true)
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(
            R.id.nav_dashboard, bundle
        )
    }

    private fun navigateToFragmentUpdateBranch() {
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(
            R.id.nav_update_branch
        )
    }

    fun reloadActivity() {
        finishAffinity()
        startActivity(Intent(applicationContext, MainActivity::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val fm = supportFragmentManager
        val navHostFragment = fm.findFragmentById(R.id.nav_host_fragment)
        val fragment = navHostFragment?.childFragmentManager?.fragments?.get(0)
        if (fragment != null) {
            if (fragment is StockCountingFragment)
                fragment.onActivityResult(requestCode, resultCode, data)

        }
        super.onActivityResult(requestCode, resultCode, data)
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


    private fun showOnExitPrompt(fragment: Fragment) {

        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.MypopUp))
        builder.setTitle("Exit Document")

        val text = "Are you sure you want to exit. Any unsaved information will be lost"

        builder.setMessage(text)

        builder.setPositiveButton("YES") { _, _ ->
            if (fragment is InventoryCountingListFragment) {

            } else {
                super.onBackPressed()
            }
        }
        builder.setNegativeButton("NO") { _, _ ->

        }

        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()

    }

    private fun handleBackPress() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val fragment = navHostFragment?.childFragmentManager?.fragments?.get(0)
        when (fragment) {

            is DashboardFragment -> {
                this.finish()
            }

            is UpdateBranchFragment -> {
                if (sessionManager!!.getUserBplid()
                        .isNotEmpty() && sessionManager!!.getWareHouseID().isNotEmpty()
                ) {
                    navigateToFragmentDashboard()
                } else {
                    showToastShort("Please update required details")
                }
            }

            is StockCountingFragment -> {
                showOnExitPrompt(fragment)
            }


        }
    }

    private fun eligibleForHandlingBackPress(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val fragment = navHostFragment?.childFragmentManager?.fragments?.get(0)
        return when (fragment) {
            is DashboardFragment,
            is UpdateBranchFragment,
            is StockCountingFragment
            -> {
                true
            }
            else -> {
                false
            }
        }
    }

    private fun eligibleForHandlingBackPressForToolbar(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val fragment = navHostFragment?.childFragmentManager?.fragments?.get(0)
        return when (fragment) {
            is UpdateBranchFragment,
            is StockCountingFragment
            -> {
                true
            }
            else -> {
                false
            }
        }
    }

    private fun handleBackPressForToolbar() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val fragment = navHostFragment?.childFragmentManager?.fragments?.get(0)
        when (fragment) {
            is UpdateBranchFragment -> {
                if (sessionManager!!.getUserBplid()
                        .isNotEmpty() && sessionManager!!.getWareHouseID().isNotEmpty()
                ) {
                    navigateToFragmentDashboard()
                } else {
                    showToastShort("Please update required details")
                }
            }

            is StockCountingFragment -> {
                showOnExitPrompt(fragment)
            }

        }
    }

    private fun showProgressBar(message: String) {
        Log.i("PROGRESS_BAR", message)
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


}