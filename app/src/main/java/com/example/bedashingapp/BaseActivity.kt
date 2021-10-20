package com.example.bedashingapp

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.view.View
import android.widget.TextView

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

open class BaseActivity: AppCompatActivity() {


    fun showToastShort(msg: String){
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
    }

    fun showToastLong(msg: String){
        Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
    }

    open fun hideMyKeyBoard() {
        this.hideKeyBoard(currentFocus)
    }

    protected fun isConnectedToNetwork(): Boolean{
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    protected fun showSnackBar(msg: String, rootLayout: View?){
        var mySnackBar: Snackbar = Snackbar.make(rootLayout!!, msg, Snackbar.LENGTH_INDEFINITE)
        mySnackBar.setAction("OK", View.OnClickListener {

            mySnackBar.dismiss()
        })

        //Changing background color of snackBar.
        mySnackBar.view.setBackgroundColor(resources.getColor(R.color.primvar))

        //Changing textColor of message
        val mainTextView = mySnackBar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        mainTextView.setTextColor(Color.WHITE)

        //Changing textColor, backGroundColor of action
        var actionTextView = mySnackBar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action)
        actionTextView.setTextColor(Color.BLACK)
        actionTextView.setBackgroundColor(resources.getColor(R.color.appbar_color))
        actionTextView.setPadding(80,0,80,0)
        actionTextView.textSize = 15F


        mySnackBar.show()

    }


}