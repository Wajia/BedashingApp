package com.example.bedashingapp.utils

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.view.ContextThemeWrapper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.bedashingapp.MainActivity
import com.example.bedashingapp.R
import com.example.bedashingapp.data.model.remote.CustomObject
import com.example.bedashingapp.views.interfaces.SingleButtonListener
import com.example.bedashingapp.views.stock_counting.InventoryStatusDialogFragment
import com.google.zxing.integration.android.IntentIntegrator
import java.text.SimpleDateFormat
import java.util.*


fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}


fun getCurrentTime(format: String): String {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    return sdf.format(Date())
}


fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}


internal fun Context.getDrawableCompat(@DrawableRes drawable: Int) =
    ContextCompat.getDrawable(this, drawable)

internal fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)

internal fun TextView.setTextColorRes(@ColorRes color: Int) =
    setTextColor(context.getColorCompat(color))


fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.initiateScanFragment(portraitCaptureActivity: Class<*>?) {
    val integrator = IntentIntegrator.forSupportFragment(this)
    integrator.setOrientationLocked(true)
    integrator.captureActivity = portraitCaptureActivity
    integrator.initiateScan()
}


fun showConfirmationAlert(
    context: Context,
    singleButtonListener: SingleButtonListener,
    message: String,
    title: String
) {
    val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.MypopUp))
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setPositiveButton(context.getString(R.string.yes)) { _, _ ->
        if (title.equals(context.getString(R.string.post_document), true)) {
            singleButtonListener.onButtonClick(context.getString(R.string.post_document), 0)
        }

    }
    builder.setNegativeButton(context.getString(R.string.no)) { _, _ ->
        singleButtonListener.onButtonClick(context.getString(R.string.no), 0)
    }

    // Create the AlertDialog
    val alertDialog: AlertDialog = builder.create()
    // Set other dialog properties
    alertDialog.setCancelable(false)
    alertDialog.show()
}

fun String.changeDateFormat(inputFormatString: String, outputFormatString: String): String = try {
    SimpleDateFormat(outputFormatString, Locale.US).format(
        SimpleDateFormat(
            inputFormatString,
            Locale.US
        ).parse(this)!!
    )
} catch (e: Exception) {
    this
}


fun openInventoryStatusDialog(data: List<CustomObject>, context: Context) {
    val dialog = InventoryStatusDialogFragment(data)
    dialog.isCancelable = true
    dialog.show((context as MainActivity).supportFragmentManager, dialog.tag)
}

fun openDatePickerDialog(context: Context, textView: TextView) {
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)


    val pickerDialog = DatePickerDialog(
        context,
        R.style.datepicker,
        { view, year, monthOfYear, dayOfMonth ->
            textView.setText(
                "${String.format("%02d", dayOfMonth)}-${
                    String.format(
                        "%02d",
                        monthOfYear
                    )
                }-$year"
            )
        },
        year,
        month,
        day
    )

    pickerDialog.show()
}
