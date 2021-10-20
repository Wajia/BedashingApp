package com.example.bedashingapp.data.api

import com.example.bedashingapp.data.model.remote.*
import retrofit2.Call
import retrofit2.http.*


interface ApiService {

    @GET("khpurchaseorder/PurchaseOrderCollection?\$expand=Supplier/SupplierName,BuyerParty&\$top=1000")
    suspend fun getPurchaseOrders(@Header("Authorization") token: String): PurchaseOrderCollectionResponse

    @GET("khpurchaseorder/ItemCollection")
    suspend fun getItemCollectionForPOs(@Header("Authorization") token: String): ItemCollectionResponse

    @GET("vmumaterial/MaterialCollection?\$expand=QuantityConversion&\$top=99999")
    suspend fun getAllItems(@Header("Authorization") token: String): MaterialCollectionResponse

    @GET("khinbounddelivery/InboundDeliveryCollection")
    fun getCRFTokenInboundDeliveryCollection(
        @HeaderMap headers: HashMap<String, String>
    ): Call<ReceiveGoodsPOResponse>

    @POST
    suspend fun receiveGoodsPO(
        @Url url: String,
        @Header("Authorization") token: String,
        @Body receiveGoodsPORequest: ReceiveGoodsPORequest
    ): ReceiveGoodsPOResponse

    @POST("khinbounddelivery/PostGoodsReceipt")
    suspend fun postGoodsReceipt(
        @HeaderMap headers: HashMap<String, String>,
        @Query("ObjectID") id: String,
    ): PostGoodsReceiptResponse

    @GET("test/EmployeeCollection?\$expand=EmployeeCommon&\$top=1999")
    suspend fun login(@Header("Authorization") token: String): LoginResponse

    @GET("logisticsarea/LogisticsAreaCollection?\$filter=TypeCode eq '2'&\$top=9999")
    suspend fun getLogisticsAreaCollection(@Header("Authorization") token: String): LogisticsAreaCollectionResponse

    @POST
    suspend fun getTasks(
        @Url url: String,
        @Header("Authorization") token: String,
        @Body getTasksRequest: TasksRequest
    ): List<TasksResponse>

    @GET("logisticsarea/log_id")
    suspend fun getFromWareHouseSiteID(
        @Header("Authorization") token: String,
        @Query("ID") id: String
    ): FromWareHouseSiteIdResponse

    @GET
    suspend fun getOutboundBins(
        @Url url: String,
        @Header("Authorization") token: String
    ): OutboundBinsResponse

    @POST
    suspend fun createOutboundDelivery(
        @Url url: String,
        @Header("Authorization") token: String,
        @Body createOutboundDeliveryRequest: CreateOutboundDeliveryRequest
    ): ReceiveGoodsPOResponse

}