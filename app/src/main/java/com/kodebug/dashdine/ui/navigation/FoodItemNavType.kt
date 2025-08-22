package com.kodebug.dashdine.ui.navigation

import androidx.navigation.NavType
import androidx.savedstate.SavedState
import com.kodebug.dashdine.data.models.FoodItem
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.net.URLDecoder
import java.net.URLEncoder

val foodItemNavType = object : NavType<FoodItem>(false){
    override fun get(
        bundle: SavedState,
        key: String
    ): FoodItem? {
        return parseValue(bundle.getString(key).toString()).copy(
            imageUrl = URLDecoder.decode(
                parseValue(bundle.getString(key).toString()).imageUrl,
                "UTF-8"
            )
        )
    }

    override fun parseValue(value: String): FoodItem {
        return Json.decodeFromString(FoodItem.serializer(), value)
    }

    override fun serializeAsValue(value: FoodItem): String {
        return Json.encodeToString(FoodItem.serializer(), value.copy(
            imageUrl = URLEncoder.encode(value.imageUrl,"UTF-8")
        ))
    }

    override fun put(
        bundle: SavedState,
        key: String,
        value: FoodItem
    ) {
        return bundle.putString(key, serializeAsValue(value))
    }
}