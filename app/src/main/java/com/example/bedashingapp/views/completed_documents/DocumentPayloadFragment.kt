package com.example.bedashingapp.views.completed_documents

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import com.example.bedashingapp.BaseFragment
import com.example.bedashingapp.R
import kotlinx.android.synthetic.main.fragment_payload_document.*

class DocumentPayloadFragment: BaseFragment() {
    override fun getLayout(): Int {
        return R.layout.fragment_payload_document
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_payload.movementMethod = ScrollingMovementMethod()
        tv_response.movementMethod = ScrollingMovementMethod()

        tv_payload.text = arguments?.getString("payload")
        tv_response.text = arguments?.getString("response")
    }

    override fun apiCaller(purpose: String) {
        TODO("Not yet implemented")
    }
}