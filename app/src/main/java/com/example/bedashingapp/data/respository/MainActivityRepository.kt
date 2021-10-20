package com.example.bedashingapp.data.respository


import com.example.bedashingapp.data.api.ApiHelper
import com.example.bedashingapp.data.model.db.ItemEntity
import com.example.bedashingapp.data.model.db.LogisticEntity
import com.example.bedashingapp.data.model.db.PostedDocumentEntity
import com.example.bedashingapp.data.model.remote.*
import com.example.bedashingapp.data.room.dao.ItemDao
import com.example.bedashingapp.data.room.dao.LogisticDao
import com.example.bedashingapp.data.room.dao.PostedDocumentDao

class MainActivityRepository(
    private val apiHelper: ApiHelper,
    private val itemDao: ItemDao,
    private val logisticDao: LogisticDao,
    private val postedDocumentDao: PostedDocumentDao
) {

    //-------------------------------------------------------------api calls--------------------------------------------------------------------------
    suspend fun getPurchaseOrders() = apiHelper.getPurchaseOrders()

    suspend fun getItemCollectionForPOs() = apiHelper.getItemCollectionForPOs()

    suspend fun getAllItems() = apiHelper.getAllItems()

    suspend fun getLogisticsAreaCollection() = apiHelper.getLogisticsAreaCollection()

    suspend fun login() = apiHelper.login()

    fun getCRFTokenInboundDelivery() = apiHelper.getCRFTokenInboundDelivery()

//    suspend fun receiveGoodsPO(crfToken: String, cookie: String, payload: ReceiveGoodsPORequest) =
//        apiHelper.receiveGoodsPO(crfToken, cookie, payload)

    suspend fun receiveGoodsPO(payload: ReceiveGoodsPORequest) =
        apiHelper.receiveGoodsPO(payload)

    suspend fun postGoodsReceipt(crfToken: String, cookie: String, id: String) =
        apiHelper.postGoodsReceipt(crfToken, cookie, id)

    suspend fun getTasks(tasksRequest: TasksRequest) = apiHelper.getTasks(tasksRequest)

    suspend fun getFromWareHouseSiteID(id: String) = apiHelper.getFromWareHouseSiteID(id)

    suspend fun getOutboundBins(itemID: String, siteID: String) = apiHelper.getOutboundBins(itemID, siteID)

    suspend fun createOutboundDelivery(payload: CreateOutboundDeliveryRequest) = apiHelper.createOutboundDelivery(payload)


    //------------------------------------------------------------Room DB calls--------------------------------------------------------------------------
    fun removeItemsDB(): Int {
        return itemDao.removeAllItems()
    }

    suspend fun addItemsDB(items: List<Item>): List<Long> {
        var itemEntityList = mutableListOf<ItemEntity>()
        items.forEach {

            itemEntityList.add(
                ItemEntity(
                    ObjectID = it.ObjectID,
                    InternalID = it.InternalID,
                    Description = it.Description,
                    BaseMeasureUnitCode = it.BaseMeasureUnitCode,
                    BaseMeasureUnitCodeText = it.BaseMeasureUnitCodeText,
                    PackagingBarcode_KUT = it.PackagingBarcode_KUT,
                    Barcode_KUT = it.Barcode_KUT,
                    QuantityConversion = it.QuantityConversion
                )
            )
        }

        return itemDao.insertItems(itemEntityList)
    }

    suspend fun getItemsBarcode(ids: List<String>): List<ItemEntity> {
        return itemDao.getItemsBarcode(ids)
    }

    fun removeLogisticsDB(): Int {
        return logisticDao.removeAllLogistics()
    }

    suspend fun addLogisticsDB(logistics: List<Logistic1>): List<Long> {
        var logisticEntityList = mutableListOf<LogisticEntity>()
        logistics.forEach {

            logisticEntityList.add(
                LogisticEntity(
                    ObjectID = it.ObjectID,
                    ID = it.ID,
                    SiteID = it.SiteID
                )
            )
        }

        return logisticDao.insertLogistics(logisticEntityList)
    }

    suspend fun getAllLogistics(siteID: String): List<LogisticEntity>{
        return logisticDao.getAllLogistics(siteID)
    }


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