package com.example.bedashingapp.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.bedashingapp.data.model.db.*
import com.example.bedashingapp.data.model.local.Line
import com.example.bedashingapp.data.model.remote.*
import com.example.bedashingapp.data.respository.MainActivityRepository
import com.example.bedashingapp.helper.DateUtilsApp
import com.example.bedashingapp.utils.Constants
import com.example.bedashingapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivityViewModel(private val mainActivityRepository: MainActivityRepository) :
    ViewModel() {
    var poNumber: Int = -1
//    ------------------------------------------------------ API calls -------------------------------------------------------------------------------

    fun login(mainURL: String, companyDB: String, password: String, username: String) =
        liveData(Dispatchers.IO) {
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

    fun getUserDetails(mainURL: String, companyName: String, sessionID: String, userCode: String) =
        liveData(Dispatchers.IO) {

            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = mainActivityRepository.getUserDetails(
                            mainURL,
                            companyName,
                            sessionID,
                            userCode
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    fun checkConnection(mainURL: String, companyName: String, sessionID: String, userID: String) =
        liveData(Dispatchers.IO) {

            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = mainActivityRepository.checkConnection(
                            mainURL,
                            companyName,
                            sessionID,
                            userID
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    fun getBranches(mainURL: String, companyName: String, sessionID: String) =
        liveData(Dispatchers.IO) {

            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = mainActivityRepository.getBranches(
                            mainURL,
                            companyName,
                            sessionID
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    fun getWarehouses(mainURL: String, companyName: String, sessionID: String) =
        liveData(Dispatchers.IO) {

            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = mainActivityRepository.getWarehouses(
                            mainURL,
                            companyName,
                            sessionID
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }


    fun getItemsMasterData(
        mainURL: String,
        companyName: String,
        sessionID: String,
        warehouseCode: String,
        from: Int
    ) = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = mainActivityRepository.getItemsMaster(
                        mainURL,
                        companyName,
                        sessionID,
                        warehouseCode,
                        from
                    )
                )
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getBarcodes(mainURL: String, companyName: String, sessionID: String, from: Int) =
        liveData(Dispatchers.IO) {

            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = mainActivityRepository.getBarcodes(
                            mainURL,
                            companyName,
                            sessionID,
                            from
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    fun getUoms(mainURL: String, companyName: String, sessionID: String) =
        liveData(Dispatchers.IO) {

            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = mainActivityRepository.getUoms(
                            mainURL,
                            companyName,
                            sessionID
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    fun getUomGroups(mainURL: String, companyName: String, sessionID: String) =
        liveData(Dispatchers.IO) {

            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = mainActivityRepository.getUomGroups(
                            mainURL,
                            companyName,
                            sessionID
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    fun getUomByID(id: String) =
        liveData(Dispatchers.IO) {

            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = mainActivityRepository.getUomsByID(id)
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    fun getPOCount(
        mainURL: String,
        companyName: String,
        sessionID: String,
        bplID: String,
        VendorCode: String
    ) =
        liveData(Dispatchers.IO) {

            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = mainActivityRepository.getPOCount(
                            mainURL,
                            companyName,
                            sessionID,
                            bplID.toInt(),
                            VendorCode
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    fun getPO(
        mainURL: String,
        sessionID: String,
        companyName: String,
        branchName: String,
        userHeadOfficeCardCode: String
    ) =
        liveData(Dispatchers.IO) {

            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = mainActivityRepository.getPO(
                            mainURL, sessionID, companyName, branchName, userHeadOfficeCardCode
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    fun getOpenPO(
        mainURL: String,
        sessionID: String,
        companyName: String,
        docNumber: String
    ) =
        liveData(Dispatchers.IO) {

            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = mainActivityRepository.getOpenPO(
                            mainURL, sessionID, companyName, docNumber
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }


    fun getGRPOCount(
        mainURL: String,
        companyName: String,
        sessionID: String,
        bplID: String,
        VendorCode: String
    ) =
        liveData(Dispatchers.IO) {

            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = mainActivityRepository.getGRPOCount(
                            mainURL,
                            companyName,
                            sessionID,
                            bplID.toInt(),
                            VendorCode
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    fun getDeliveryCount(mainURL: String, companyName: String, sessionID: String, bplID: String) =
        liveData(Dispatchers.IO) {

            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = mainActivityRepository.getDeliveryCount(
                            mainURL,
                            companyName,
                            sessionID,
                            bplID.toInt()
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    fun getInventoryCount(mainURL: String, companyName: String, sessionID: String, bplID: String) =
        liveData(Dispatchers.IO) {

            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = mainActivityRepository.getInventoryCount(
                            mainURL,
                            companyName,
                            sessionID,
                            bplID.toInt()
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }


    fun getInventoryCountings(
        mainURL: String,
        companyName: String,
        sessionID: String,
        bplID: String
    ) =
        liveData(Dispatchers.IO) {

            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = mainActivityRepository.getInventoryCountings(
                            mainURL,
                            companyName,
                            sessionID,
                            bplID.toInt()
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }


    fun getInventoryStatus(
        mainURL: String,
        companyName: String,
        sessionID: String,
        itemCode: String
    ) =
        liveData(Dispatchers.IO) {

            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = mainActivityRepository.getInventoryStatus(
                            mainURL,
                            companyName,
                            sessionID,
                            itemCode
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    fun getItem(
        mainURL: String,
        companyName: String,
        sessionID: String,
        warehouseCode: String,
        itemCode: String
    ) =
        liveData(Dispatchers.IO) {

            emit(Resource.loading(data = null))
            try {
                emit(

                    Resource.success(
                        data = mainActivityRepository.getItem(
                            mainURL,
                            companyName,
                            sessionID,
                            warehouseCode,
                            itemCode
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    fun getItemPO(
        mainURL: String,
        companyName: String,
        sessionID: String,
        warehouseCode: String,
        itemCode: String
    ) =
        liveData(Dispatchers.IO) {

            emit(Resource.loading(data = null))
            try {
                emit(

                    Resource.success(
                        data = mainActivityRepository.getItemPO(
                            mainURL,
                            companyName,
                            sessionID,
                            warehouseCode,
                            itemCode
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    fun inventoryCountings(
        mainURL: String,
        companyName: String,
        sessionID: String,
        payload: InventoryCountingRequest
    ) =
        liveData(Dispatchers.IO) {

            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = mainActivityRepository.inventoryCountings(
                            mainURL,
                            companyName,
                            sessionID,
                            payload
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    fun PurchaseDeliveryNotes(
        mainURL: String,
        companyName: String,
        sessionID: String,
        branchName: String,
        userHeadOfficeCardCode: String,
        payload: PurchaseDeliveryNotesRequest
    ) =
        liveData(Dispatchers.IO) {

            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = mainActivityRepository.PurchaseDeliveryNotes(
                            mainURL,
                            sessionID, companyName, branchName, userHeadOfficeCardCode, payload
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    fun goodsReciept(
        mainURL: String,
        companyName: String,
        sessionID: String,
        payload: PurchaseDeliveryNotesRequest
    ) =
        liveData(Dispatchers.IO) {

            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = mainActivityRepository.deliveryNotes(
                            mainURL,
                            companyName, sessionID, payload
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    fun postPO(
        mainURL: String,
        companyName: String,
        sessionID: String,
        payload: PostPurchaseOrderRequest
    ) =
        liveData(Dispatchers.IO) {

            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = mainActivityRepository.postPO(
                            mainURL,
                            companyName,
                            sessionID,
                            payload
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    fun getPO() {

    }

    //-------------------------------------------------- Room Calls--------------------------------------------------------------


    fun saveItemsMaster(data: List<Item>) = liveData(Dispatchers.IO) {
        val itemsList = mutableListOf<ItemEntity>()
        data.forEach {
            itemsList.add(
                ItemEntity(
                    ItemCode = it.Items.ItemCode,
                    ItemName = it.Items.ItemName,
                    BarCode = it.Items.BarCode,
                    UoMGroupEntry = it.Items.UoMGroupEntry,
                    U_Deprtmnt = it.Items.U_Deprtmnt,
                    U_PrdctCat = it.Items.U_PrdctCat,
                    Frozen = it.Items.Frozen,
                    ItemsGroupCode = it.Items.ItemsGroupCode,
                    WarehouseCode = it.ItemWarehouseInfoCollection.WarehouseCode,
                    InStock = it.ItemWarehouseInfoCollection.InStock
                )
            )
        }

        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(data = mainActivityRepository.addItemsDB(itemsList))
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun saveUoms(data: List<UOM>) = liveData(Dispatchers.IO) {
        val uomsList = mutableListOf<UOMEntity>()
        data.forEach {
            uomsList.add(
                UOMEntity(
                    AbsEntry = it.AbsEntry,
                    Code = it.Code,
                    Name = it.Name
                )
            )
        }

        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(data = mainActivityRepository.addUOMs(uomsList))
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun saveUomGroups(data: List<UOMGroup>) = liveData(Dispatchers.IO) {
        val uomGroupsList = mutableListOf<UOMGroupEntity>()
        data.forEach {
            uomGroupsList.add(
                UOMGroupEntity(
                    AbsEntry = it.AbsEntry.toString(),
                    UoMGroupDefinitionCollection = it.UoMGroupDefinitionCollection
                )
            )
        }

        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(data = mainActivityRepository.addUOMGroups(uomGroupsList))
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun saveBarcodes(data: List<Barcodes>) = liveData(Dispatchers.IO) {
        val barcodesList = mutableListOf<BarcodeEntity>()
        data.forEach {
            barcodesList.add(
                BarcodeEntity(
                    AbsEntry = it.BarCodes.AbsEntry,
                    ItemNo = it.BarCodes.ItemNo,
                    UoMEntry = it.BarCodes.UoMEntry,
                    Barcode = it.BarCodes.Barcode
                )
            )
        }

        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(data = mainActivityRepository.addBarcodes(barcodesList))
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    fun getItemsWithOffsetDB(limit: Int, offset: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainActivityRepository.getItemsWithOffset(limit, offset)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getItemsByName(name: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainActivityRepository.getItemsByName(name)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    fun getUomsByUomGroupEntry(uomGroupEntry: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainActivityRepository.getUomsByUomGroupEntry(uomGroupEntry)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getItemByBarcode(barcode: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainActivityRepository.getItemByBarcode(barcode)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getItemByItemCode(itemCode: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainActivityRepository.getItemByItemCode(itemCode)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


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

    private var reloadDocumentsFlagMutableLiveData: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()

    fun setReloadDocumentsFlag(flag: Boolean) {
        reloadDocumentsFlagMutableLiveData.value = flag
    }


    fun updateStatusOfDocument(id: String, status: String, response: String, newID: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = mainActivityRepository.updateStatusOfDocument(
                            id,
                            status,
                            response,
                            newID
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }


    fun saveInventoryCountingDocument(document: InventoryCountingRequest) =
        liveData(Dispatchers.IO) {

            var payload = ""
            payload += "{\n" +
                    "BranchID: ${document.BranchID}\n" +
                    "CountDate: ${document.CountDate}\n" +
                    "Remarks: ${document.Remarks}\n" +
                    "InventoryCountingLines: [\n"

            for (line in document.InventoryCountingLines) {
                payload += "{\n" +
                        "ItemCode: ${line.ItemCode}\n" +
                        "Freeze: ${line.Freeze}\n" +
                        "WarehouseCode: ${line.WarehouseCode}\n" +
                        "Counted: ${line.Counted}\n" +
                        "CountedQuantity: ${line.CountedQuantity}\n" +
                        "Variance: ${line.Variance}\n" +
                        "CostingCode: ${line.CostingCode}\n" +
                        "CostingCode2: ${line.CostingCode2}\n" +
                        "CostingCode3: ${line.CostingCode3}\n" +
                        "UoMCode: ${line.UoMCode}\n" +
                        "}\n"
            }
            payload += "]\n}\n"

            var docDateDB = DateUtilsApp.getUTCFormattedDateTimeString(
                SimpleDateFormat(
                    "dd/MM/yyyy - hh:mm a",
                    Locale.getDefault()
                ), Calendar.getInstance().time
            )

            var document = PostedDocumentEntity(
                ID = Calendar.getInstance().timeInMillis.toString(),
                docType = "Stock Counting",
                dateTime = docDateDB,
                payload = payload,
                status = Constants.PENDING
            )
            lastDocumentSavedID = document.ID

            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = mainActivityRepository.insertDocument(document)
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    fun savePurchaseOrderDocument(document: PostPurchaseOrderRequest) = liveData(Dispatchers.IO) {

        var payload = ""
        payload += "{\n" +
                "BranchID: ${document.BranchID}\n" +
                "DocDate: ${document.DocDate}\n" +
                "DocDate: ${document.DocDate}\n" +
                "Comments: ${document.Comments}\n" +
                "InventoryCountingLines: [\n"

        for (line in document.DocumentLines) {
            payload += "{\n" +
                    "ItemCode: ${line.ItemCode}\n" +
                    "WarehouseCode: ${line.WarehouseCode}\n" +
                    "Quantity: ${line.Quantity}\n" +
                    "CountedQuantity: ${line.Quantity}\n" +
                    "CostingCode: ${line.CostingCode}\n" +
                    "CostingCode2: ${line.CostingCode2}\n" +
                    "CostingCode3: ${line.CostingCode3}\n" +
                    "UoMEntry: ${line.UoMEntry}\n" +
                    "}\n"
        }
        payload += "]\n}\n"

        var docDateDB = DateUtilsApp.getUTCFormattedDateTimeString(
            SimpleDateFormat(
                "dd/MM/yyyy - hh:mm a",
                Locale.getDefault()
            ), Calendar.getInstance().time
        )

        var document = PostedDocumentEntity(
            ID = Calendar.getInstance().timeInMillis.toString(),
            docType = "Purchase Order",
            dateTime = docDateDB,
            payload = payload,
            status = Constants.PENDING
        )
        lastDocumentSavedID = document.ID

        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = mainActivityRepository.insertDocument(document)
                )
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun saveGoodRecieptDocument(document: PurchaseDeliveryNotesRequest) = liveData(Dispatchers.IO) {

        var payload = ""
        payload += "{\n" +

                "DocDate: ${document.DocDate}\n" +
                "DocDueDate: ${document.DocDueDate}\n" +
                "GoodsLines: [\n"

        for (line in document.DocumentLines) {
            payload += "{\n" +
                    "ItemCode: ${line.ItemCode}\n" +
                    "WarehouseCode: ${line.WarehouseCode}\n" +
                    "Quantity: ${line.Quantity}\n" +
                    "CountedQuantity: ${line.Quantity}\n" +
                    "CostingCode: ${line.CostingCode}\n" +
                    "CostingCode2: ${line.CostingCode2}\n" +
                    "CostingCode3: ${line.CostingCode3}\n" +
                    "UoMEntry: ${line.UoMEntry}\n" +
                    "}\n"
        }
        payload += "]\n}\n"

        var docDateDB = DateUtilsApp.getUTCFormattedDateTimeString(
            SimpleDateFormat(
                "dd/MM/yyyy - hh:mm a",
                Locale.getDefault()
            ), Calendar.getInstance().time
        )

        var document = PostedDocumentEntity(
            ID = Calendar.getInstance().timeInMillis.toString(),
            docType = "Goods Receipt",
            dateTime = docDateDB,
            payload = payload,
            status = Constants.PENDING
        )
        lastDocumentSavedID = document.ID

        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = mainActivityRepository.insertDocument(document)
                )
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun savePCDocument(document: PurchaseDeliveryNotesRequest) = liveData(Dispatchers.IO) {

        var payload = ""
        payload += "{\n" +

                "DocDate: ${document.DocDate}\n" +
                "DocDueDate: ${document.DocDueDate}\n" +
                "InventoryCountingLines: [\n"

        for (line in document.DocumentLines) {
            payload += "{\n" +
                    "ItemCode: ${line.ItemCode}\n" +
                    "WarehouseCode: ${line.WarehouseCode}\n" +
                    "Quantity: ${line.Quantity}\n" +
                    "CountedQuantity: ${line.Quantity}\n" +
                    "CostingCode: ${line.CostingCode}\n" +
                    "CostingCode2: ${line.CostingCode2}\n" +
                    "CostingCode3: ${line.CostingCode3}\n" +
                    "UoMEntry: ${line.UoMEntry}\n" +
                    "}\n"
        }
        payload += "]\n}\n"

        var docDateDB = DateUtilsApp.getUTCFormattedDateTimeString(
            SimpleDateFormat(
                "dd/MM/yyyy - hh:mm a",
                Locale.getDefault()
            ), Calendar.getInstance().time
        )

        var document = PostedDocumentEntity(
            ID = Calendar.getInstance().timeInMillis.toString(),
            docType = "Personal Checkout",
            dateTime = docDateDB,
            payload = payload,
            status = Constants.PENDING
        )
        lastDocumentSavedID = document.ID

        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = mainActivityRepository.insertDocument(document)
                )
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    var lastDocumentSavedID: String = ""


    //--------------------------------------------------------------------------------------------------------------------------------------------


    private var selectedItems: ArrayList<Line> = ArrayList()
    fun setSelectedItems(mSelectedItems: ArrayList<Line>) {
        selectedItems.clear()
        selectedItems.addAll(mSelectedItems)
        for (item in selectedItems) {
            item.originalRemainingQuantity = item.RemainingOpenQuantity
        }
    }

    fun getSelectedItems() = selectedItems
    fun clearSelectedItems() {
        selectedItems.clear()
    }

    fun addInventoryCountingLine(
        item: ItemEntity,
        warehouseCode: String,
        countedQuantity: Double,
        variance: Double,
        costingCode: String,
        costingCode2: String,
        costingCode3: String,
        uomCode: String,
        inStock: Double
    ) {
        //first check whether item with same itemCode & uomCode exists or not
        val index =
            selectedItems.indexOfFirst { it.ItemCode == item.ItemCode && it.UoMCode == uomCode }
        if (index != -1) {
            selectedItems[index].CountedQuantity += countedQuantity
            selectedItems[index].CostingCode3 = costingCode3
            selectedItems[index].Variance = selectedItems[index].CountedQuantity - inStock
        } else {
            selectedItems.add(
                Line(
                    ItemCode = item.ItemCode,
                    ItemDescription = item.ItemName,
                    Freeze = "tNO",
                    WarehouseCode = warehouseCode,
                    Counted = "tYES",
                    BarCode = item.BarCode,
                    CountedQuantity = countedQuantity,
                    Variance = variance,
                    CostingCode = costingCode,
                    CostingCode2 = costingCode2,
                    CostingCode3 = costingCode3,
                    UoMCode = uomCode

                )
            )
        }
    }

    fun updateInventoryCountingLine(
        item: ItemEntity,
        countedQuantity: Double,
        variance: Double,
        costingCode3: String,
        uomCode: String,
        inStock: Double,
        itemIndex: Int
    ): Boolean {
        //first check whether another item with same itemCode & uomCode exists or not
        //if it exists then return false
        val index = selectedItems.filterIndexed { index, _ -> index != itemIndex }
            .indexOfFirst { it.ItemCode == item.ItemCode && it.UoMCode == uomCode }
        return if (index != -1) {
            false
        } else {
            selectedItems[itemIndex].CountedQuantity = countedQuantity
            selectedItems[itemIndex].Variance = selectedItems[itemIndex].CountedQuantity - inStock
            selectedItems[itemIndex].CostingCode3 = costingCode3
            selectedItems[itemIndex].UoMCode = uomCode
            true
        }
    }


    fun addPurchaseOrderLine(
        item: ItemEntity,
        warehouseCode: String,
        unitPrice: Double,
        quantity: Double,
        countedQuantity: Double,
        costingCode: String,
        costingCode2: String,
        costingCode3: String,
        uomCode: String,
        inStock: Double,
        uoMEntry: String
    ) {
        //first check whether item with same itemCode & uomCode exists or not
        val index =
            selectedItems.indexOfFirst { it.ItemCode == item.ItemCode && it.UoMCode == uomCode }
        if (index != -1) {
            selectedItems[index].Quantity =
                (selectedItems[index].Quantity.toDouble() + quantity).toString()
            selectedItems[index].CostingCode3 = costingCode3
        } else {
            selectedItems.add(
                Line(

                    ItemCode = item.ItemCode,
                    ItemDescription = item.ItemName,
                    Freeze = "tNO",
                    WarehouseCode = warehouseCode,
                    Counted = "tYES",
                    BarCode = item.BarCode,
                    CountedQuantity = countedQuantity,
                    Quantity = quantity.toString(),
                    CostingCode = costingCode,
                    CostingCode2 = costingCode2,
                    CostingCode3 = costingCode3,
                    UoMCode = uomCode,
                    UoMEntry = uoMEntry,
                    UnitPrice = unitPrice.toString()
                )
            )
        }
    }

    fun updatePurchaseOrderLine(
        item: ItemEntity,
        quantity: Double,
        countedQuantity: Double,
        costingCode3: String,
        uomCode: String,
        inStock: Double,
        itemIndex: Int
    ): Boolean {
        //first check whether another item with same itemCode & uomCode exists or not
        //if it exists then return false
        val index = selectedItems.filterIndexed { index, _ -> index != itemIndex }
            .indexOfFirst { it.ItemCode == item.ItemCode && it.UoMCode == uomCode }
        return if (index != -1) {
            false
        } else {
            selectedItems[itemIndex].CountedQuantity = countedQuantity
            selectedItems[itemIndex].Quantity = (quantity - inStock).toString()
            selectedItems[itemIndex].CostingCode3 = costingCode3
            selectedItems[itemIndex].UoMCode = uomCode
            true
        }
    }


    fun removeSelectedItem(item: Line) {
        selectedItems.removeAt(selectedItems.indexOf(item))
    }

}