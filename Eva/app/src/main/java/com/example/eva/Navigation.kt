package com.example.eva

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class EvaScreens(val route: String) {
    object Home : EvaScreens("home")
    object Message : EvaScreens("message")
    object Calendar : EvaScreens("calendar")
    object Profile : EvaScreens("profile")
    object ProfileList: EvaScreens("profile_list")
    object Login: EvaScreens("login")
    object Register: EvaScreens("register")
    object FindDoctors : EvaScreens("find_doctors") {
        fun withSpecialization(specialization: String): String {
            val encoded = URLEncoder.encode(specialization, StandardCharsets.UTF_8.toString())
            return "find_doctors?specialization=$encoded"
        }
    }
    object SpecializationsList : EvaScreens("specializations_list")
    object BranchesList : EvaScreens("branches_list")
    object DoctorDetails : EvaScreens("doctor_details")
    object AppointmentBooking : EvaScreens("appointmentBooking/{doctorId}") {
        fun withDoctorId(id: Int) = "appointmentBooking/$id"
    }
}