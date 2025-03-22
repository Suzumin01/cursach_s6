package com.example.eva

sealed class EvaScreens(val route: String) {
    object Home : EvaScreens("home")
    object Message : EvaScreens("message")
    object Calendar : EvaScreens("calendar")
    object Profile : EvaScreens("profile")
}