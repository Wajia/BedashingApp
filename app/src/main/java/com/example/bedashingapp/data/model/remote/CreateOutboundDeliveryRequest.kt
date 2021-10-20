package com.example.bedashingapp.data.model.remote

data class CreateOutboundDeliveryRequest(
    var TaskID: String,
    var FromSiteID: String,
    var TaskDetails: List<TaskDetails>,
)

data class TaskDetails(
    var ItemCode: String,
    var LogisticDetails: List<LogisticDetails>,
)

data class LogisticDetails(
    var LogisticID: String,
    var Quantity: String,
    var UOM: String
)
