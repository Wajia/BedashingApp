package com.example.bedashingapp.data.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_barcode")
data class BarcodeEntity(
    @PrimaryKey
    val AbsEntry: Int,
    val ItemNo: String,
    val UoMEntry: Int,
    val Barcode: String
)
