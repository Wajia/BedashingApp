package com.example.bedashingapp.data.respository


import com.example.bedashingapp.data.api.ApiHelper
import com.example.bedashingapp.data.model.db.*
import com.example.bedashingapp.data.model.remote.*
import com.example.bedashingapp.data.room.dao.*

class MainActivityRepository(
    private val apiHelper: ApiHelper,
    private val itemDao: ItemDao,
    private val uomDao: UOMDao,
    private val uomGroupDao: UomGroupDao,
    private val barcodeDao: BarcodeDao,
    private val postedDocumentDao: PostedDocumentDao
) {

    //-------------------------------------------------------------api calls--------------------------------------------------------------------------

    suspend fun login(mainURL: String, payload: LoginRequest) = apiHelper.login(mainURL, payload)

    suspend fun getUserDetails(
        mainURL: String,
        companyName: String,
        sessionID: String,
        userCode: String
    ) =
        apiHelper.getUserDetails(mainURL, companyName, sessionID, userCode)

    suspend fun checkConnection(
        mainURL: String,
        companyName: String,
        sessionID: String,
        userID: String
    ) =
        apiHelper.checkConnection(mainURL, companyName, sessionID, userID)

    suspend fun getBranches(mainURL: String, companyName: String, sessionID: String) =
        apiHelper.getBranches(mainURL, companyName, sessionID)

    suspend fun getWarehouses(mainURL: String, companyName: String, sessionID: String) =
        apiHelper.getWarehouses(mainURL, companyName, sessionID)

    suspend fun getItemsMaster(
        mainURL: String,
        companyName: String,
        sessionID: String,
        warehouseCode: String,
        from: Int
    ): GetItemsMasterResponse {
        if (from == 0) {
            itemDao.removeAllItems()
        }
        return apiHelper.getItemsMaster(mainURL, companyName, sessionID, warehouseCode, from)
    }

    suspend fun getUoms(mainURL: String, companyName: String, sessionID: String): GetUOMsResponse {
        uomDao.removeAllUoms()
        return apiHelper.getUoms(mainURL, companyName, sessionID)
    }

    suspend fun getUomsByID(id: String): UOMEntity {

        return uomDao.getUomByID(id.toInt())
    }

    suspend fun getUomGroups(
        mainURL: String,
        companyName: String,
        sessionID: String
    ): GetUomGroupsResponse {
        uomGroupDao.removeAllUomGroups()
        return apiHelper.getUomGroups(mainURL, companyName, sessionID)
    }

    suspend fun getBarcodes(
        mainURL: String,
        companyName: String,
        sessionID: String,
        from: Int
    ): GetBarcodesResponse {
        if (from == 0) {
            barcodeDao.removeAllBarcodes()
        }
        return apiHelper.getBarcodes(mainURL, companyName, sessionID, from)
    }

    suspend fun getPOCount(
        mainURL: String,
        companyName: String,
        sessionID: String,
        BPLID: Int,
        VendorCode: String
    ) =
        apiHelper.getPOCount(mainURL, companyName, sessionID, BPLID, VendorCode)

    suspend fun getPO(
        mainURL: String,
        sessionID: String,
        companyName: String,
        branchName: String,
        userHeadOfficeCardCode: String
    ) =
        apiHelper.getPO(mainURL, sessionID, companyName, branchName, userHeadOfficeCardCode)

    suspend fun PurchaseDeliveryNotes(
        mainURL: String,
        sessionID: String,
        companyName: String,
        branchName: String,
        userHeadOfficeCardCode: String,
        payload: PurchaseDeliveryNotesRequest
    ) =
        apiHelper.PurchaseDeliveryNotes(mainURL, sessionID, companyName, payload)


    suspend fun getOpenPO(
        mainURL: String,
        sessionID: String,
        companyName: String,
        docNumber: String
    ) =
        apiHelper.getOPenPO(mainURL, companyName, sessionID, docNumber)

    suspend fun getGRPOCount(
        mainURL: String,
        companyName: String,
        sessionID: String,
        BPLID: Int,
        VendorCode: String
    ) =
        apiHelper.getGRPOCount(mainURL, companyName, sessionID, BPLID, VendorCode)

    suspend fun getDeliveryCount(
        mainURL: String,
        companyName: String,
        sessionID: String,
        BPLID: Int
    ) =
        apiHelper.getDeliveryCount(mainURL, companyName, sessionID, BPLID)

    suspend fun getInventoryCount(
        mainURL: String,
        companyName: String,
        sessionID: String,
        BPLID: Int
    ) =
        apiHelper.getInventoryCount(mainURL, companyName, sessionID, BPLID)

    suspend fun getInventoryCountings(
        mainURL: String,
        companyName: String,
        sessionID: String,
        BPLID: Int
    ) =
        apiHelper.getInventoryCountings(mainURL, companyName, sessionID, BPLID)

    suspend fun getInventoryStatus(
        mainURL: String,
        companyName: String,
        sessionID: String,
        itemCode: String
    ) =
        apiHelper.getInventoryStatus(mainURL, companyName, sessionID, itemCode)

    suspend fun getPurchaseOrders(
        mainURL: String,
        companyName: String,
        sessionID: String,
        itemCode: String
    ) =
        apiHelper.getInventoryStatus(mainURL, companyName, sessionID, itemCode)

    suspend fun getItem(
        mainURL: String,
        companyName: String,
        sessionID: String,
        warehouseCode: String,
        itemCode: String
    ) =
        apiHelper.getItem(mainURL, companyName, sessionID, warehouseCode, itemCode)

    suspend fun getItemPO(
        mainURL: String,
        companyName: String,
        sessionID: String,
        warehouseCode: String,
        itemCode: String
    ) =
        apiHelper.getItemPO(mainURL, companyName, sessionID, warehouseCode, itemCode)

    fun inventoryCountings(
        mainURL: String,
        companyName: String,
        sessionID: String,
        payload: InventoryCountingRequest
    ) =
        apiHelper.inventoryCountings(mainURL, companyName, sessionID, payload)

    fun deliveryNotes(
        mainURL: String,
        companyName: String,
        sessionID: String,
        payload: PurchaseDeliveryNotesRequest
    ) =
        apiHelper.deliveryNotes(mainURL, companyName, sessionID, payload)

    fun postPO(
        mainURL: String,
        companyName: String,
        sessionID: String,
        payload: PostPurchaseOrderRequest
    ) =
        apiHelper.postPO(mainURL, companyName, sessionID, payload)

    //------------------------------------------------------------Room DB calls--------------------------------------------------------------------------


    suspend fun addItemsDB(data: List<ItemEntity>) =
        itemDao.insertItems(data)

    suspend fun addUOMs(data: List<UOMEntity>) =
        uomDao.insertUOMs(data)

    suspend fun addUOMGroups(data: List<UOMGroupEntity>) =
        uomGroupDao.insertUOMGroups(data)

    suspend fun addBarcodes(data: List<BarcodeEntity>) =
        barcodeDao.insertBarcodes(data)

    suspend fun getItemsWithOffset(limit: Int, offset: Int) =
        itemDao.getItemsWithOffset(limit, offset)

    suspend fun getItemsByName(name: String) = itemDao.getItemsByName(name)

    suspend fun getUomsByUomGroupEntry(uomGroupEntry: String): List<UOMEntity> {
        val uomGroups = uomGroupDao.getAlternateUomsByUomGroupEntry(uomGroupEntry)
        val listAlternateUoms = mutableListOf<Int>()
        for (entity in uomGroups) {
            for (uomGroup in entity.UoMGroupDefinitionCollection) {
                listAlternateUoms.add(uomGroup.AlternateUoM)
            }
        }
        return uomDao.getUomsByAlternateUoms(listAlternateUoms)
    }

    suspend fun getItemByBarcode(barcode: String): ItemEntity? {
        var barcodesList = barcodeDao.getBarcodeEntityByBarcode(barcode)
        return if (barcodesList.isEmpty()) {
            null
        } else {
            itemDao.getItemByItemCode(barcodesList.first().ItemNo)
        }
    }

    suspend fun getItemByItemCode(itemCode: String) =
        itemDao.getItemByItemCode(itemCode)

    suspend fun insertDocument(document: PostedDocumentEntity): Long {
        return postedDocumentDao.insertDocument(document)
    }

    suspend fun getAllDocuments(): List<PostedDocumentEntity> {
        return postedDocumentDao.getPostedDocuments()
    }

    suspend fun updateStatusOfDocument(
        id: String,
        status: String,
        response: String,
        newID: String
    ) {
        return postedDocumentDao.updateStatus(status, response, id, newID)
    }


}