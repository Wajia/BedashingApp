package com.example.bedashingapp.data.model.remote

import com.example.bedashingapp.data.model.local.Line


data class PurchaseDeliveryNotesRequest(

    var DocDate: String,
    val DocDueDate: String,
    val BPL_IDAssignedToInvoice: String,
    val DocumentLines: ArrayList<Line> = ArrayList(),
    val CardCode: String,
    val U_DocNo: String,
    val id: String = ""
)




