package com.example.bedashingapp.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bedashingapp.data.model.db.ItemEntity
import com.example.bedashingapp.data.model.db.UOMGroupEntity

@Dao
interface UomGroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUOMGroups(data: List<UOMGroupEntity>): List<Long>

    @Query("SELECT * FROM table_uom_group")
    fun getAllUomGroups() : List<UOMGroupEntity>

    @Query("SELECT * FROM table_uom_group WHERE AbsEntry = :absEntry")
    suspend fun getUomGroupByID(absEntry: String): UOMGroupEntity

    @Query("SELECT * FROM table_uom_group WHERE AbsEntry LIKE :uomGroupEntry")
    suspend fun getAlternateUomsByUomGroupEntry(uomGroupEntry: String): List<UOMGroupEntity>

    @Query("DELETE FROM table_uom_group")
    fun removeAllUomGroups(): Int

}