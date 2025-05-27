package com.example.eva.retrofit

data class AppointmentRequest(
    val userLogin: String,
    val specialistId: Int,
    val dateTime: String,
    val status: String
)