package com.example.bedashingapp.data.model.local

data class Line(

    var Freeze: String,
    var Counted: String,
    var CountedQuantity: Double = 0.0,
    var Variance: Double = 0.0,
    var CostingCode: String = "",
    var CostingCode2: String = "",
    var CostingCode3: String = "",
    var UnitPrice: String = "",
    var UoMEntry: String = "",
    var LineNum: String? = "",
    var ItemCode: String? = "",
    var ItemDescription: String = "",
    var Quantity: String = "0.0",
    var UoMCode: String? = "",
    var BarCode: String? = "",
    var WarehouseCode: String? = "",
    var comments: String? = "",
    var RemainingOpenQuantity: Double = 0.0,
    var BaseLine: String = "0",
    var BaseType: String? = "",
    var BaseEntry: String? = "",
    var LineStatus: String = "",
    var DocEntry: String = "",
    var originalRemainingQuantity: Double = 0.0


    )
