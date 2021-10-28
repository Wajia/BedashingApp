package com.example.bedashingapp.data.api

import com.example.bedashingapp.data.model.remote.*
import com.example.bedashingapp.utils.Constants
import retrofit2.Call


class ApiHelper(private val apiService: ApiService) {



//    suspend fun receiveGoodsPO(crfToken: String, cookie: String, payload: ReceiveGoodsPORequest): ReceiveGoodsResponse1 {
//        val headers = HashMap<String, String>()
//        headers["Authorization"] = Constants.BASIC_AUTH
//        headers["x-csrf-token"] = crfToken
//        headers["Cookie"] = cookie
//
//        return apiService.receiveGoodsPO(headers, payload)
//    }

    suspend fun login(mainURL: String, payload: LoginRequest): LoginResponse{
        val url = "$mainURL/b1s/v1/Login"
        return apiService.login(url, payload)
    }

    suspend fun getUserDetails(mainURL: String, companyName: String, sessionID: String, userCode: String): GetUserDetailsResponse{
        val url = "$mainURL/b1s/v1/Users?\$filter= UserCode eq '$userCode'"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        return apiService.getUserDetails(url, headers)
    }

    suspend fun checkConnection(mainURL: String, companyName: String, sessionID: String, userID: String): GetUserDetailsResponse{
        val url = "$mainURL/b1s/v1/Users($userID)"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        return apiService.checkConnection(url, headers)
    }

    suspend fun getBranches(mainURL: String, companyName: String, sessionID: String): GetBranchesResponse{
        val url = "$mainURL/b1s/v1/BusinessPlaces?\$select=BPLID,BPLName,BPLNameForeign,DefaultCustomerID,DefaultVendorID,DefaultWarehouseID&\$filter=Disabled eq 'tNO'"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        return apiService.getBranches(url, headers)
    }

    suspend fun getWarehouses(mainURL: String, companyName: String, sessionID: String): GetWarehousesResponse{
        val url = "$mainURL/b1s/v1/Warehouses?\$select=BusinessPlaceID,WarehouseCode,WarehouseName&\$filter=Inactive eq 'tNO'"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        return apiService.getWarehouses(url, headers)
    }

    suspend fun getItemsMaster(mainURL: String, companyName: String, sessionID: String, warehouseCode: String, from: Int): GetItemsMasterResponse{
        val url = "$mainURL/b1s/v1/\$crossjoin(Items,Items/ItemWarehouseInfoCollection)?\$expand=Items(\$select=ItemCode,ItemName,BarCode,UoMGroupEntry,U_Deprtmnt,U_PrdctCat,Frozen,ItemsGroupCode),Items/ItemWarehouseInfoCollection(\$select=WarehouseCode,InStock)&\$filter=Items/ItemWarehouseInfoCollection/ItemCode eq Items/ItemCode and Items/ItemWarehouseInfoCollection/WarehouseCode eq '$warehouseCode' and ((Items/BarCode ne 'X') and (Items/BarCode ne '0') and (Items/BarCode ne null) and (Items/BarCode ne 'xx')) and ((Items/Series eq 70)  or  (Items/Series eq 71)  or  (Items/Series eq 74) )&\$skip=$from"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        headers["Prefer"] = "odata.maxpagesize=500"
        return apiService.getItemsMaster(url, headers)
    }

    suspend fun getUoms(mainURL: String, companyName: String, sessionID: String): GetUOMsResponse{
        val url = "$mainURL/b1s/v1/UnitOfMeasurements"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        return apiService.getUoms(url, headers)
    }

    suspend fun getUomGroups(mainURL: String, companyName: String, sessionID: String): GetUomGroupsResponse{
        val url = "$mainURL/b1s/v1/UnitOfMeasurementGroups?\$select=AbsEntry,UoMGroupDefinitionCollection"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        return apiService.getUomGroups(url, headers)
    }

    suspend fun getBarcodes(mainURL: String, companyName: String, sessionID: String, from: Int): GetBarcodesResponse{
        val url = "$mainURL/b1s/v1/\$crossjoin(BarCodes,Items)?\$expand=BarCodes(\$select=AbsEntry,ItemNo,UoMEntry,Barcode)&\$filter=BarCodes/ItemNo eq Items/ItemCode and ((Items/BarCode ne 'X') and (Items/BarCode ne '0') and (Items/BarCode ne null) and (Items/BarCode ne 'xx')) and ((Items/Series eq 70)  or  (Items/Series eq 71)  or  (Items/Series eq 74) )&\$skip=$from"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        headers["Prefer"] = "odata.maxpagesize=500"
        return apiService.getBarcodes(url, headers)
    }

    suspend fun getPOCount(mainURL: String, companyName: String, sessionID: String, BPLID: Int, VendorCode: String): Int{
        val url = "$mainURL/b1s/v1/PurchaseOrders/\$count?\$filter=DocumentStatus eq 'bost_Open' and BPL_IDAssignedToInvoice eq $BPLID and CardCode eq '$VendorCode'"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        return apiService.getPOCount(url, headers)
    }

    suspend fun getGRPOCount(mainURL: String, companyName: String, sessionID: String, BPLID: Int, VendorCode: String): Int{
        val url = "$mainURL/b1s/v1/PurchaseDeliveryNotes/\$count?\$filter=DocumentStatus eq 'bost_Open' and BPL_IDAssignedToInvoice eq $BPLID and CardCode eq '$VendorCode'"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        return apiService.getGRPOCount(url, headers)
    }

    suspend fun getDeliveryCount(mainURL: String, companyName: String, sessionID: String, BPLID: Int): Int{
        val url = "$mainURL/b1s/v1/DeliveryNotes/\$count?\$filter=DocumentStatus eq 'bost_Open' and BPL_IDAssignedToInvoice eq $BPLID"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        return apiService.getDeliveryCount(url, headers)
    }

    suspend fun getInventoryCount(mainURL: String, companyName: String, sessionID: String, BPLID: Int): Int{
        val url = "$mainURL/b1s/v1/InventoryCountings/\$count?\$filter=DocumentStatus eq 'cdsOpen' and BranchID  eq $BPLID"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        return apiService.getInventoryCount(url, headers)
    }


    suspend fun getInventoryCountings(mainURL: String, companyName: String, sessionID: String, BPLID: Int): GetInventoryCountingsResponse{
        val url = "$mainURL/b1s/v1/InventoryCountings?\$select=DocumentEntry,CountDate,DocumentNumber,BranchID&\$filter=DocumentStatus eq 'cdsOpen' and BranchID eq $BPLID"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        headers["Prefer"] = "odata.maxpagesize=2000"
        return apiService.getInventoryCountings(url, headers)
    }







}