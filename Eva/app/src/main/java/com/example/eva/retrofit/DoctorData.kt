package com.example.eva.retrofit

//data class Doctor(
//    val id: Int,
//    val firstName: String,
//    val lastName: String,
//    val middleName: String,
//    val avatarUrl: String,
//    val summary: Doctor_Summary,
//    val specialties: Array<Specialty>,
//    val consultationTypes: Array<Consultation_Type>
//)
data class Doctor(
    val id: String,
    val firstName: String,
    val lastName: String,
    val middleName: String
) {
    val fullName: String
        get() = "$lastName $firstName $middleName"
}

//data class Doctor_Summary(
//    val proRating: Float,
//    val consultationCount: Int,
//    val feedbackCount: Int
//)
//
//data class Specialty(
//    val id: Int,
//    val name: String
//)
//
//data class Consultation_Type(
//    val id: Int,
//    val type: Object,
//    val cost: Money,
//    val timeSchedules: Array<Time_interval>,
//    val isAvailable: Boolean,
//    val isDisabled: Boolean
//
//)
//
//data class Money(
//    val amount: Int,
//    val currency: String
//)
//
//data class Time_interval(
//    val beginAtUtc: String,
//    val endAtUtc: String
//)