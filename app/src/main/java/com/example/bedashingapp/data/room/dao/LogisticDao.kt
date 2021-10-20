package com.example.bedashingapp.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bedashingapp.data.model.db.LogisticEntity

@Dao
interface LogisticDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogistics(items: List<LogisticEntity>): List<Long>

    @Query("SELECT * FROM table_logistic WHERE SiteID=:siteID")
    fun getAllLogistics(siteID: String) : List<LogisticEntity>

    @Query("DELETE FROM table_logistic")
    fun removeAllLogistics(): Int


}