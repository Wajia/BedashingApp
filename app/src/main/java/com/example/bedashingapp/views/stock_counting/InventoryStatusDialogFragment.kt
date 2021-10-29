package com.example.bedashingapp.views.stock_counting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bedashingapp.R
import com.example.bedashingapp.data.model.db.ItemEntity
import com.example.bedashingapp.data.model.remote.CustomObject
import com.example.bedashingapp.helper.SessionManager
import com.example.bedashingapp.viewmodel.MainActivityViewModel
import com.example.bedashingapp.views.stock_counting.adapter.InventoryStatusAdapter
import kotlinx.android.synthetic.main.dialog_inventory_status.*

class InventoryStatusDialogFragment(val data: List<CustomObject>) : DialogFragment() {


    var mOnItemClickListener: OnItemClickListener? = null

    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sessionManager = SessionManager(requireContext())
        return inflater.inflate(R.layout.dialog_inventory_status, container, false)
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerView()

        btn_close.setOnClickListener {
            dismiss()
        }
    }

    private var adapter: InventoryStatusAdapter? = null
    private fun setRecyclerView() {
        adapter = InventoryStatusAdapter(data)
        rv_inventory_status.adapter = adapter
        rv_inventory_status.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        rv_inventory_status.layoutManager = layoutManager
    }

    interface OnItemClickListener {
        public fun onItemClick(item: ItemEntity)
    }
}