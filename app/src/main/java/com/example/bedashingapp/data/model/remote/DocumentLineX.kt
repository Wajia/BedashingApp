package com.example.bedashingapp.data.model.remote

data class DocumentLineX(

    var Counted: String,
    var CountedQuantity: Double = 0.0,

    var LineStatus: String = "",
    var DocEntry: String = "",

    var LineNum: String? = "",
    var BaseEntry: String? = "",
    var ItemCode: String? = "",
    var ItemDescription: String? = "",
    var Quantity: Double = 0.0,
    var UoMCode: String? = "",
    var BarCode: String? = "",
    var WarehouseCode: String? = "",
    var comments: String? = "",
    var RemainingOpenQuantity: Double = 0.0,
    var CostingCode: String? = "",
    var CostingCode2: String? = "",


    var CostingCode3: String? = "",
    var BaseLine: String? = "",
    var BaseType: String? = "",

    var UoMEntry: String? = "",
    var UnitPrice: Double = 0.0,
)