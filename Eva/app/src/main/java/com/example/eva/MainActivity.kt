package com.example.eva

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.eva.auth_server.AuthViewModel
import com.example.eva.retrofit.DoctorWithNames
import com.example.eva.screens.AppointmentBookingScreen
import com.example.eva.screens.AppointmentDetailsScreen
import com.example.eva.screens.BranchesListScreen
import com.example.eva.screens.CalendarScreen
import com.example.eva.screens.DoctorDetailsScreen
import com.example.eva.screens.FindDoctorsScreen
import com.example.eva.screens.HomeScreen
import com.example.eva.screens.MessageScreen
import com.example.eva.screens.ProfileListScreen
import com.example.eva.screens.ProfileScreen
import com.example.eva.screens.SpecializationsListScreen
import com.example.eva.screens.auth_screen.LoginScreen
import com.example.eva.screens.auth_screen.RegisterScreen
import com.example.eva.ui.theme.CustomIcons
import com.example.eva.ui.theme.EvaTheme
import com.example.eva.ui.theme.ThemeViewModel

class MainActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by lazy { ThemeViewModel(applicationContext) }
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
            EvaTheme(darkTheme = isDarkTheme) {
                MainScreen(themeViewModel, authViewModel)
            }
        }
    }
}

@Composable
fun MainScreen(themeViewModel: ThemeViewModel, authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = CustomIcons.Home),
                            contentDescription = "Home",
                            tint = if (currentRoute == EvaScreens.Home.route)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    selected = currentRoute == EvaScreens.Home.route,
                    onClick = {
                        navController.navigate(EvaScreens.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = CustomIcons.Message),
                            contentDescription = "Messages",
                            tint = if (currentRoute == EvaScreens.Message.route)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    selected = currentRoute == EvaScreens.Message.route,
                    onClick = {
                        navController.navigate(EvaScreens.Message.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = CustomIcons.Calendar),
                            contentDescription = "Calendar",
                            tint = if (currentRoute == EvaScreens.Calendar.route)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    selected = currentRoute == EvaScreens.Calendar.route,
                    onClick = {
                        navController.navigate(EvaScreens.Calendar.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = CustomIcons.Profile),
                            contentDescription = "Profile",
                            tint = if (currentRoute == EvaScreens.Profile.route)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    selected = currentRoute == EvaScreens.Profile.route,
                    onClick = {
                        navController.navigate(EvaScreens.Profile.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = EvaScreens.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(EvaScreens.Home.route) { HomeScreen(navController) }
            composable(EvaScreens.Message.route) { MessageScreen() }
            composable(EvaScreens.Calendar.route) { CalendarScreen(navController, authViewModel) }
            composable(EvaScreens.Profile.route) {
                ProfileScreen(themeViewModel, navController, authViewModel)
            }
            composable(EvaScreens.ProfileList.route) { ProfileListScreen(navController, authViewModel) }
            composable(EvaScreens.Login.route) { LoginScreen(authViewModel, navController) }
            composable(EvaScreens.Register.route) { RegisterScreen(authViewModel, navController) }
            composable(
                route = "find_doctors?specialization={specialization}",
                arguments = listOf(
                    navArgument("specialization") {
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val specialization = backStackEntry.arguments?.getString("specialization")
                FindDoctorsScreen(navController = navController, initialSpecialization = specialization)
            }
            composable(EvaScreens.SpecializationsList.route) { SpecializationsListScreen(navController) }
            composable(EvaScreens.BranchesList.route) { BranchesListScreen(navController) }
            composable("doctorDetails") {
                DoctorDetailsScreen(navController)
            }
            composable(
                route = EvaScreens.AppointmentBooking.route,
                arguments = listOf(navArgument("doctorId") { type = NavType.IntType })
            ) {
                val doctorId = it.arguments?.getInt("doctorId") ?: return@composable
                AppointmentBookingScreen(
                    doctorId = doctorId,
                    navController = navController,
                    authViewModel = authViewModel
                )
            }
            composable("appointment_details/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
                if (id != null) {
                    AppointmentDetailsScreen(appointmentId = id, navController = navController)
                } else {
                    Text("Ошибка: некорректный ID")
                }
            }

        }
    }
}