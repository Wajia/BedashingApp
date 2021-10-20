package com.example.bedashingapp.data.model.remote

data class TasksResponse(
    val TaskID: String,
    val SiteID: String,
    val lstTask: List<TaskItem>
)

data class TaskItem(
    val ProductID: String,
    val SourceLogisticAreaID: String,
    val ProductDescription: String,
    var PlanQuantity: Double,
    var OpenQuantity: Double,
    val TotalConfirmedQuantity: Double,
    val UOM: String,
    var LineID: String,
    //fields added for data manipulation
    var Bins: ArrayList<SelectedBin> = ArrayList(),
    var isSelected: Boolean = false
)

data class SelectedBin(
    val ID: String,
    var PickedQuantity: Double,
    val Quantity: Double,
    var isSelected: Boolean = false
)
