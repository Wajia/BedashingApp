package com.example.bedashingapp.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bedashingapp.data.model.db.UOMEntity

@Dao
interface UOMDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUOMs(data: List<UOMEntity>): List<Long>

    @Query("SELECT * FROM table_uom")
    fun getAllUoms() : List<UOMEntity>

    @Query("SELECT * FROM table_uom WHERE AbsEntry = :absEntry")
    suspend fun getUomByID(absEntry: Int): UOMEntity

    @Query("SELECT * FROM table_uom WHERE AbsEntry IN (:alternateUoms)")
    suspend fun getUomsByAlternateUoms(alternateUoms: List<Int>): List<UOMEntity>

    @Query("DELETE FROM table_uom")
    fun removeAllUoms(): Int
}