package com.example.eva.auth_server

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
}
