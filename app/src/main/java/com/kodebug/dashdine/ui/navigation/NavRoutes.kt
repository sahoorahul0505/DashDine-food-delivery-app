package com.kodebug.dashdine.ui.navigation

import kotlinx.serialization.Serializable


@Serializable
object Auth
@Serializable
object SignUp

@Serializable
object Login

@Serializable
object Home

@Serializable
data class RestaurantDetail(val restaurantID: String,val restaurantName: String, val restaurantImageUrls: String )