package com.example.bedashingapp.data.model.remote

data class PurchaseOrderCollectionResponse(
    val d: D
)

data class D(
    val results: List<PurchaseOrder>
)


data class PurchaseOrder(
    val ID: String, //PO ID
    val ObjectID: String, //Primary key
    val DeliveryStatusCode: String,
    val CreationDateTime: String,
    val Supplier: Supplier,
    val BuyerParty: BuyerParty
)

data class Supplier(
    val PartyID: String,
    val SupplierName: List<SupplierName>
)

data class SupplierName(
    val ObjectID: String,
    val FormattedName: String
)

data class BuyerParty(
    val PartyID: String
)