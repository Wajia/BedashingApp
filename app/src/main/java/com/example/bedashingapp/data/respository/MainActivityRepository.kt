package com.example.bedashingapp.data.respository


import com.example.bedashingapp.data.api.ApiHelper
import com.example.bedashingapp.data.model.db.PostedDocumentEntity
import com.example.bedashingapp.data.model.remote.LoginRequest
import com.example.bedashingapp.data.room.dao.ItemDao
import com.example.bedashingapp.data.room.dao.PostedDocumentDao

class MainActivityRepository(
    private val apiHelper: ApiHelper,
    private val itemDao: ItemDao,
    private val postedDocumentDao: PostedDocumentDao
) {

    //-------------------------------------------------------------api calls--------------------------------------------------------------------------

    suspend fun login(mainURL: String, payload: LoginRequest) = apiHelper.login(mainURL, payload)

    suspend fun getUserDetails(mainURL: String, companyName: String, sessionID: String, userCode: String) =
        apiHelper.getUserDetails(mainURL, companyName, sessionID,userCode)

    suspend fun checkConnection(mainURL: String, companyName: String, sessionID: String, userID: String) =
        apiHelper.checkConnection(mainURL, companyName, sessionID, userID)

    suspend fun getBranches(mainURL: String, companyName: String, sessionID: String) =
        apiHelper.getBranches(mainURL, companyName, sessionID)

    suspend fun getWarehouses(mainURL: String, companyName: String, sessionID: String) =
        apiHelper.getWarehouses(mainURL, companyName, sessionID)


    //------------------------------------------------------------Room DB calls--------------------------------------------------------------------------



    suspend fun insertDocument(document: PostedDocumentEntity): Long {
        return postedDocumentDao.insertDocument(document)
    }

    suspend fun getAllDocuments(): List<PostedDocumentEntity>{
        return postedDocumentDao.getPostedDocuments()
    }

    suspend fun updateStatusOfDocument(id: String, status: String, response: String){
        return postedDocumentDao.updateStatus(status, response, id)
    }


}