package com.example.bedashingapp.data.model.remote


data class MaterialCollectionResponse(
    val d: D2
)

data class D2(
    val results: List<Item>
)

data class Item(
    val ObjectID: String,
    val InternalID: String,
    val Description: String?,
    val BaseMeasureUnitCode: String,
    val BaseMeasureUnitCodeText: String,
    val PackagingBarcode_KUT: String,
    val Barcode_KUT: String,
    val QuantityConversion: List<QuantityConversion>
//    val GlobalTradeItemNumber: List<GlobalTradeItemNumber>
//    val Logistics: List<Logistic>
)

data class GlobalTradeItemNumber(
    val ID: String,
    val QuantityTypeCode: String,
    val QuantityTypeCodeText: String
)

data class QuantityConversion(
    val ObjectID: String,
    val QuantityUnitCode: String,
    val QuantityUnitCodeText: String,
    val Quantity: String
)

data class Logistic(
    val SiteID: String,
    val LifeCycleStatusCode: String,
    val SiteName: String,
    var isSelected: Boolean = false
)
