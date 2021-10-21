package com.example.bedashingapp.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.bedashingapp.data.model.remote.LoginRequest
import com.example.bedashingapp.data.respository.MainActivityRepository
import com.example.bedashingapp.utils.Resource
import kotlinx.coroutines.Dispatchers


class MainActivityViewModel(private val mainActivityRepository: MainActivityRepository) :
    ViewModel() {


    fun login(mainURL: String, companyDB: String, password: String, username: String) = liveData(Dispatchers.IO) {
        val loginRequest = LoginRequest(
            CompanyDB = companyDB,
            Password = password,
            UserName = username
        )
        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(data = mainActivityRepository.login(mainURL, loginRequest))
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getUserDetails(mainURL: String, companyName: String, sessionID: String, userCode: String) = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(data = mainActivityRepository.getUserDetails(mainURL, companyName, sessionID, userCode))
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun checkConnection(mainURL: String, companyName: String, sessionID: String, userID: String) = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(data = mainActivityRepository.checkConnection(mainURL, companyName, sessionID, userID))
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getBranches(mainURL: String, companyName: String, sessionID: String) = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(data = mainActivityRepository.getBranches(mainURL, companyName, sessionID))
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getWarehouses(mainURL: String, companyName: String, sessionID: String) = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(data = mainActivityRepository.getWarehouses(mainURL, companyName, sessionID))
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }











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