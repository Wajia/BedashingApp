package com.example.bedashingapp.data.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_posted_document")
data class PostedDocumentEntity(
    @PrimaryKey(autoGenerate = true)
    var num: Int = 0,
    var ID: String,
    var docType: String,
    var dateTime: String,
    var payload: String,
    var status: String = "PENDING",
    var response: String = ""
)

