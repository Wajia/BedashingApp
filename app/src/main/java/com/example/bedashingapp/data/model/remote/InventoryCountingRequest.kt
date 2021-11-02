package com.example.bedashingapp.data.model.remote


data class InventoryCountingRequest(
    var BranchID: String,
    var CountDate: String,
    var Remarks: String,
    var InventoryCountingLines: ArrayList<InventoryCountingLineRemote> = ArrayList()
)


data class InventoryCountingLineRemote(
    var ItemCode: String,
    var Freeze: String,
    var WarehouseCode: String,
    var Counted: String,
    var CountedQuantity: Double,
    var Variance: Double,
    var CostingCode: String,
    var CostingCode2: String,
    var CostingCode3: String,
    var UoMCode: String
)



