package com.example.bedashingapp

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import butterknife.ButterKnife
import butterknife.Unbinder
import com.google.android.material.snackbar.Snackbar
import java.util.*

abstract class BaseFragment: Fragment() {

    private var unbinder: Unbinder? = null
    private var globalProgressLayout: RelativeLayout? = null
    private var globalProgressBar: ProgressBar? = null
    private var progressErrorTxt: TextView? = null

    @Nullable
    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(getLayout(), null)
        unbinder = ButterKnife.bind(this, view)
        globalProgressLayout = view.findViewById(R.id.progress_bar_layout)
        globalProgressBar = view.findViewById(R.id.progress_bar)
        progressErrorTxt = view.findViewById(R.id.progress_txt)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)
    }

    open fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view: View? = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    protected open fun setupViews(view: View) {

    }

    protected abstract fun getLayout(): Int

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onDestroyView() {
        unbinder!!.unbind()
        super.onDestroyView()
    }


    open fun getBaseActivity(): BaseActivity {
        return activity as BaseActivity
    }



    open fun showToastShort(message: String) {
        getBaseActivity().showToastShort(message)
    }

    open fun showToastLong(message: String){
        getBaseActivity().showToastLong(message)
    }

    open fun showAlert(title: String, message:String, fragmentId: Int, bundle: Bundle? = null){
        val builder = AlertDialog.Builder(ContextThemeWrapper(requireActivity(),R.style.MypopUp))
        //set title for alert dialog
        builder.setTitle(title)
        //set message for alert dialog
        builder.setMessage(message)
        //performing positive action
        builder.setPositiveButton("Ok"){ _, _ ->
            if(bundle == null){
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(
                        fragmentId,Bundle())
            }else {
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(
                        fragmentId, bundle)
            }
        }

        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }



    protected fun showSnackBar(msg: String, rootLayout: View?, fragmentId: Int){
        var mySnackBar: Snackbar = Snackbar.make(rootLayout!!, msg, Snackbar.LENGTH_INDEFINITE)
        mySnackBar.setAction("OK", View.OnClickListener {

            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(
                fragmentId,Bundle())
            mySnackBar.dismiss()
        })

        //Changing background color of snackBar.
        mySnackBar.view.setBackgroundColor(resources.getColor(R.color.primvar))

        //Changing textColor of message
        val mainTextView = mySnackBar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        mainTextView.setTextColor(Color.WHITE)
        if (Build.VERSION.SDK_INT>=26) {
            mainTextView.setAutoSizeTextTypeUniformWithConfiguration(8, 12, 1, 1)
        }

        //Changing textColor, backGroundColor of action
        var actionTextView = mySnackBar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action)
        actionTextView.setTextColor(Color.BLACK)
        actionTextView.setBackgroundColor(resources.getColor(R.color.appbar_color))
        actionTextView.setPadding(80,0,80,0)
        actionTextView.textSize = 15F


        mySnackBar.show()

    }



    protected open fun showProgressBar(title: String?, message: String?) {
        //getBaseActivity().showLoadingBar(title, message);
        globalProgressBar?.visibility = View.VISIBLE
        progressErrorTxt?.visibility = View.VISIBLE
        progressErrorTxt?.text = message
        if (globalProgressLayout != null) {
            globalProgressLayout!!.visibility = View.VISIBLE
        }
    }
    open fun hideProgressBar() {
        if (globalProgressLayout != null && globalProgressLayout!!.visibility == View.VISIBLE) {
            globalProgressLayout!!.visibility = View.GONE
        }
    }


    fun checkPermission(
            permission: Array<String>,
            requestCode: Array<Int> = arrayOf(101)
    ): Boolean {
        var check = true
        val notGPermission: ArrayList<String> = ArrayList()
        val notGPermissionRequest: ArrayList<Int> = ArrayList()
        for (i in permission.indices) {
            if (ContextCompat.checkSelfPermission(
                            requireActivity(),
                            permission[i]
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
//                log(javaClass.name, "Permission for : ${permission[i]} not granted")
                notGPermission.add(permission[i])
                notGPermissionRequest.add(requestCode[0])
                check = false
            } else {
//                log(javaClass.name, "Permission for : ${permission[i]} granted")
            }
        }
        if (!check) {
            requestPermissions(
                    notGPermission.toArray(arrayOfNulls<String>(notGPermission.size)),
                    requestCode[0]
            )
        }
        return check
    }

    protected fun isConnectedToNetwork(): Boolean{
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }



}