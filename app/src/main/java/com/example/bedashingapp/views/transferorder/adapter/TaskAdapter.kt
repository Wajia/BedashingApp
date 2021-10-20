package com.example.bedashingapp.views.transferorder.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bedashingapp.R
import com.example.bedashingapp.data.model.remote.TasksResponse
import com.example.bedashingapp.utils.OnItemClickListener

class TaskAdapter(
    private var tasksList: List<TasksResponse>,
    private var context: Context,
    private var mOnItemClickListener: OnItemClickListener<TasksResponse>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_tasks_single_item, parent, false)
        return ViewHolder(view!!)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int = tasksList.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        private var taskIDTextView = mView.findViewById<TextView>(R.id.tv_task_id)
        private var toWareHouseTextView = mView.findViewById<TextView>(R.id.tv_to_wh)
//        private var fromWareHouseTextView = mView.findViewById<TextView>(R.id.tv_from_wh)

        fun bind(result: Int) {
            val data = tasksList[result]

            taskIDTextView.text = data.TaskID
            toWareHouseTextView.text = data.SiteID

            itemView.setOnClickListener {
                mOnItemClickListener.onClicked(itemView, position, "", data)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun updateList(list: List<TasksResponse>) {
        tasksList = list
        notifyDataSetChanged()
    }
}