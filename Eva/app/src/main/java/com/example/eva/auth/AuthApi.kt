package com.example.eva.auth

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("login")
    fun login(@Body request: LoginRequest): Call<AuthResponce>

    @POST("register")
    fun register(@Body request: RegisterRequest): Call<AuthResponce>
}