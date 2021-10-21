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