package com.kodebug.dashdine.data

import com.kodebug.dashdine.data.models.AddToCartRequest
import com.kodebug.dashdine.data.models.AddToCartResponse
import com.kodebug.dashdine.data.models.AuthResponse
import com.kodebug.dashdine.data.models.CartResponse
import com.kodebug.dashdine.data.models.CategoryResponse
import com.kodebug.dashdine.data.models.FoodItemResponse
import com.kodebug.dashdine.data.models.GenericMessageResponse
import com.kodebug.dashdine.data.models.LoginRequest
import com.kodebug.dashdine.data.models.OAuthRequest
import com.kodebug.dashdine.data.models.RestaurantsResponse
import com.kodebug.dashdine.data.models.SignUpRequest
import com.kodebug.dashdine.data.models.UpdateCartItemRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DashDineApiService {

    // ---  AUTHENTICATION SERVICES ---
    @POST("/auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): Response<AuthResponse>

    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("/auth/oauth")
    suspend fun oAuth(@Body request: OAuthRequest): Response<AuthResponse>


    // ---  CATEGORY SERVICES ---
    @GET("/categories")
    suspend fun getAllCategories(): Response<CategoryResponse>

    // --- RESTAURANT SERVICES ---

    @GET("/restaurants")
    suspend fun getRestaurants(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ): Response<RestaurantsResponse>

    @GET("/restaurants/{id}/menu")
    suspend fun getFoodItemsForRestaurant(@Path("id") restaurantId: String): Response<FoodItemResponse>

    // --- CART SERVICES ---

    @POST("/cart")
    suspend fun addToCart(@Body request: AddToCartRequest): Response<AddToCartResponse>

    @GET("/cart")
    suspend fun getCart(): Response<CartResponse>

    @PATCH("/cart")
    suspend fun updateCart(@Body request: UpdateCartItemRequest): Response<GenericMessageResponse>

    @DELETE("/cart/{cartItemId}")
    suspend fun deleteCartItem(@Path("cartItemId") cartItemId: String): Response<GenericMessageResponse>

}