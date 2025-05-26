package com.example.eva.auth_server

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("login") val login: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("login") val login: String,
    @SerializedName("password") val password: String,
    @SerializedName("email") val email: String
)

data class AuthResponse(
    @SerializedName("token") val token: String
)