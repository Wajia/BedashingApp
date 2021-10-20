package com.example.bedashingapp.data.room


import androidx.room.TypeConverter
import com.example.bedashingapp.data.model.remote.GlobalTradeItemNumber
import com.example.bedashingapp.data.model.remote.Logistic
import com.example.bedashingapp.data.model.remote.QuantityConversion
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

class DataConverter : Serializable {

    @TypeConverter
    fun fromGlobalTradeItemNumber(lstGlobalTradeItemNumber: List<GlobalTradeItemNumber>?) : String?{
        if(lstGlobalTradeItemNumber == null){
            return (null)
        }
        var gson = Gson();
        var type = object: TypeToken<List<GlobalTradeItemNumber>>(){}.type

        return gson.toJson(lstGlobalTradeItemNumber, type)
    }

    @TypeConverter
    fun toGlobalTradeItemNumber(lstGlobalTradeItemString: String?): List<GlobalTradeItemNumber>?{
        if(lstGlobalTradeItemString == null){
            return (null)
        }
        var gson = Gson()
        var type = object: TypeToken<List<GlobalTradeItemNumber>>(){}.type

        return gson.fromJson(lstGlobalTradeItemString, type)
    }

    @TypeConverter
    fun fromLogistics(lstLogistics: List<Logistic>?) : String?{
        if(lstLogistics == null){
            return (null)
        }
        var gson = Gson();
        var type = object: TypeToken<List<Logistic>>(){}.type

        return gson.toJson(lstLogistics, type)
    }

    @TypeConverter
    fun toLogistic(lstLogisticsString: String?): List<Logistic>?{
        if(lstLogisticsString == null){
            return (null)
        }
        var gson = Gson()
        var type = object: TypeToken<List<Logistic>>(){}.type

        return gson.fromJson(lstLogisticsString, type)
    }

    @TypeConverter
    fun fromQuantityConversion(lstQuantityConversion: List<QuantityConversion>?) : String?{
        if(lstQuantityConversion == null){
            return (null)
        }
        var gson = Gson();
        var type = object: TypeToken<List<QuantityConversion>>(){}.type

        return gson.toJson(lstQuantityConversion, type)
    }

    @TypeConverter
    fun toQuantityConversion(lstQuantityConversionString: String?): List<QuantityConversion>?{
        if(lstQuantityConversionString == null){
            return (null)
        }
        var gson = Gson()
        var type = object: TypeToken<List<QuantityConversion>>(){}.type

        return gson.fromJson(lstQuantityConversionString, type)
    }
}