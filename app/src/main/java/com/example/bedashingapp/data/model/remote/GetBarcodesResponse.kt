package com.example.bedashingapp.data.model.remote

data class GetBarcodesResponse(
    val value: List<Barcodes>
)

data class Barcodes(
    val BarCodes: Barcode
)

data class Barcode(
    val AbsEntry: Int,
    val ItemNo: String,
    val UoMEntry: Int,
    val Barcode: String
)
