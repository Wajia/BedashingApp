package com.example.bedashingapp.data.api

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class RetrofitBuilder {

    companion object {
        private const val BASE_URL = "https://my357593.sapbydesign.com/sap/byd/odata/cust/v1/"


        @Volatile
        private var apiServiceINSTANCE: ApiService? = null

        private fun getRetrofit(client: OkHttpClient, ip: String): Retrofit {

            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build() //Doesn't require the adapter
        }

        fun getApiService(ip: String): ApiService {
            //interceptor for logs in retrofit
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder()
            client.connectTimeout(2, TimeUnit.MINUTES)
            client.readTimeout(2, TimeUnit.MINUTES)
            client.writeTimeout(2, TimeUnit.MINUTES)

            client.addInterceptor { chain ->
                val requestBuilder: Request.Builder = chain.request().newBuilder()
                requestBuilder.header("Accept", "application/json")
                chain.proceed(requestBuilder.build())
            }
            val client1 = client.addInterceptor(interceptor).build()


            val tempInstance = apiServiceINSTANCE
            if (tempInstance != null) {
                return apiServiceINSTANCE!!
            } else {
                synchronized(this) {
                    apiServiceINSTANCE = getRetrofit(client1, ip).create(ApiService::class.java)

                    return apiServiceINSTANCE!!
                }
            }
        }
    }
}