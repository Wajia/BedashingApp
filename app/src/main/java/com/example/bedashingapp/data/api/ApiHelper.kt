package com.example.bedashingapp.data.api

import com.example.bedashingapp.data.model.remote.*
import com.example.bedashingapp.utils.Constants
import retrofit2.Call


class ApiHelper(private val apiService: ApiService) {

    suspend fun getPurchaseOrders() = apiService.getPurchaseOrders(Constants.BASIC_AUTH)

    suspend fun getItemCollectionForPOs() = apiService.getItemCollectionForPOs(Constants.BASIC_AUTH)

    suspend fun getAllItems() = apiService.getAllItems(Constants.BASIC_AUTH)

    suspend fun getLogisticsAreaCollection() =
        apiService.getLogisticsAreaCollection(Constants.BASIC_AUTH)

    suspend fun login() = apiService.login(Constants.BASIC_AUTH)

    suspend fun receiveGoodsPO(payload: ReceiveGoodsPORequest) = apiService.receiveGoodsPO(
        "${Constants.BASE_URL_FOR_MIDDLEWARE_API}api/integration/CreateInboundDelivery",
        Constants.BASIC_AUTH_FOR_MIDDLEWARE_API,
        payload
    )


    fun getCRFTokenInboundDelivery(): Call<ReceiveGoodsPOResponse> {
        val headers = HashMap<String, String>()
        headers["Authorization"] = Constants.BASIC_AUTH
        headers["x-csrf-token"] = "fetch"

        return apiService.getCRFTokenInboundDeliveryCollection(headers)
    }

//    suspend fun receiveGoodsPO(crfToken: String, cookie: String, payload: ReceiveGoodsPORequest): ReceiveGoodsResponse1 {
//        val headers = HashMap<String, String>()
//        headers["Authorization"] = Constants.BASIC_AUTH
//        headers["x-csrf-token"] = crfToken
//        headers["Cookie"] = cookie
//
//        return apiService.receiveGoodsPO(headers, payload)
//    }

    suspend fun postGoodsReceipt(
        crfToken: String,
        cookie: String,
        id: String
    ): PostGoodsReceiptResponse {
        val headers = HashMap<String, String>()
        headers["Authorization"] = Constants.BASIC_AUTH
        headers["x-csrf-token"] = crfToken
        headers["Cookie"] = cookie

        return apiService.postGoodsReceipt(headers, "'$id'")
    }


    suspend fun getTasks(tasksRequest: TasksRequest) = apiService.getTasks(
        "${Constants.BASE_URL_FOR_MIDDLEWARE_API}api/integration/GetTasks",
        Constants.BASIC_AUTH_FOR_MIDDLEWARE_API,
        tasksRequest
    )

    suspend fun getFromWareHouseSiteID(id: String) = apiService.getFromWareHouseSiteID(Constants.BASIC_AUTH, "'$id'")

    suspend fun getOutboundBins(itemID: String, siteID: String): OutboundBinsResponse{
        var url = "https://my357593.sapbydesign.com/sap/byd/odata/ana_businessanalytics_analytics.svc/RPZ84027081E0F03244F12104QueryResults?\$select=CLOG_AREA_UUID,CMATERIAL_UUID,COWNER_UUID,CSITE_UUID,KCON_HAND_STOCK,KCRESTRICTED_STOCK,KCUN_RESTRICTED_STOCK&\$filter=(CMATERIAL_UUID eq '${itemID}') and (CSITE_UUID eq '${siteID}')&\$format=json"
        return apiService.getOutboundBins(url, Constants.BASIC_AUTH)
    }

    suspend fun createOutboundDelivery(payload: CreateOutboundDeliveryRequest) = apiService.createOutboundDelivery(
        "${Constants.BASE_URL_FOR_MIDDLEWARE_API}api/integration/CreateOutboundDelivery",
        Constants.BASIC_AUTH_FOR_MIDDLEWARE_API,
        payload
    )



}