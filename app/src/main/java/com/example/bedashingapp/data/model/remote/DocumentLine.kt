package com.example.bedashingapp.data.model.remote

data class DocumentLine(
    val CostingCode: String,
    val CostingCode2: String,
    val CostingCode3: String,
    val ItemCode: String,
    val LineStatus: String,
    val Quantity: Double,
    val UnitPrice: Double,
    val UoMEntry: String,
    val WarehouseCode: String,
    val comments: String=""
)