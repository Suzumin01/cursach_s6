package com.example.eva.retrofit

import retrofit2.http.GET
import retrofit2.http.Query


interface TelemedicineApiService {
    @GET("specialists")
    suspend fun getDoctors(
        @Query("search") query: String = ""
    ): List<Doctor>

    @GET("specialities")
    suspend fun getSpecialities(): List<Speciality>

    @GET("branches")
    suspend fun getBranches(): List<Branch>
}