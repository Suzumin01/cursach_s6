package com.example.eva.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.eva.auth_server.AuthState
import com.example.eva.auth_server.AuthViewModel
import com.example.eva.retrofit.ApiClient
import com.example.eva.retrofit.AppointmentWithSpecialistDTO
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var appointments by remember { mutableStateOf<List<AppointmentWithSpecialistDTO>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            val login = authViewModel.currentLogin ?: return@LaunchedEffect
            isLoading = true
            try {
                val response = ApiClient.appointmentApi.getDetailedAppointments(login)
                if (response.isSuccessful) {
                    appointments = response.body() ?: emptyList()
                } else {
                    Toast.makeText(context, "Ошибка: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Ваши приемы") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            if (authState !is AuthState.Success) {
                Column {
                    Text("Вы не авторизованы", style = MaterialTheme.typography.bodyLarge)
                }
                return@Scaffold
            }

            LazyColumn {
                item {
                    Text("Предстоящие приемы", style = MaterialTheme.typography.titleMedium)
                }

                items(appointments.filter { it.status == "pending" }) { appointment ->
                    AppointmentItem(appointment) {
                        navController.navigate("appointment_details/${appointment.id}")
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Завершенные приемы", style = MaterialTheme.typography.titleMedium)
                }

                items(appointments.filter { it.status in listOf("completed", "cancelled") }) { appointment ->
                    AppointmentItem(appointment) {
                        navController.navigate("appointment_details/${appointment.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentItem(appointment: AppointmentWithSpecialistDTO, onClick: () -> Unit) {
    val (datePart, timePart) = appointment.dateTime.split("T")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = appointment.specialistFullName, style = MaterialTheme.typography.bodyLarge)
            Text(text = appointment.speciality)
            Text(text = "Дата: $datePart")
            Text(text = "Время: $timePart")
            Text(text = "Филиал: ${appointment.branchName}")
        }
    }
}

