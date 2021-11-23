package com.example.bedashingapp.data.model.remote

data class AddPurchaseOderResponse(
    val DocEntry: String="",
    val error: Error?
    /*val BPL_IDAssignedToInvoice: String,
    val BranchID: String,
    val CardCode: String,
    val Comments: String,
    val DocDate: String,
    val DocDueDate: String,
    val DocumentLines: List<DocumentLine>,
    val RequriedDate: String,
    val U_DocNo: String*/
)