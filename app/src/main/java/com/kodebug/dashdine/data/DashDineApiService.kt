package com.kodebug.dashdine.data

import com.kodebug.dashdine.data.models.AuthResponse
import com.kodebug.dashdine.data.models.LoginRequest
import com.kodebug.dashdine.data.models.OAuthRequest
import com.kodebug.dashdine.data.models.SignUpRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface DashDineApiService {

    @GET("/food")
    suspend fun getFood(): List<String>

    @POST("/auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): AuthResponse

    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("/auth/oauth")
    suspend fun oAuth(@Body request: OAuthRequest): AuthResponse
}