package com.example.bedashingapp.data.model.remote

data class FromWareHouseSiteIdResponse(
    val d: D7
)

data class D7(
    val results: List<FromWareHouse>
)

data class FromWareHouse(
    val ObjectID: String,
    val SiteID: String
)
