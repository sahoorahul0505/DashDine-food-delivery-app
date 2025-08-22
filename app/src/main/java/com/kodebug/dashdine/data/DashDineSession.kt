package com.kodebug.dashdine.data

import android.content.Context
import android.util.Log
import androidx.core.content.edit

class DashDineSession(val context: Context) {
    val sharedPreferences = context.getSharedPreferences("DashDineSession", Context.MODE_PRIVATE)

    fun storeToken(token: String){
        sharedPreferences.edit { putString("token", token) }
    }

    fun getToken() : String? {
        sharedPreferences.getString("token", null)?.let {
            return it
        }
        return sharedPreferences.getString("token", null)
    }
}