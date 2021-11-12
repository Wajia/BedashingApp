package com.example.bedashingapp.data.model.remote


data class PostPurchaseOrderRequest(
    var BranchID: String,
    var DocDate: String,
    val RequriedDate: String,
    val DocDueDate: String,
    val BPLName: String,
    val BPL_IDAssignedToInvoice: String,
    val Comments: String,
    val DocumentLines: ArrayList<DocumentLine> = ArrayList(),
    val CardCode: String,
    val U_DocNo: String
)




