package com.example.bedashingapp.data.model.local

data class Line(
    var ItemCode: String,
    var ItemDescription: String,
    var Freeze: String,
    var WarehouseCode: String,
    var Counted: String,
    var BarCode: String,
    var CountedQuantity: Double = 0.0,
    var Variance: Double = 0.0,
    var CostingCode: String = "",
    var CostingCode2: String = "",
    var CostingCode3: String = "",
    var UoMCode: String
)
