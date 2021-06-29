package com.awesome.amuclient.api.service

import com.awesome.amuclient.api.response.StoreResponse
import retrofit2.Call
import retrofit2.http.*

interface GetStoreInfoService {
    @Headers("accept: application/json",
        "content-type: application/json")
    @GET("/getStoreInfo")
    fun getStoreInfo(@Query("store_id") store_id:String) : Call<StoreResponse>
}