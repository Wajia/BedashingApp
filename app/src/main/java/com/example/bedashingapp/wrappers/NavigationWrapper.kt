package com.sixlogics.flexspace.wrappers

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.example.bedashingapp.MainActivity
import com.example.bedashingapp.R


class NavigationWrapper {


    companion object {
        @SuppressLint("StaticFieldLeak")
        private var navController: NavController? = null
        private const val VIEW_ID = R.id.nav_host_fragment

        //    @SuppressLint("StaticFieldLeak")
        private var wrapper: NavigationWrapper? = null

        fun init(activity: Activity?) {
            if (activity != null) {
                navController = Navigation.findNavController(activity, VIEW_ID)
            }
        }

        fun getInstance(): NavigationWrapper? {
            if (wrapper == null) {
                wrapper = NavigationWrapper()
            }
            return wrapper
        }

        fun navigateToFragmentDashboard(isReady: Boolean) {
            val bundle = Bundle()
            bundle.putBoolean("ready", isReady)
            if (navController != null) {
                navController!!.navigate(R.id.nav_dashboard, bundle)
            }
        }

        fun navigateToFragmentUpdateBranch() {
            if (navController != null) {
                navController!!.navigate(R.id.nav_update_branch)
            }
        }

        fun navigateToFragmentUpdatePurchaseOrders() {
            if (navController != null) {
                navController!!.navigate(R.id.nav_purchase_order)
            }
        }

        fun navigateToFragmentPurchaseOrdersList() {
            if (navController != null) {
                navController!!.navigate(R.id.nav_purchase_oder_listing)
            }
        }

        fun navigateToFragmentInventoryCounting() {
            if (navController != null) {
                navController!!.navigate(R.id.nav_inventory_countings)
            }
        }

        fun navigateToFragmentGoodsReceipt(bundle: Bundle = Bundle()) {
            if (navController != null) {
                navController!!.navigate(R.id.nav_good_receipt)
            }
        }
        fun navigateToFragmentProfessionalCheckOut(bundle: Bundle = Bundle()) {
            if (navController != null) {
                navController!!.navigate(R.id.nav_professional_checkout)
            }
        }


    }
}