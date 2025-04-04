package com.example.eva.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://67efa5f12a80b06b88952c97.mockapi.io/api/v1/"

    val apiService: TelemedicineApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TelemedicineApiService::class.java)
    }
}
