package com.rheasan.todoapp.models

import android.content.Context
import android.widget.TextView
import com.rheasan.todoapp.R
import com.rheasan.todoapp.network.RetrofitHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun updateDevName(headerTextView: TextView, context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
        val api = RetrofitHelper.getInstance()
        try {
            val res = api.getDevName().body()
            val devName = res?.name!!
            "$devName's TODOs".also { headerTextView.text = it }
        }
        catch (e: Exception) {
            println("Failed to fetch api data: $e")
            headerTextView.text = context.getString(R.string.offline_header)
        }
    }
}