package com.kodebug.dashdine.ui.navigation

import com.kodebug.dashdine.data.models.FoodItem
import kotlinx.serialization.Serializable

interface NavRoutes

@Serializable
object Auth: NavRoutes
@Serializable
object SignUp: NavRoutes

@Serializable
object Login: NavRoutes

@Serializable
object Home: NavRoutes

@Serializable
data class RestaurantDetail(val restaurantID: String,val restaurantName: String, val restaurantImageUrls: String ): NavRoutes

@Serializable
data class FoodDetail(val foodItem: FoodItem): NavRoutes

@Serializable
object Cart: NavRoutes

@Serializable
object Notification: NavRoutes