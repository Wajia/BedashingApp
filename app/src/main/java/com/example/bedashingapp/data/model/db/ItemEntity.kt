package com.example.bedashingapp.data.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bedashingapp.data.model.remote.QuantityConversion

@Entity(tableName = "table_item")
data class ItemEntity(
        @PrimaryKey
        val ObjectID : String,
        val InternalID : String,
        val Description : String?,
        val BaseMeasureUnitCode: String,
        val BaseMeasureUnitCodeText: String,
        val PackagingBarcode_KUT: String,
        val Barcode_KUT: String,
        val QuantityConversion: List<QuantityConversion>

)



