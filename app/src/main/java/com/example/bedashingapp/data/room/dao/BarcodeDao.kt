package com.example.bedashingapp.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bedashingapp.data.model.db.BarcodeEntity

@Dao
interface BarcodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBarcodes(data: List<BarcodeEntity>): List<Long>

    @Query("SELECT * FROM table_barcode")
    fun getAllBarcodes() : List<BarcodeEntity>

    @Query("SELECT * FROM table_barcode WHERE AbsEntry = :absEntry")
    suspend fun getBarcodeByID(absEntry: Int): BarcodeEntity

    @Query("SELECT * FROM table_barcode WHERE Barcode = :barcode")
    suspend fun getBarcodeEntityByBarcode(barcode: String): List<BarcodeEntity>

    @Query("DELETE FROM table_barcode")
    fun removeAllBarcodes(): Int

}