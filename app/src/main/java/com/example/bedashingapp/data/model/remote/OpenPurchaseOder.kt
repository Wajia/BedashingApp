package com.example.bedashingapp.data.model.remote

import com.example.bedashingapp.data.model.local.Line

data class OpenPurchaseOder(
    var LineNum: String? = "",
    var ItemCode: String? = "",
    var ItemDescription: String = "",
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
    var LineStatus: String = "",
    var DocDate: String = "",
    var DocDueDate: String = "",

    var DocumentLines: ArrayList<Line> = ArrayList(),
    val error: Error?
)



