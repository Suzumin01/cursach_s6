package com.example.eva.retrofit

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

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
    val name: String,
    val address: String
)

@Parcelize
data class DoctorWithNames(
    val id: Int,
    val fullName: String,
    val speciality: String,
    val branch: String,
    val branchAddress: String
) : Parcelable