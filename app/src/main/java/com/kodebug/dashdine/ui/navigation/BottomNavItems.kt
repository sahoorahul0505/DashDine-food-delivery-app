package com.kodebug.dashdine.ui.navigation

import com.kodebug.dashdine.R

sealed class BottomNavItems(
    val routes: NavRoutes,
    val icon: Int
) {
    object Home : BottomNavItems(routes = com.kodebug.dashdine.ui.navigation.Home, icon = R.drawable.ic_explore)
    object Cart : BottomNavItems(routes = com.kodebug.dashdine.ui.navigation.Cart, icon = R.drawable.ic_location)
    object Notification :
        BottomNavItems(routes = com.kodebug.dashdine.ui.navigation.Notification, icon = R.drawable.ic_notification)
}