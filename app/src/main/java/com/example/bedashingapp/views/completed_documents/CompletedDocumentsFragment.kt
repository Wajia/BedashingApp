package com.example.bedashingapp.views.completed_documents

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bedashingapp.BaseFragment
import com.example.bedashingapp.R
import com.example.bedashingapp.data.api.ApiHelper
import com.example.bedashingapp.data.api.RetrofitBuilder
import com.example.bedashingapp.data.model.db.PostedDocumentEntity
import com.example.bedashingapp.helper.ViewModelFactory
import com.example.bedashingapp.utils.OnItemClickListener
import com.example.bedashingapp.utils.Status
import com.example.bedashingapp.viewmodel.MainActivityViewModel
import com.example.bedashingapp.views.completed_documents.adapter.CompletedDocumentAdapter
import kotlinx.android.synthetic.main.fragment_documents_completed.*

class CompletedDocumentsFragment : BaseFragment() {

    private lateinit var mainActivityViewModel: MainActivityViewModel

    private val documentsList: ArrayList<PostedDocumentEntity> = ArrayList()

    override fun getLayout(): Int {
        return R.layout.fragment_documents_completed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        edSearchVC.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString().toLowerCase())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        getData()
        setupObserver()
    }

    private fun getData() {
        mainActivityViewModel.getAllCompletedDocuments().observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        documentsList.clear()
                        documentsList.addAll(resource.data as ArrayList)
                        setRecyclerView()
                        rlProgress.visibility = View.GONE
                    }
                    Status.LOADING -> {
                        rlProgress.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        rlProgress.visibility = View.GONE
                        showToastLong(resource.message!!)
                    }
                }
            }
        })
    }

    private fun setupObserver(){
        mainActivityViewModel.reloadDocumentsFlagLiveData.observe(viewLifecycleOwner, Observer {
            if(it){
                getData()
                mainActivityViewModel.setReloadDocumentsFlag(false)
            }
        })
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

    private var adapter: CompletedDocumentAdapter? = null
    private fun setRecyclerView() {
        adapter = CompletedDocumentAdapter(documentsList, requireContext(), onItemClickListener)
        rv_completed_documents.adapter = adapter
        rv_completed_documents.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        rv_completed_documents.layoutManager = layoutManager
    }

    private var onItemClickListener: OnItemClickListener<PostedDocumentEntity> =
        object : OnItemClickListener<PostedDocumentEntity>() {
            override fun onClicked(
                view: View?,
                position: Int,
                type: String?,
                data: PostedDocumentEntity?
            ) {
                var bundle = Bundle()
                bundle.putString("payload", data?.payload)
                bundle.putString("response", data?.response)
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(
                    R.id.nav_doc_payload, bundle
                )
            }

        }

    private fun filter(text: String) {
        if (text.isEmpty()) {
            adapter?.updateList(documentsList)
        } else {
            var temp: ArrayList<PostedDocumentEntity> = ArrayList()
            for (document in documentsList) {
                if (document.ID.toLowerCase().contains(text) || document.docType.toLowerCase()
                        .contains(text)
                ) {
                    temp.add(document)
                }
            }
            adapter?.updateList(temp)
        }
    }
}