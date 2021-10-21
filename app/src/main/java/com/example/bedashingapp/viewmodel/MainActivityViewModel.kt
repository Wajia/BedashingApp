package com.example.bedashingapp.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.bedashingapp.data.respository.MainActivityRepository
import com.example.bedashingapp.utils.Resource
import kotlinx.coroutines.Dispatchers


class MainActivityViewModel(private val mainActivityRepository: MainActivityRepository) :
    ViewModel() {














    //--------------------------------------------------------------------------------------------------------------------------------------------






    fun getAllCompletedDocuments() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(data = mainActivityRepository.getAllDocuments())
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }



    val reloadDocumentsFlagLiveData: LiveData<Boolean>
        get() = reloadDocumentsFlagMutableLiveData

    private var reloadDocumentsFlagMutableLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    fun setReloadDocumentsFlag(flag: Boolean){
        reloadDocumentsFlagMutableLiveData.value = flag
    }


    fun updateStatusOfDocument(id: String, status: String, response: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(data = mainActivityRepository.updateStatusOfDocument(id, status, response))
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }



}