package com.example.bedashingapp.helper

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bedashingapp.data.api.ApiHelper
import com.example.bedashingapp.data.respository.MainActivityRepository
import com.example.bedashingapp.data.room.database.TVGDatabase
import com.example.bedashingapp.viewmodel.MainActivityViewModel


class ViewModelFactory(
    private val apiHelper: ApiHelper,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return MainActivityViewModel(
                MainActivityRepository(
                    apiHelper, TVGDatabase.getDatabase(application).itemDao(),
                    TVGDatabase.getDatabase(application).logisticDao(),
                    TVGDatabase.getDatabase(application).postedDocumentDao()
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}