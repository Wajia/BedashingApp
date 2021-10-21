package com.example.bedashingapp.data.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.bedashingapp.data.model.db.ItemEntity
import com.example.bedashingapp.data.model.db.PostedDocumentEntity

import com.example.bedashingapp.utils.Constants
import com.example.bedashingapp.data.room.DataConverter
import com.example.bedashingapp.data.room.dao.ItemDao
import com.example.bedashingapp.data.room.dao.PostedDocumentDao

@Database(entities = [ItemEntity::class, PostedDocumentEntity::class], version = 1, exportSchema = false)
@TypeConverters(DataConverter::class)
abstract class TVGDatabase: RoomDatabase() {

    abstract fun itemDao(): ItemDao


    abstract fun postedDocumentDao(): PostedDocumentDao

    companion object {
        @Volatile
        private var INSTANCE: TVGDatabase? = null

        fun getDatabase(context: Context): TVGDatabase {
            val tempInstance = INSTANCE
            if(tempInstance!= null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext, TVGDatabase::class.java, Constants.DATABASE_NAME).build()
                INSTANCE = instance
                return instance
            }

        }
    }
}