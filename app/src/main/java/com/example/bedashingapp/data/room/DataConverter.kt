package com.example.bedashingapp.data.room


import androidx.room.TypeConverter
import com.example.bedashingapp.data.model.db.ItemEntity
import com.example.bedashingapp.data.model.remote.UoMGroupDefinitionCollection
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

class DataConverter : Serializable {



//    @TypeConverter
//    fun fromQuantityConversion(lstQuantityConversion: List<ItemEntity>?) : String?{
//        if(lstQuantityConversion == null){
//            return (null)
//        }
//        var gson = Gson();
//        var type = object: TypeToken<List<ItemEntity>>(){}.type
//
//        return gson.toJson(lstQuantityConversion, type)
//    }
//
//    @TypeConverter
//    fun toQuantityConversion(lstQuantityConversionString: String?): List<ItemEntity>?{
//        if(lstQuantityConversionString == null){
//            return (null)
//        }
//        var gson = Gson()
//        var type = object: TypeToken<List<ItemEntity>>(){}.type
//
//        return gson.fromJson(lstQuantityConversionString, type)
//    }

    @TypeConverter
    fun fromUomGroupDefinitionCollection(lstUoMGroupDefinitionCollection:  List<UoMGroupDefinitionCollection>?) : String?{
        if(lstUoMGroupDefinitionCollection == null){
            return (null)
        }
        var gson = Gson();
        var type = object: TypeToken<List<UoMGroupDefinitionCollection>>(){}.type

        return gson.toJson(lstUoMGroupDefinitionCollection, type)
    }

    @TypeConverter
    fun toUomGroupDefinitionCollection(lstUoMGroupDefinitionCollectionString: String?): List<UoMGroupDefinitionCollection>?{
        if(lstUoMGroupDefinitionCollectionString == null){
            return (null)
        }
        var gson = Gson()
        var type = object: TypeToken<List<UoMGroupDefinitionCollection>>(){}.type

        return gson.fromJson(lstUoMGroupDefinitionCollectionString, type)
    }


}