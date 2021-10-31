package com.example.bedashingapp.data.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_uom")
data class UOMEntity(
    @PrimaryKey
    val AbsEntry: Int,
    val Code: String,
    val Name: String



) {
    override fun toString(): String {
        return Name
    }
}
