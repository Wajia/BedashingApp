package com.example.bedashingapp.views.goodsreceiving

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.bedashingapp.BaseFragment
import com.example.bedashingapp.R
import kotlinx.android.synthetic.main.fragment_goods_receiving.*


/**
 * A simple [Fragment] subclass.
 * Use the [GoodsReceivingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GoodsReceivingFragment : BaseFragment() {


    override fun getLayout(): Int {
        return R.layout.fragment_goods_receiving
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        btn_po.setOnClickListener{
            val navController =
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
            navController.navigate(R.id.nav_po_list, Bundle())
        }
    }


}