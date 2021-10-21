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



}