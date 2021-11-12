package com.example.bedashingapp.data.model.remote

data class AddPurchaseOrderResponse(
    val DocEntry: Int,
    val DocNum: Int,
    val Series: Int,
    val CountDate: String,
    val CountTime: String,
    val error: Error?,
    val DocDueDate: String,
    val DocDate: String,
    val DocumentLines: ArrayList<DocumentLine>

)
