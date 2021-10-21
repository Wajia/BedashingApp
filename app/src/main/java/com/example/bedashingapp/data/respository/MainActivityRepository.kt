package com.example.bedashingapp.data.respository


import com.example.bedashingapp.data.api.ApiHelper
import com.example.bedashingapp.data.model.db.PostedDocumentEntity
import com.example.bedashingapp.data.room.dao.ItemDao
import com.example.bedashingapp.data.room.dao.PostedDocumentDao

class MainActivityRepository(
    private val apiHelper: ApiHelper,
    private val itemDao: ItemDao,
    private val postedDocumentDao: PostedDocumentDao
) {

    //-------------------------------------------------------------api calls--------------------------------------------------------------------------



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