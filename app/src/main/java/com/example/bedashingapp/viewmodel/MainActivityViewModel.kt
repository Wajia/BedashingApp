package com.example.bedashingapp.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.bedashingapp.data.model.db.ItemEntity
import com.example.bedashingapp.data.model.db.PostedDocumentEntity
import com.example.bedashingapp.data.model.local.Task
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


    fun getPurchaseOrders() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainActivityRepository.getPurchaseOrders()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getItemCollectionForPOs() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainActivityRepository.getItemCollectionForPOs()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getAllItems() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainActivityRepository.getAllItems()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getLogisticsAreaCollection() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainActivityRepository.getLogisticsAreaCollection()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun login() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainActivityRepository.login()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    fun getCRFTokenInboundDelivery() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainActivityRepository.getCRFTokenInboundDelivery()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getItemsBarcode(ids: List<String>) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainActivityRepository.getItemsBarcode(ids)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun removeItemsDB() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainActivityRepository.removeItemsDB()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun addItems(items: List<Item>) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainActivityRepository.addItemsDB(items)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun removeLogisticsDB() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainActivityRepository.removeLogisticsDB()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun addLogisticsDB(logistics: List<Logistic1>) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainActivityRepository.addLogisticsDB(logistics)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getAllLogisticsAreasDB(siteID: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainActivityRepository.getAllLogistics(siteID)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    private var selectedPO: PurchaseOrder? = null
    fun getSelectedPO() = selectedPO
    fun clearSelectedPO() {
        selectedPO = null
    }

    fun setSelectedPO(po: PurchaseOrder) {
        selectedPO = po
    }


    private var poItems: ArrayList<ItemPO> = ArrayList()
    fun getPOItems() = poItems
    fun setPOItems(itemList: ArrayList<ItemPO>) {
        poItems.addAll(itemList)
    }

    fun clearPOItems() = poItems.clear()


    private var selectedItem: ItemEntity? = null
    fun getSelectedItem() = selectedItem
    fun setSelectedItem(item: ItemEntity) {
//        var barCodeList = ArrayList<GlobalTradeItemNumber>()
//        barCodeList.addAll(item.GlobalTradeItemNumber as ArrayList)

        var quantityConversionList = ArrayList<QuantityConversion>()
        quantityConversionList.addAll(item.QuantityConversion as ArrayList)

        selectedItem = ItemEntity(
            ObjectID = item.ObjectID,
            InternalID = item.InternalID,
            Description = item.Description,
            BaseMeasureUnitCode = item.BaseMeasureUnitCode,
            BaseMeasureUnitCodeText = item.BaseMeasureUnitCodeText,
            PackagingBarcode_KUT = item.PackagingBarcode_KUT,
            Barcode_KUT = item.Barcode_KUT,
            QuantityConversion = item.QuantityConversion
        )
    }

    fun clearSelectedItem() {
        selectedItem = null
    }

    var scannedBarcode: String = ""
    var selectedLineNum: String = ""

    fun enterQuantity(
        quantity: Double,
        unitCode: String,
        conditionGoods: String,
        styleMatch: String,
        packingCondition: String
    ) {
        val index = poItems.indexOfFirst { it.ID == selectedLineNum }
        if (index != -1) {
            poItems[index].UnitCode = unitCode
            poItems[index].QuantityReceived = quantity
            poItems[index].ConditionGoods = conditionGoods
            poItems[index].StyleMatch = styleMatch
            poItems[index].PackingCondition = packingCondition
        }
    }

    fun enterQuantityForUpdate(
        quantity: Double,
        conditionGoods: String,
        styleMatch: String,
        packingCondition: String
    ) {
        val index = poItems.indexOfFirst { it.ID == selectedLineNum }
        if (index != -1) {
            poItems[index].QuantityReceived = quantity
            poItems[index].ConditionGoods = conditionGoods
            poItems[index].StyleMatch = styleMatch
            poItems[index].PackingCondition = packingCondition
        }
    }

    fun enterBinCode(binCode: String) {
        val index = poItems.indexOfFirst { it.ID == selectedLineNum }
        if (index != -1) {
            poItems[index].BinCode = binCode
        }
    }


    //for goods receiving po

    fun saveReceiveGoodsPODocument(receiveGoodsPORequest: ReceiveGoodsPORequest) =
        liveData(Dispatchers.IO) {
            var payload = ""
            payload += "{\n" +
                    "ID: ${receiveGoodsPORequest.ID}\n" +
                    "ProcessingTypeCode: ${receiveGoodsPORequest.ProcessingTypeCode}\n" +
                    "Item: [\n"
            for (itemPO in receiveGoodsPORequest.Item) {
                payload += "{\n" +
                        "TypeCode: ${itemPO.TypeCode}\n" +
                        "ProductID: ${itemPO.ProductID}\n" +
                        "ItemQuantity: [\n{\n" +
                        "Quantity: ${itemPO.ItemQuantity[0].Quantity}\n" +
                        "UnitCode: ${itemPO.ItemQuantity[0].UnitCode}\n" +
                        "QuantityRoleCode: ${itemPO.ItemQuantity[0].QuantityRoleCode}\n" +
                        "QuantityTypeCode: ${itemPO.ItemQuantity[0].QuantityTypeCode}\n" +
                        "LogisticAreaID: ${itemPO.ItemQuantity[0].LogisticAreaID}\n" +
                        "}\n]\n" +
                        "ItemPurchaseOrderReference: {\n" +
                        "ID: ${itemPO.ItemPurchaseOrderReference.ID}\n" +
                        "ItemID: ${itemPO.ItemPurchaseOrderReference.ItemID}\n" +
                        "ItemTypeCode: ${itemPO.ItemPurchaseOrderReference.ItemTypeCode}\n" +
                        "TypeCode: ${itemPO.ItemPurchaseOrderReference.TypeCode}\n" +
                        "RelationshipRoleCode: ${itemPO.ItemPurchaseOrderReference.RelationshipRoleCode}\n" +
                        "GoodsCondition: ${itemPO.ItemPurchaseOrderReference.GoodsCondition}\n" +
                        "StyleMatch: ${itemPO.ItemPurchaseOrderReference.StyleMatch}\n" +
                        "PackingCondition: ${itemPO.ItemPurchaseOrderReference.PackingCondition}\n" +
                        "}\n" +
                        "ItemSellerParty: {\n" +
                        "PartyID: ${itemPO.ItemSellerParty.PartyID}\n" +
                        "}\n" +
                        "ItemBuyerParty: {\n" +
                        "PartyID: ${itemPO.ItemBuyerParty.PartyID}\n" +
                        "}\n" +
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
                ID = receiveGoodsPORequest.ID,
                docType = "Goods Receiving - PO",
                dateTime = docDateDB,
                payload = payload,
                status = Constants.PENDING
            )

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


    fun receiveGoodsPO(receiveGoodsPORequest: ReceiveGoodsPORequest) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = mainActivityRepository.receiveGoodsPO(
                        receiveGoodsPORequest
                    )
                )
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------



    fun postGoodsReceipt(crfToken: String, cookie: String, id: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = mainActivityRepository.postGoodsReceipt(
                        crfToken,
                        cookie,
                        id
                    )
                )
            )
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





    //------------------------------------------------- For Transfer orders------------------------------------------------------------------

    fun getTasks(processTypeCode: Int, empID: String) = liveData(Dispatchers.IO) {
        val tasksRequest = TasksRequest(empID, processTypeCode)
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainActivityRepository.getTasks(tasksRequest)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    private var selectedTask: Task? = null
    fun getSelectedTask() = selectedTask
    fun clearSelectedTask() {
        selectedTask = null
    }

    fun setSelectedTask(task: Task) {
        selectedTask = task
    }


    private var transferItems: ArrayList<TaskItem> = ArrayList()
    fun getTransferItems() = transferItems
    fun setTransferItems(itemList: ArrayList<TaskItem>) {
        for(item in itemList){
            item.Bins = ArrayList()
        }
        transferItems.addAll(itemList)
    }

    fun clearTransferItems() = transferItems.clear()


    var selectedFromWarehouse: String = ""
    fun getFromWareHouseSiteID(id: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainActivityRepository.getFromWareHouseSiteID(id)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getOutboundBins(itemID: String, siteID: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainActivityRepository.getOutboundBins(itemID, siteID)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun setOutboundBins(selectedBins: ArrayList<SelectedBin>){
        transferItems.find{it.LineID == selectedLineNum}!!.Bins.clear()
        transferItems.find{it.LineID == selectedLineNum}!!.Bins.addAll(selectedBins)
    }

    fun updateTransferItems(taskItem: TaskItem) {
        transferItems[transferItems.indexOf(taskItem)].Bins.clear()
    }


    fun createOutboundDelivery(createOutboundDeliveryRequest: CreateOutboundDeliveryRequest) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = mainActivityRepository.createOutboundDelivery(
                        createOutboundDeliveryRequest
                    )
                )
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    var outBoundDeliveryRequestIDForDB: String = ""

    fun saveOutboundDeliveryDocument(outboundDeliveryRequest: CreateOutboundDeliveryRequest) =
        liveData(Dispatchers.IO) {
            var payload = ""
            payload += "{\n" +
                    "TaskID: ${outboundDeliveryRequest.TaskID}\n" +
                    "FromSiteID: ${outboundDeliveryRequest.FromSiteID}\n" +
                    "TaskDetails: [\n"
            for(item in outboundDeliveryRequest.TaskDetails){
                payload += "{\n" +
                        "ItemCode: ${item.ItemCode}\n" +
                        "LogisticDetails: [\n"
                for(bin in item.LogisticDetails){
                    payload += "{\n" +
                            "LogisticID: ${bin.LogisticID}\n" +
                            "Quantity: ${bin.Quantity}\n" +
                            "UOM: ${bin.UOM}\n" +
                            "}\n"
                }
                payload += "]\n" +
                        "}\n"
            }
            payload += "]\n}"


            var docDateDB = DateUtilsApp.getUTCFormattedDateTimeString(
                SimpleDateFormat(
                    "dd/MM/yyyy - hh:mm a",
                    Locale.getDefault()
                ), Calendar.getInstance().time
            )

            outBoundDeliveryRequestIDForDB = outboundDeliveryRequest.TaskID + "$" + Calendar.getInstance().timeInMillis.toString()
            var document = PostedDocumentEntity(
                ID = outBoundDeliveryRequestIDForDB,
                docType = "Outbound Delivery Task",
                dateTime = docDateDB,
                payload = payload,
                status = Constants.PENDING
            )

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




}