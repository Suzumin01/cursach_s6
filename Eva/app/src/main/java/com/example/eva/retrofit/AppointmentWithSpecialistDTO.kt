package com.example.eva.retrofit

data class AppointmentWithSpecialistDTO(
    val id: Int,
    val userLogin: String,
    val dateTime: String,
    val status: String,
    val specialistId: Int,
    val specialistFullName: String,
    val speciality: String,
    val branchName: String
)