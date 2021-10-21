package com.example.bedashingapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import com.example.bedashingapp.helper.SessionManager
import com.example.bedashingapp.helper.ViewModelFactory
import com.example.bedashingapp.utils.Status
import com.example.bedashingapp.viewmodel.MainActivityViewModel
import com.example.bedashingapp.views.dashboard.DashboardFragment
import com.example.bedashingapp.views.login.LoginActivity
import com.example.bedashingapp.views.update_branch.UpdateBranchFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    var navController: NavController? = null


    private lateinit var mainActivityViewModel: MainActivityViewModel
    private var sessionManager: SessionManager? = null

    private var globalProgressLayout: RelativeLayout? = null
    private var globalProgressBar: ProgressBar? = null
    private var progressErrorTxt: TextView? = null

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
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(applicationContext)

        globalProgressLayout = findViewById(R.id.progress_bar_for_problematic_resync)
        globalProgressBar = findViewById(R.id.progress_bar)
        progressErrorTxt = findViewById(R.id.progress_txt)

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


        //so that view model can work in fragment dashboard because it is start destination  of Navigation Component
        navigateToFragmentDashboard()

        if (sessionManager!!.isSynced()) {

        } else {
            //Syncing process

            //check if branch is updated or not
            //if it is updated then start syncing directly
            if (sessionManager!!.getUserBplid().isNotEmpty() && sessionManager!!.getWareHouseID()
                    .isNotEmpty()
            ) {
                syncingProcess()
            } else {
                navigateToFragmentUpdateBranch()
            }
        }

    }


    private fun syncingProcess() {
        sessionManager!!.putIsSynced(false)
        showProgressBar("Syncing...")


    }


    //for posting documents


    private fun updateStatusOfDocument(id: String, status: String, response: String) {
        mainActivityViewModel.updateStatusOfDocument(id, status, response).observe(this, Observer {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val fm = supportFragmentManager
        val navHostFragment = fm.findFragmentById(R.id.nav_host_fragment)
        val fragment = navHostFragment?.childFragmentManager?.fragments?.get(0)
        if (fragment != null) {
//            if (fragment is POItemScanFragment)
//                fragment.onActivityResult(requestCode, resultCode, data)

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
//            if (fragment is POItemScanFragment) {
//                Navigation.findNavController(this, R.id.nav_host_fragment)
//                    .navigate(R.id.nav_po_list, Bundle())
//            }
//            else {
//                super.onBackPressed()
//            }
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
                showToastShort("Please update required details")
            }


        }
    }

    private fun eligibleForHandlingBackPress(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val fragment = navHostFragment?.childFragmentManager?.fragments?.get(0)
        return when (fragment) {
            is DashboardFragment,
            is UpdateBranchFragment
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
            is UpdateBranchFragment
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
                showToastShort("Please update required details")
            }

        }
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


}