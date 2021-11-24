package com.example.bedashingapp.data.model.remote


import com.example.bedashingapp.data.model.local.Line
import java.util.*

data class PurchaseOder(
    val BPLName: String = "",
    val DocDate: String = "",
    val DocDueDate: String = "",
    val DocEntry: Int = 0,
    val DocNum: Int = 0,
    val RequriedDate: String = "",
    var DocumentLines: ArrayList<Line> = ArrayList()
)

