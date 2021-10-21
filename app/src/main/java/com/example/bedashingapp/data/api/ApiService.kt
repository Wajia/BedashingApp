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
}