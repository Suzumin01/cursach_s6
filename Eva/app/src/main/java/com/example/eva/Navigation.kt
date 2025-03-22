package com.example.eva

sealed class EvaScreens(val route: String) {
    object Home : EvaScreens("home")
    object Message : EvaScreens("message")
    object Calendar : EvaScreens("calendar")
    object Profile : EvaScreens("profile")
    object FindDoctors : EvaScreens("find_doctors")
    object SpecializationsList : EvaScreens("specializations_list")
    object BranchesList : EvaScreens("branches_list")
}