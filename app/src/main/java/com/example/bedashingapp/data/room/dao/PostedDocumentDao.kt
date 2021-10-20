package com.example.bedashingapp.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bedashingapp.data.model.db.PostedDocumentEntity

@Dao
interface PostedDocumentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) 
    suspend fun insertDocument(document: PostedDocumentEntity): Long

    @Query("SELECT * FROM table_posted_document ORDER BY dateTime DESC")
    fun getPostedDocuments(): List<PostedDocumentEntity>

    @Query("UPDATE table_posted_document SET status =:status, response =:response WHERE ID=:ID")
    fun updateStatus(status: String, response: String, ID: String)

    @Query("SELECT * FROM table_posted_document WHERE ID=:ID")
    fun getDocument(ID: String): PostedDocumentEntity
}