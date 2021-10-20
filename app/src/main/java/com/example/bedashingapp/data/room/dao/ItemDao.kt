package com.example.bedashingapp.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bedashingapp.data.model.db.ItemEntity

@Dao
interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ItemEntity>): List<Long>
//
    @Query("SELECT * FROM table_item")
    fun getAllItems() : List<ItemEntity>

    @Query("SELECT * FROM table_item WHERE ObjectID = :itemCode")
    suspend fun getItemByItemCode(itemCode: String): ItemEntity

    @Query("DELETE FROM table_item")
    fun removeAllItems(): Int

    @Query("SELECT * FROM table_item WHERE InternalID IN (:ids)")
    fun getItemsBarcode(ids: List<String>): List<ItemEntity>

    @Query("SELECT * FROM table_item WHERE ObjectID = :id")
    fun getItem(id: String): ItemEntity
//
//    @Query("SELECT * FROM table_item WHERE Category = :category")
//    fun getAllItemsByCategory(category: String): List<ItemEntity>
//
//    @Query("SELECT * FROM table_item LIMIT :limit OFFSET :offset")
//    fun getItemsWithOffset(limit: Int,offset: Int): List<ItemEntity>?
//
//    @Query("SELECT * FROM table_item  WHERE Category = :category LIMIT :limit OFFSET :offset")
//    fun getItemsByCategoryWithOffset(limit: Int,offset: Int,category: String): List<ItemEntity>?
//

//
//    @Query("SELECT COUNT(*) FROM table_item")
//    fun getItemsCount(): Int
//
//    @Query("SELECT * FROM table_item WHERE ItemCode NOT IN (:ids)")
//    fun getAllItemsExcept(ids: List<String>): List<ItemEntity>
//
//    @Query("SELECT * FROM table_item WHERE ItemCode NOT IN (:ids) AND Category = :category")
//    fun getAllItemsByCategoryExcept(ids: List<String>, category: String): List<ItemEntity>
//
//    @Query("SELECT * FROM table_item WHERE (ItemName LIKE '%' || :itemName || '%') OR (ItemCode LIKE '%' || :itemName || '%')")
//    fun getItemsByName(itemName: String): List<ItemEntity>
//
//    @Query("SELECT * FROM table_item WHERE Category = :category AND (ItemName LIKE '%' || :itemName || '%' OR ItemCode LIKE '%' || :itemName || '%')")
//    fun getItemsByNameAndByCategory(itemName: String, category: String): List<ItemEntity>
//
//    @Query("SELECT * FROM table_item WHERE BarCode = :barcode")
//    fun getItemByBarCode(barcode: String): List<ItemEntity>
}