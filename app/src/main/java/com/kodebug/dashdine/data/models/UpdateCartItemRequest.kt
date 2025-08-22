package com.kodebug.dashdine.data.models

data class UpdateCartItemRequest(
    val quantity: Int,
    val cartItemId: String
)
