package com.example.bedashingapp.data.model.remote

import com.google.gson.annotations.SerializedName

data class GetItemsMasterResponse(
    val value: List<Item>
)

data class Item(
    val Items: ItemDetails,
    @SerializedName("Items/ItemWarehouseInfoCollection")
    val ItemWarehouseInfoCollection: ItemWarehouseInfoCollection
)

data class ItemDetails(
    val ItemCode: String,
    val ItemName: String,
    val BarCode: String,
    val UoMGroupEntry: String,
    val U_Deprtmnt: String,
    val U_PrdctCat: String,
    val Frozen: String,
    val ItemsGroupCode: Int
)

data class ItemWarehouseInfoCollection(
    val WarehouseCode: String,
    val InStock: Double
)

