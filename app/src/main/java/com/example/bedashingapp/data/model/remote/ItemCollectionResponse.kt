package com.example.bedashingapp.data.model.remote

data class ItemCollectionResponse(
    val d: D1
)


data class D1(
    val results: List<ItemPO>
)

data class ItemPO(
    val ObjectID: String, //primary key
    val ParentObjectID: String, //Parent PO Primary Key
    val ID: String, //Linenum
    val ProductID: String, //id to show to user
    val DeliveryStatusCode: String,
    var Quantity: String,
    var TotalDeliveredQuantity: String,
    val QuantityUnitCode: String,
    val QuantityUnitCodeText: String,
    //now fields for front end
    var isSelected: Boolean = false,
    var QuantityReceived: Double = 0.0,
    var UnitCode: String,
    var BinCode: String,
    var ConditionGoods: String,
    var StyleMatch: String,
    var PackingCondition: String
)