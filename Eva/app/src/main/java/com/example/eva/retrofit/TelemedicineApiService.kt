package com.example.eva.retrofit

import retrofit2.http.GET
import retrofit2.http.Query

interface TelemedicineApiService {
    @GET("doctor")
    suspend fun getDoctors(
        @Query("search") query: String = ""
    ): List<Doctor>
}
