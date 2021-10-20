package com.example.bedashingapp.views.goodsreceiving.purchaseorder

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bedashingapp.BaseFragment
import com.example.bedashingapp.R
import com.example.bedashingapp.data.api.ApiHelper
import com.example.bedashingapp.data.api.RetrofitBuilder
import com.example.bedashingapp.data.model.db.LogisticEntity
import com.example.bedashingapp.helper.ViewModelFactory
import com.example.bedashingapp.utils.OnItemClickListener
import com.example.bedashingapp.utils.Status
import com.example.bedashingapp.viewmodel.MainActivityViewModel
import com.example.bedashingapp.views.goodsreceiving.purchaseorder.adapters.POBinAdapter
import kotlinx.android.synthetic.main.fragment_po_bin_selection.*


/**
 * A simple [Fragment] subclass.
 * Use the [POBinSelectionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class POBinSelectionFragment : BaseFragment() {
    private lateinit var mainActivityViewModel: MainActivityViewModel

    private val binsList: ArrayList<LogisticEntity> = ArrayList()

    override fun getLayout(): Int {
        return R.layout.fragment_po_bin_selection
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //getting data
//        binsList.addAll(mainActivityViewModel.getSelectedItem()?.Logistics as ArrayList)
        mainActivityViewModel.getAllLogisticsAreasDB("DC1100").observe(viewLifecycleOwner, Observer {
            it?.let{ resource ->
                when(resource.status){
                    Status.SUCCESS->{
                        binsList.addAll(resource.data!!)
                        hideProgressBar()
                        setRecyclerView()
                    }
                    Status.LOADING->{
                        showProgressBar("","")
                    }
                    Status.ERROR->{
                        hideProgressBar()
                        showToastLong(resource.message!!)
                    }
                }
            }
        })

        edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString().toLowerCase())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        btn_next_bin.setOnClickListener{
            //bin must be selected
            if(binsList.find { it.isSelected } != null) {
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.nav_po_item_scan, Bundle())
            }else{
                showToastShort("Please select bin location")
            }
        }

    }

    private fun setUpViewModel() {
        mainActivityViewModel = ViewModelProviders.of(
            requireActivity(),
            ViewModelFactory(
                ApiHelper(RetrofitBuilder.getApiService("dynamic ip here")),
                requireActivity().application
            )
        ).get(MainActivityViewModel::class.java)
    }

    private var adapter: POBinAdapter? = null
    private fun setRecyclerView(){
        adapter = POBinAdapter(binsList, context = requireContext(), mOnItemClickListener = onItemClickListener)
        rv_bins_po.adapter = adapter
        rv_bins_po.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        rv_bins_po.layoutManager = layoutManager
    }

    private var onItemClickListener: OnItemClickListener<LogisticEntity> = object : OnItemClickListener<LogisticEntity>(){
        override fun onClicked(view: View?, position: Int, type: String?, data: LogisticEntity?) {
            binsList.find{it.isSelected}?.isSelected = false
            binsList[binsList.indexOf(data)].isSelected = true
            adapter?.notifyDataSetChanged()
            mainActivityViewModel.enterBinCode(data!!.ID)
        }

    }

    private fun filter(text: String) {
        if (text.isEmpty()) {
            adapter?.updateList(binsList)
        } else {
            var temp: ArrayList<LogisticEntity> = ArrayList()
            for (bin in binsList) {
                if (bin.ID.toLowerCase().contains(text)) {
                    temp.add(bin)
                }
            }
            adapter?.updateList(temp)
        }
    }

}