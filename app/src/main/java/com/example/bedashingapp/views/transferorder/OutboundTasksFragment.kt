package com.example.bedashingapp.views.transferorder

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
import com.example.bedashingapp.data.model.local.Task
import com.example.bedashingapp.data.model.remote.TaskItem
import com.example.bedashingapp.data.model.remote.TasksResponse
import com.example.bedashingapp.helper.SessionManager
import com.example.bedashingapp.helper.ViewModelFactory
import com.example.bedashingapp.utils.OnItemClickListener
import com.example.bedashingapp.utils.Status
import com.example.bedashingapp.viewmodel.MainActivityViewModel
import com.example.bedashingapp.views.transferorder.adapter.TaskAdapter
import kotlinx.android.synthetic.main.fragment_outbound_tasks.*

/**
 * A simple [Fragment] subclass.
 * Use the [OutboundTasksFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OutboundTasksFragment : BaseFragment() {
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private var sessionManager: SessionManager? = null

    private val tasksList: ArrayList<TasksResponse> = ArrayList()

    override fun getLayout(): Int {
        return R.layout.fragment_outbound_tasks
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(requireContext())
        setUpViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivityViewModel.clearSelectedTask()
        mainActivityViewModel.clearTransferItems()
        mainActivityViewModel.selectedFromWarehouse = ""
        getData()


        edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString().toLowerCase())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun getData(){
        if(isConnectedToNetwork()) {
            mainActivityViewModel.getTasks(2, sessionManager!!.getEmployeeID())
                .observe(viewLifecycleOwner, Observer {
                    it?.let {resource ->
                        when(resource.status){
                            Status.SUCCESS->{
                                hideProgressBar()
                                tasksList.clear()
                                tasksList.addAll(resource.data as ArrayList)
                                setRecyclerView()
                            }
                            Status.LOADING->{
                                showProgressBar("", "Fetching tasks")
                            }
                            Status.ERROR->{
                                hideProgressBar()
                            }
                        }
                    }
                })
        }else{
            hideProgressBar()
            showToastLong(resources.getString(R.string.network_not_connected_msg))
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

    private var adapter: TaskAdapter? = null
    private fun setRecyclerView(){
        adapter = TaskAdapter(tasksList, requireActivity(), onItemClickListener)
        rv_outbound_tasks.adapter = adapter
        rv_outbound_tasks.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        rv_outbound_tasks.layoutManager = layoutManager
    }

    private var onItemClickListener: OnItemClickListener<TasksResponse> = object : OnItemClickListener<TasksResponse>(){
        override fun onClicked(view: View?, position: Int, type: String?, data: TasksResponse?) {

            //set selected task
            mainActivityViewModel.setSelectedTask(Task(TaskID = data!!.TaskID, SiteID = data.SiteID))
            mainActivityViewModel.setTransferItems(data.lstTask as ArrayList<TaskItem>)

            //get from WareHouse
            if(isConnectedToNetwork()) {
                mainActivityViewModel.getFromWareHouseSiteID(data.lstTask[0].SourceLogisticAreaID).observe(viewLifecycleOwner, Observer {
                    it?.let{resource ->
                        when(resource.status){
                            Status.SUCCESS->{
                                mainActivityViewModel.selectedFromWarehouse = resource.data!!.d.results[0].SiteID
                                Navigation.findNavController(activity!!, R.id.nav_host_fragment).navigate(R.id.nav_outbound_item_scan, Bundle())
                            }
                            Status.LOADING->{
                                rlProgress.visibility = View.VISIBLE
                            }
                            Status.ERROR->{
                                showToastLong(resource.message!!)
                            }
                        }
                    }
                })
            }else{
                showToastLong(resources.getString(R.string.network_not_connected_msg))
            }


        }

    }

    private fun filter(text: String){
        if(text.isEmpty()){
            adapter?.updateList(tasksList)
        }else{
            var temp: ArrayList<TasksResponse> = ArrayList()
            for(task in tasksList){
                if(task.TaskID.toLowerCase().contains(text)){
                    temp.add(task)
                }
            }
            adapter?.updateList(temp)
        }
    }

}