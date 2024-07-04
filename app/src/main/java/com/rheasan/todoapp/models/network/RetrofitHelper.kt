package com.rheasan.todoapp.models.network

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class DevName (
    val name: String
)
interface Api {
    @GET("/api")
    suspend fun getDevName() : Response<DevName>
}

object RetrofitHelper {
    private var instance: Api? = null
    fun getInstance(): Api {
        if(instance == null) {
            createRetrofit()
        }
        return instance!!
    }
    private fun createRetrofit() {
        val baseUrl = "https://mockapi-phi.vercel.app"
        instance = Retrofit.
        Builder().
        baseUrl(baseUrl).
        addConverterFactory(GsonConverterFactory.create())
            .build().create(Api::class.java)
    }
}