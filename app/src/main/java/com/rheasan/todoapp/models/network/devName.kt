package com.rheasan.todoapp.models.network

import android.util.Log

suspend fun getDevName(): String? {
    val api = RetrofitHelper.getInstance()
    try {
        val res = api.getDevName().body()
        val devName = res?.name!!
        return devName
    }
    catch (e: Exception) {
        Log.e("Retrofit", "Failed to fetch api data: $e")
    }

    return null
}