package com.example.eva.retrofit

data class Doctor(
    val id: String,
    val firstName: String,
    val lastName: String,
    val middleName: String
) {
    val fullName: String
        get() = "$lastName $firstName $middleName"
}
