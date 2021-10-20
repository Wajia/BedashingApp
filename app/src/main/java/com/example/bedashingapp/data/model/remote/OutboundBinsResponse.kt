package com.example.bedashingapp.data.model.remote

data class OutboundBinsResponse(
    val d: D8
)

data class D8(
    val results: List<OutBoundBin>
)

data class OutBoundBin(
    var CLOG_AREA_UUID: String,
    val KCON_HAND_STOCK: String,
    val KCUN_RESTRICTED_STOCK: String

)
