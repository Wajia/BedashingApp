package com.example.bedashingapp.data.model.remote

data class LogisticsAreaCollectionResponse(
    val d: D6
)

data class D6(
    var results: List<Logistic1>
)

data class Logistic1(
    val ObjectID: String,
    val ID: String,
    val SiteID: String,
)
