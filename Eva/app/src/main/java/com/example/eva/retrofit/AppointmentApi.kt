package com.example.eva.retrofit

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AppointmentApi {
    @POST("appointments")
    suspend fun createAppointment(@Body request: AppointmentRequest): Response<ResponseBody>
}