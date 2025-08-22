package com.kodebug.dashdine.utils

object StringUtils {
    fun formatCurrency(value : Double) : String {
        val currencyFormater = java.text.NumberFormat.getCurrencyInstance()
        currencyFormater.currency = java.util.Currency.getInstance("USD")
        return currencyFormater.format(value)
    }
}