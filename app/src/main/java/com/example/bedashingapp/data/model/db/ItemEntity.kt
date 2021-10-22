package com.example.bedashingapp.data.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_item")
data class ItemEntity(
        @PrimaryKey
        val ItemCode: String,
        val ItemName: String,
        val BarCode: String,
        val UoMGroupEntry: String,
        val U_Deprtmnt: String,
        val U_PrdctCat: String,
        val Frozen: String,
        val ItemsGroupCode: Int,
        val WarehouseCode: String,
        val InStock: Double
)



