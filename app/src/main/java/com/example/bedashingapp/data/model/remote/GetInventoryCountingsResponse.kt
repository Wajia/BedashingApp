package com.example.bedashingapp.data.model.remote

data class GetInventoryCountingsResponse(
    val value: List<InventoryCounting>
)


data class InventoryCounting(
    val DocumentEntry: Int,
    val DocumentNumber: Int,
    val CountDate: String,
    val BranchID: Int
)
