package com.example.eva.retrofit

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AppointmentApi {
    @POST("appointments")
    suspend fun createAppointment(@Body request: AppointmentRequest): Response<ResponseBody>

    @GET("appointments/{login}/detailed")
    suspend fun getDetailedAppointments(
        @Path("login") login: String
    ): Response<List<AppointmentWithSpecialistDTO>>

    @GET("appointments/id/{id}")
    suspend fun getAppointmentById(
        @Path("id") id: Int
    ): Response<AppointmentWithSpecialistDTO>

    @FormUrlEncoded
    @PUT("appointments/{id}/status")
    suspend fun updateStatus(
        @Path("id") id: Int,
        @Field("status") status: String
    ): Response<Unit>
}

