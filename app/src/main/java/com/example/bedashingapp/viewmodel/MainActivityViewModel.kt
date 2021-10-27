package com.example.bedashingapp.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.bedashingapp.data.model.db.BarcodeEntity
import com.example.bedashingapp.data.model.db.ItemEntity
import com.example.bedashingapp.data.model.db.UOMEntity
import com.example.bedashingapp.data.model.db.UOMGroupEntity
import com.example.bedashingapp.data.model.remote.*
import com.example.bedashingapp.data.respository.MainActivityRepository
import com.example.bedashingapp.utils.Resource
import kotlinx.coroutines.Dispatchers


class MainActivityViewModel(private val mainActivityRepository: MainActivityRepository) :
    ViewModel() {

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

    fun getPOCount(mainURL: String, companyName: String, sessionID: String, bplID: String, VendorCode: String) =
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

    fun getGRPOCount(mainURL: String, companyName: String, sessionID: String, bplID: String, VendorCode: String) =
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
                    AbsEntry = it.AbsEntry,
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


    fun updateStatusOfDocument(id: String, status: String, response: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = mainActivityRepository.updateStatusOfDocument(
                            id,
                            status,
                            response
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }


}