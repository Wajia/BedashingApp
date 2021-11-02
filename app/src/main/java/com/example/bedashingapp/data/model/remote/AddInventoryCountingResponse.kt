package com.example.bedashingapp.data.model.remote

data class AddInventoryCountingResponse(
    val DocumentEntry: Int?,
    val DocumentNumber: Int?,
    val Series: Int?,
    val CountDate: String?,
    val CountTime: String?,
    val error: Error?
)
