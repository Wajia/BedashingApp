package com.example.bedashingapp.data.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_logistic")
data class LogisticEntity(
    @PrimaryKey
    val ObjectID: String,
    val ID: String,
    val SiteID: String,
    var isSelected: Boolean = false
)
