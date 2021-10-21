package com.example.bedashingapp.views.dashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.example.bedashingapp.BaseFragment
import com.example.bedashingapp.R
import com.example.bedashingapp.data.api.ApiHelper
import com.example.bedashingapp.data.api.RetrofitBuilder
import com.example.bedashingapp.helper.ViewModelFactory
import com.example.bedashingapp.utils.Status
import com.example.bedashingapp.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.fragment_dashboard.*


/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment : BaseFragment() {

    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun getLayout(): Int {
        return R.layout.fragment_dashboard
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let{
            setUpViewModel()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let{
//            setupObserver()
            btn_goods_receiving.setOnClickListener{

            }

            btn_transfer_order.setOnClickListener{

            }
        }

    }

    private fun setupObserver(){
        
    }

    private fun setUpViewModel() {
        mainActivityViewModel = ViewModelProviders.of(requireActivity(),
            ViewModelFactory(ApiHelper(RetrofitBuilder.getApiService("dynamic ip here")), requireActivity().application)
        ).get(MainActivityViewModel::class.java)
    }

}