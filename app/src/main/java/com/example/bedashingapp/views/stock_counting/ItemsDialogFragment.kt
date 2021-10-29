package com.example.bedashingapp.views.stock_counting

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bedashingapp.R
import com.example.bedashingapp.data.api.ApiHelper
import com.example.bedashingapp.data.api.RetrofitBuilder
import com.example.bedashingapp.data.model.db.ItemEntity
import com.example.bedashingapp.helper.SessionManager
import com.example.bedashingapp.helper.ViewModelFactory
import com.example.bedashingapp.utils.Status
import com.example.bedashingapp.viewmodel.MainActivityViewModel
import com.example.bedashingapp.views.stock_counting.adapter.ItemAdapter
import kotlinx.android.synthetic.main.items_dialog.*

class ItemsDialogFragment : DialogFragment(), ItemAdapter.OnItemClickListener {

    var mOnItemClickListener: OnItemClickListener? = null

    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var sessionManager: SessionManager

    private var itemsList: ArrayList<ItemEntity> = ArrayList()

    var page = 0
    var limit = 10
    var isLoading = false
    var onScrollLoad = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sessionManager = SessionManager(requireContext())
        return inflater.inflate(R.layout.items_dialog, container, false)
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpViewModel()
        initRecyclerView()
        getData()

        btn_close.setOnClickListener { dismiss() }

        et_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                filter(et_search.text.toString().trim())
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

    }

    private fun filter(text: String) {
        if (text.length <= 1) {
            itemsList.clear()
            onScrollLoad = true
            page = 0
            limit = 10
            getData()
        } else {
            //get items by name
            onScrollLoad = false

            mainActivityViewModel.getItemsByName(text).observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            itemsList.clear()
                            itemsList.addAll(resource.data as ArrayList)
                            setRecyclerView()
                            rlProgress.visibility = View.GONE
                        }
                        Status.LOADING -> {
                            rlProgress.visibility = View.VISIBLE
                        }
                        Status.ERROR -> {
                            Toast.makeText(
                                requireContext(),
                                "Cannot fetch items",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            })

        }
    }

    private fun getData() {
        mainActivityViewModel.getItemsWithOffsetDB(limit, page)
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.let { items ->
                                if (items.size == limit) {
                                    isLoading = true
                                    page += 10
                                } else {
                                    isLoading = false
                                }
                            }
                            itemsList.addAll(resource.data as ArrayList)
                            setRecyclerView()
                            rlProgress.visibility = View.GONE
                        }
                        Status.LOADING -> {

                            rlProgress.visibility = View.VISIBLE
                        }
                        Status.ERROR -> {
                            Toast.makeText(
                                requireContext(),
                                "Cannot fetch items",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            })

    }

    private var adapter: ItemAdapter? = null
    private fun initRecyclerView() {
        itemsList = ArrayList()
        adapter = ItemAdapter(itemsList, requireActivity(), this)
        rv_items.adapter = adapter
        rv_items.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        rv_items.layoutManager = layoutManager

        rv_items.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (onScrollLoad) {
                    val totalItem = layoutManager.itemCount
                    val lastVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition()
                    if (lastVisibleItem == totalItem - 1 && isLoading) {
                        getData()
                    }
                }
            }

        })
    }

    private fun setRecyclerView() {
        adapter?.notifyDataSetChanged()

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

    interface OnItemClickListener {
        public fun onItemClick(item: ItemEntity)
    }

    override fun onItemClick(item: ItemEntity) {
        mOnItemClickListener?.onItemClick(item)
        dismiss()
    }
}