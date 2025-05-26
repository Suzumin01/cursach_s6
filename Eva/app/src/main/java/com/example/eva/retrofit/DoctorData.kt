package com.example.eva.retrofit

data class Doctor(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val middleName: String?,
    val specialityId: Int,
    val branchId: Int
) {
    val fullName: String
        get() = listOfNotNull(lastName, firstName, middleName).joinToString(" ")
}

data class Speciality(
    val id: Int,
    val name: String
)

data class Branch(
    val id: Int,
    val name: String
)

data class DoctorWithNames(
    val fullName: String,
    val speciality: String,
    val branch: String
)