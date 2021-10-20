package com.example.bedashingapp

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun Activity.hideKeyBoard(view: View? = null) {
    var view = view
    if(view == null){
        view = currentFocus
    }
    if (view != null) {
        val imm = getSystemService(
            Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}