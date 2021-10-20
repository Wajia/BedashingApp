package com.example.bedashingapp.data.model.remote

data class ReceiveGoodsPORequest(
    var ID: String,
    var ProcessingTypeCode: String,
    var Item: List<ItemRequest>

)

data class ItemRequest(
    var TypeCode: String,
    var ProductID: String,
    var ItemQuantity: List<ItemQuantity>,
    var ItemPurchaseOrderReference: ItemPurchaseOrderReference,
    var ItemSellerParty: ItemSellerParty,
    var ItemBuyerParty: ItemBuyerParty
)


data class ItemQuantity(
    var Quantity: String,
    var UnitCode: String,
    var QuantityRoleCode: String,
    var QuantityTypeCode: String,
    var LogisticAreaID: String
)

data class ItemPurchaseOrderReference(
    var ID: String,
    var ItemID: String,
    var ItemTypeCode: String,
    var TypeCode: String,
    var RelationshipRoleCode: String,
    var GoodsCondition: String,
    var StyleMatch: String,
    var PackingCondition: String
)

data class ItemSellerParty(
    var PartyID: String
)

data class ItemBuyerParty(
    var PartyID: String
)


