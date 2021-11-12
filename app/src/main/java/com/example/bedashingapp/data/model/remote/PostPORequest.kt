package com.example.bedashingapp.data.model.remote

data class PostPORequest(
    val BPL_IDAssignedToInvoice: Int,
    val CardCode: String,
    val Comments: String,
    val DocDate: String,
    val DocDueDate: String,
    val DocEntry: String,
    val DocNum: String,
    val DocumentLines: List<DocumentLine>,
    val RequriedDate: String,
    val U_DocNo: String,
    val id: String
)