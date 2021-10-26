package com.example.bedashingapp.data.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.bedashingapp.data.model.db.*

import com.example.bedashingapp.utils.Constants
import com.example.bedashingapp.data.room.DataConverter
import com.example.bedashingapp.data.room.dao.*

@Database(
    entities = [
        ItemEntity::class,
        PostedDocumentEntity::class,
        UOMEntity::class,
        UOMGroupEntity::class,
        BarcodeEntity::class], version = 1, exportSchema = false
)
@TypeConverters(DataConverter::class)
abstract class TVGDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao
    abstract fun uomDao(): UOMDao
    abstract fun uomGroupDao(): UomGroupDao
    abstract fun barcodeDao(): BarcodeDao

    abstract fun postedDocumentDao(): PostedDocumentDao

    companion object {
        @Volatile
        private var INSTANCE: TVGDatabase? = null

        fun getDatabase(context: Context): TVGDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TVGDatabase::class.java,
                    Constants.DATABASE_NAME
                ).build()
                INSTANCE = instance
                return instance
            }

        }
    }
}