package com.example.bedashingapp.data.model.remote

data class GetWarehousesResponse(
    val value: List<Warehouse>
)

data class Warehouse(
    val WarehouseCode: String?,
    val WarehouseName: String,
    val BusinessPlaceID: Int


) {
    override fun toString(): String {
        return WarehouseName
    }
}
