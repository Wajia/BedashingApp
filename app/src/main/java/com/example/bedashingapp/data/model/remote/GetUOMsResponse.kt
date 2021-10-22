package com.example.bedashingapp.data.model.remote

data class GetUOMsResponse(
    val value: List<UOM>
)

data class UOM(
    val AbsEntry: Int,
    val Code: String,
    val Name: String
)
