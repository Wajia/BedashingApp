package com.example.bedashingapp.data.model.remote

import com.example.bedashingapp.data.model.local.Line


data class ProfessionalCheckoutRequest(

    var DocDate: String,
    val DocDueDate: String = "",
    val RequriedDate: String = "",
    val BPL_IDAssignedToInvoice: Int,
    val DocumentLines: ArrayList<PcLine> = ArrayList(),
    val CardCode: String,
    val U_DocNo: String,
    val id: String = "1637571158629",
    val DocNum: String = "",
    val DocEntry: String = "",
    val Comments: String = "",
    )
data class PcLine
    (
        var CostingCode: String = "",
        var CostingCode2: String = "",
        var CostingCode3: String = "",
        var UnitPrice: Double = 0.0,
        var UoMEntry: String = "",
        var ItemCode: String? = "",
        var Quantity: Double = 0.0,
        var WarehouseCode: String? = "",
        var comments: String? = "",
        var LineStatus: String = "",


)





