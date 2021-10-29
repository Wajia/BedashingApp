package com.example.bedashingapp.data.model.remote

import com.google.gson.annotations.SerializedName

data class GetInventoryStatusResponse(
    val value: List<CustomObject>
)

data class CustomObject(
    @SerializedName("Items/ItemWarehouseInfoCollection")
    val ItemWarehouseInfoCollection: WarehouseInfo
)

data class WarehouseInfo(
    val InStock: Double,
    val WarehouseCode: String
)
