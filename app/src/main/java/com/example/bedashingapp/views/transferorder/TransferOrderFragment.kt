package com.example.bedashingapp.views.transferorder

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.bedashingapp.BaseFragment
import com.example.bedashingapp.R
import com.example.bedashingapp.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.fragment_transfer_order.*


/**
 * A simple [Fragment] subclass.
 * Use the [TransferOrderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TransferOrderFragment : BaseFragment() {


    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun getLayout(): Int {
        return R.layout.fragment_transfer_order
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_outbound_tasks.setOnClickListener{
            val navController =
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
            navController.navigate(R.id.nav_outbound_tasks, Bundle())
        }

    }

}