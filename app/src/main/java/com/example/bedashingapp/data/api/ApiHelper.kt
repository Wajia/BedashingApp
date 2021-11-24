package com.example.bedashingapp.data.api

import com.example.bedashingapp.data.model.remote.*
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

    suspend fun login(mainURL: String, payload: LoginRequest): LoginResponse {
        val url = "$mainURL/b1s/v1/Login"
        return apiService.login(url, payload)
    }

    suspend fun getUserDetails(
        mainURL: String,
        companyName: String,
        sessionID: String,
        userCode: String
    ): GetUserDetailsResponse {
        val url = "$mainURL/b1s/v1/Users?\$filter= UserCode eq '$userCode'"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        return apiService.getUserDetails(url, headers)
    }

    suspend fun checkConnection(
        mainURL: String,
        companyName: String,
        sessionID: String,
        userID: String
    ): GetUserDetailsResponse {
        val url = "$mainURL/b1s/v1/Users($userID)"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        return apiService.checkConnection(url, headers)
    }

    suspend fun getBranches(
        mainURL: String,
        companyName: String,
        sessionID: String
    ): GetBranchesResponse {
        val url =
            "$mainURL/b1s/v1/BusinessPlaces?\$select=BPLID,BPLName,BPLNameForeign,DefaultCustomerID,DefaultVendorID,DefaultWarehouseID&\$filter=Disabled eq 'tNO'"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        headers["Prefer"] = "odata.maxpagesize=500"
        return apiService.getBranches(url, headers)
    }

    suspend fun getWarehouses(
        mainURL: String,
        companyName: String,
        sessionID: String
    ): GetWarehousesResponse {
        val url =
            "$mainURL/b1s/v1/Warehouses?\$select=BusinessPlaceID,WarehouseCode,WarehouseName&\$filter=Inactive eq 'tNO'"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        headers["Prefer"] = "odata.maxpagesize=500"
        return apiService.getWarehouses(url, headers)
    }

    suspend fun getItemsMaster(
        mainURL: String,
        companyName: String,
        sessionID: String,
        warehouseCode: String,
        from: Int
    ): GetItemsMasterResponse {
        val url =
            "$mainURL/b1s/v1/\$crossjoin(Items,Items/ItemWarehouseInfoCollection)?\$expand=Items(\$select=ItemCode,ItemName,BarCode,UoMGroupEntry,U_Deprtmnt,U_PrdctCat,Frozen,ItemsGroupCode),Items/ItemWarehouseInfoCollection(\$select=WarehouseCode,InStock)&\$filter=Items/ItemWarehouseInfoCollection/ItemCode eq Items/ItemCode and Items/ItemWarehouseInfoCollection/WarehouseCode eq '$warehouseCode' and ((Items/BarCode ne 'X') and (Items/BarCode ne '0') and (Items/BarCode ne null) and (Items/BarCode ne 'xx')) and ((Items/Series eq 70)  or  (Items/Series eq 71)  or  (Items/Series eq 74) )&\$skip=$from"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        headers["Prefer"] = "odata.maxpagesize=500"
        return apiService.getItemsMaster(url, headers)
    }

    suspend fun getUoms(mainURL: String, companyName: String, sessionID: String): GetUOMsResponse {
        val url = "$mainURL/b1s/v1/UnitOfMeasurements"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        headers["Prefer"] = "odata.maxpagesize=500"
        return apiService.getUoms(url, headers)
    }

    suspend fun getUomGroups(
        mainURL: String,
        companyName: String,
        sessionID: String
    ): GetUomGroupsResponse {
        val url =
            "$mainURL/b1s/v1/UnitOfMeasurementGroups?\$select=AbsEntry,UoMGroupDefinitionCollection"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        headers["Prefer"] = "odata.maxpagesize=500"
        return apiService.getUomGroups(url, headers)
    }

    suspend fun getBarcodes(
        mainURL: String,
        companyName: String,
        sessionID: String,
        from: Int
    ): GetBarcodesResponse {
        val url =
            "$mainURL/b1s/v1/\$crossjoin(BarCodes,Items)?\$expand=BarCodes(\$select=AbsEntry,ItemNo,UoMEntry,Barcode)&\$filter=BarCodes/ItemNo eq Items/ItemCode and ((Items/BarCode ne 'X') and (Items/BarCode ne '0') and (Items/BarCode ne null) and (Items/BarCode ne 'xx')) and ((Items/Series eq 70)  or  (Items/Series eq 71)  or  (Items/Series eq 74) )&\$skip=$from"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        headers["Prefer"] = "odata.maxpagesize=500"
        return apiService.getBarcodes(url, headers)
    }

    suspend fun getPOCount(
        mainURL: String,
        companyName: String,
        sessionID: String,
        BPLID: Int,
        VendorCode: String
    ): Int {
        val url =
            "$mainURL/b1s/v1/PurchaseOrders/\$count?\$filter=DocumentStatus eq 'bost_Open' and BPL_IDAssignedToInvoice eq $BPLID and CardCode eq '$VendorCode'"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        return apiService.getPOCount(url, headers)
    }

    suspend fun getPO(
        mainURL: String,
        sessionID: String,
        companyName: String,
        branchName: String,
        userHeadOfficeCardCode: String
    ): GetPoResponse {
        val url =
            mainURL + "/b1s/v1/PurchaseOrders?\$select=DocEntry,DocDate,DocNum,DocDueDate,RequriedDate,BPLName&\$filter=DocumentStatus eq 'bost_Open' and BPL_IDAssignedToInvoice eq " + branchName + " and CardCode eq '" + userHeadOfficeCardCode + "'"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        headers["Prefer"] = "odata.maxpagesize=2000"
        headers["Content-Type"] = "application/json; charset=UTF-8"
        return apiService.getPO(url, headers)
    }

    suspend fun getPODetails(
        mainURL: String,
        sessionID: String,
        companyName: String,
        branchName: String,
        userHeadOfficeCardCode: String
    ): GetPoResponse {
        val url =
            mainURL + "/b1s/v1/PurchaseOrders?\$filter=DocumentStatus eq 'bost_Open' and  BPL_IDAssignedToInvoice eq " + branchName + " and CardCode eq '" + userHeadOfficeCardCode + "' "
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        headers["Prefer"] = "odata.maxpagesize=2000"
        headers["Content-Type"] = "application/json; charset=UTF-8"
        return apiService.getPODetails(url, headers)
    }

    fun PurchaseDeliveryNotes(
        mainURL: String,
        sessionID: String,
        companyName: String,
        payload: PurchaseDeliveryNotesRequest
    ): Call<AddInventoryCountingResponse> {
        val url =
            mainURL + "/b1s/v1/PurchaseDeliveryNotes"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        headers["Content-Type"] = "application/json; charset=UTF-8"
        return apiService.postPurchaseDeliveryNotes(url, headers, payload)
    }


    suspend fun getGRPOCount(
        mainURL: String,
        companyName: String,
        sessionID: String,
        BPLID: Int,
        VendorCode: String
    ): Int {
        val url =
            "$mainURL/b1s/v1/PurchaseDeliveryNotes/\$count?\$filter=DocumentStatus eq 'bost_Open' and BPL_IDAssignedToInvoice eq $BPLID and CardCode eq '$VendorCode'"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        return apiService.getGRPOCount(url, headers)
    }

    suspend fun getOPenPO(
        mainURL: String,
        companyName: String,
        sessionID: String,
        docNumber: String
    ): GetOpenPOResponse {

        val url =
            "$mainURL/b1s/v1/PurchaseOrders?\$filter= DocNum eq $docNumber"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        headers["Content-Type"] = "application/json; charset=UTF-8"
        return apiService.getOpenPO(url, headers)
    }

    suspend fun getDeliveryCount(
        mainURL: String,
        companyName: String,
        sessionID: String,
        BPLID: Int
    ): Int {
        val url =
            "$mainURL/b1s/v1/DeliveryNotes/\$count?\$filter=DocumentStatus eq 'bost_Open' and BPL_IDAssignedToInvoice eq $BPLID"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        return apiService.getDeliveryCount(url, headers)
    }

    suspend fun getInventoryCount(
        mainURL: String,
        companyName: String,
        sessionID: String,
        BPLID: Int
    ): Int {
        val url =
            "$mainURL/b1s/v1/InventoryCountings/\$count?\$filter=DocumentStatus eq 'cdsOpen' and BranchID  eq $BPLID"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        return apiService.getInventoryCount(url, headers)
    }


    suspend fun getInventoryCountings(
        mainURL: String,
        companyName: String,
        sessionID: String,
        BPLID: Int
    ): GetInventoryCountingsResponse {
        val url =
            "$mainURL/b1s/v1/InventoryCountings?\$select=DocumentEntry,CountDate,DocumentNumber,BranchID&\$filter=DocumentStatus eq 'cdsOpen' and BranchID eq $BPLID"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        headers["Prefer"] = "odata.maxpagesize=2000"
        return apiService.getInventoryCountings(url, headers)
    }

    suspend fun getInventoryStatus(
        mainURL: String,
        companyName: String,
        sessionID: String,
        itemCode: String
    ): GetInventoryStatusResponse {
        val url =
            "$mainURL/b1s/v1/\$crossjoin(Items,Items/ItemWarehouseInfoCollection)?\$expand=Items/ItemWarehouseInfoCollection(\$select=InStock,WarehouseCode)&\$filter= Items/ItemCode eq Items/ItemWarehouseInfoCollection/ItemCode and Items/ItemCode eq '$itemCode'"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        return apiService.getInventoryStatus(url, headers)
    }

    suspend fun getItem(
        mainURL: String,
        companyName: String,
        sessionID: String,
        warehouseCode: String,
        itemCode: String
    ): GetItemsMasterResponse {
        val url =
            "$mainURL/b1s/v1/\$crossjoin(Items,Items/ItemWarehouseInfoCollection)?\$expand=Items/ItemWarehouseInfoCollection(\$select=InStock),Items(\$select=ItemsGroupCode,U_Deprtmnt,U_PrdctCat)&\$filter= Items/ItemCode eq Items/ItemWarehouseInfoCollection/ItemCode and Items/ItemCode eq '$itemCode' and Items/ItemWarehouseInfoCollection/WarehouseCode eq '$warehouseCode'"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        return apiService.getItem(url, headers)
    }

    suspend fun getItemPO(
        mainURL: String,
        companyName: String,
        sessionID: String,
        warehouseCode: String,
        itemCode: String
    ): GetItemsMasterResponse {

        val url = "\$crossjoin(Items,Items/ItemWarehouseInfoCollection)" +
                "?\$expand=Items/ItemWarehouseInfoCollection(\$select=InStock,StandardAveragePrice,WarehouseCode),Items(\$select=ItemsGroupCode,U_Deprtmnt,U_PrdctCat)" +
                "&\$filter= Items/ItemCode eq Items/ItemWarehouseInfoCollection/ItemCode" +
                " and Items/ItemCode eq '" + itemCode + "' and" +
                " ((Items/ItemWarehouseInfoCollection/WarehouseCode eq '" + warehouseCode + "')  or (Items/ItemWarehouseInfoCollection/WarehouseCode eq '01'))"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        return apiService.getItem(url, headers)
    }

    fun inventoryCountings(
        mainURL: String,
        companyName: String,
        sessionID: String,
        payload: InventoryCountingRequest
    ): Call<AddInventoryCountingResponse> {
        val url = "$mainURL/b1s/v1/InventoryCountings"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        return apiService.inventoryCountings(url, headers, payload)
    }

    fun deliveryNotes(
        mainURL: String,
        companyName: String,
        sessionID: String,
        payload: ProfessionalCheckoutRequest
    ): Call<AddInventoryCountingResponse> {
        val url = "$mainURL/b1s/v1/DeliveryNotes"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        return apiService.deliveryNotes(url, headers, payload)
    }

    fun postPO(
        mainURL: String,
        companyName: String,
        sessionID: String,
        payload: PostPurchaseOrderRequest
    ): Call<AddPurchaseOderResponse> {
        val url = "$mainURL/b1s/v1/PurchaseOrders"
        val headers = HashMap<String, String>()
        headers["Cookie"] = "B1SESSION=$sessionID;CompanyDB=$companyName"
        return apiService.postPO(url, headers, payload)
    }

}