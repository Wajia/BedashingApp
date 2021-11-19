package com.example.bedashingapp.data.api

import com.example.bedashingapp.data.model.remote.*
import retrofit2.Call
import retrofit2.http.*


interface ApiService {

//    @POST("khinbounddelivery/PostGoodsReceipt")
//    suspend fun postGoodsReceipt(
//        @HeaderMap headers: HashMap<String, String>,
//        @Query("ObjectID") id: String,
//    ): PostGoodsReceiptResponse


    @POST
    suspend fun login(
        @Url url: String,
        @Body payload: LoginRequest
    ): LoginResponse


    @GET
    suspend fun getUserDetails(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>
    ): GetUserDetailsResponse


    @GET
    suspend fun checkConnection(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>
    ): GetUserDetailsResponse

    @GET
    suspend fun getBranches(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>
    ): GetBranchesResponse

    @GET
    suspend fun getWarehouses(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>
    ): GetWarehousesResponse

    @GET
    suspend fun getItemsMaster(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>
    ): GetItemsMasterResponse

    @GET
    suspend fun getUoms(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>
    ): GetUOMsResponse

    @GET
    suspend fun getUomGroups(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>
    ): GetUomGroupsResponse

    @GET
    suspend fun getBarcodes(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>
    ): GetBarcodesResponse

    @GET
    suspend fun getPOCount(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>
    ): Int

    @GET
    suspend fun getPO(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>
    ): GetPoResponse

    @GET
    suspend fun getOpenPO(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>
    ): GetOpenPOResponse

    @GET
    suspend fun getGRPOCount(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>
    ): Int

    @GET
    suspend fun getDeliveryCount(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>
    ): Int

    @GET
    suspend fun getInventoryCount(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>
    ): Int

    @GET
    suspend fun getInventoryCountings(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>
    ): GetInventoryCountingsResponse

    @GET
    suspend fun getInventoryStatus(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>
    ): GetInventoryStatusResponse

    @GET
    suspend fun getItem(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>
    ): GetItemsMasterResponse

    @POST
    fun inventoryCountings(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>,
        @Body payload: InventoryCountingRequest
    ): Call<AddInventoryCountingResponse>
    @POST
    fun GoodsReciept(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>,
        @Body payload: InventoryCountingRequest
    ): Call<AddInventoryCountingResponse>

    @POST
    fun postPO(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>,
        @Body payload: PostPurchaseOrderRequest
    ): Call<AddInventoryCountingResponse>
    @POST
    fun postPurchaseDeliveryNotes(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>,
        @Body payload: PurchaseDeliveryNotesRequest
    ): Call<AddInventoryCountingResponse>
}