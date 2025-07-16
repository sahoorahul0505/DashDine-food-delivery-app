package com.kodebug.dashdine.data

import retrofit2.http.GET

interface DashDineApiService {

    @GET("food")
    suspend fun getFood(): List<String>
}