package com.example.bedashingapp.views.completed_documents.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bedashingapp.R
import com.example.bedashingapp.data.model.db.PostedDocumentEntity
import com.example.bedashingapp.utils.Constants
import com.example.bedashingapp.utils.OnItemClickListener

class CompletedDocumentAdapter(
    private var documentsList: List<PostedDocumentEntity>,
    private var context: Context,
    private var mOnItemClickListener: OnItemClickListener<PostedDocumentEntity>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_completed_documents_single_item, parent, false)
        return ViewHolder(view!!)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int = documentsList.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        private var docTypeTextView = mView.findViewById<TextView>(R.id.tv_docType)
        private var docIDTextView = mView.findViewById<TextView>(R.id.tv_docID)
        private var docDateTextView = mView.findViewById<TextView>(R.id.tv_docDateDB)
        private var statusImageView = mView.findViewById<ImageView>(R.id.iv_status)
        private var progressBar = mView.findViewById<ProgressBar>(R.id.progress_bar)

        fun bind(result: Int) {
            val data = documentsList[result]

            docTypeTextView.text = data.docType
            if(data.ID.contains("$")){
                docIDTextView.text = data.ID.split("$").first()
            }else {
                docIDTextView.text = data.ID
            }
            docDateTextView.text = data.dateTime

            when(data.status){
                Constants.PENDING->{
                    progressBar.visibility = View.VISIBLE
                    statusImageView.visibility = View.GONE
                }
                Constants.SYNCED->{
                    progressBar.visibility = View.GONE
                    statusImageView.setImageResource(R.drawable.ic_baseline_done_24)
                    statusImageView.visibility = View.VISIBLE
                }
                Constants.FAILED->{
                    progressBar.visibility = View.GONE
                    statusImageView.setImageResource(R.drawable.ic_baseline_sync_problem_24)
                    statusImageView.visibility = View.VISIBLE
                }
            }


            itemView.setOnClickListener {
                if(data.status == Constants.FAILED){
                    mOnItemClickListener.onClicked(itemView, position, "", data)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun updateList(list: List<PostedDocumentEntity>) {
        documentsList = list
        notifyDataSetChanged()
    }
}