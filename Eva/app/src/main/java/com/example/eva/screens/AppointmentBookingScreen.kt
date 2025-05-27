package com.example.eva.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.eva.retrofit.AppointmentRequest
import com.example.eva.retrofit.FindDoctorsViewModel
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentBookingScreen(
    doctorId: Int,
    navController: NavHostController,
    viewModel: FindDoctorsViewModel = viewModel(
        factory = FindDoctorsViewModel.provideFactory(LocalContext.current)
    ),
    authViewModel: AuthViewModel
) {
    val doctors by viewModel.doctors.collectAsState()
    val doctor = doctors.find { it.id == doctorId } ?: return

    val doctorUi = viewModel.mapDoctorToUi(doctor)

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val calendar = remember { Calendar.getInstance() }
    var selectedDateText by remember { mutableStateOf("") }
    var selectedTimeText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("День и время записи") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SpecialistCard2(
                name = doctorUi.fullName,
                speciality = doctorUi.speciality,
                onClick = {}
            )

            val authState = authViewModel.authState.collectAsState().value
            val currentLogin = when (authState) {
                is AuthState.Success -> authViewModel.currentLogin
                else -> null
            }

            if (currentLogin != null) {
                Text(
                    text = "$currentLogin, выберите все поля ниже",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                Text(
                    text = "Вы не авторизованы",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            val isAuthorized = currentLogin != null

            Button(
                onClick = {
                    val now = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            calendar.set(year, month, day)
                            selectedDateText = String.format("%04d-%02d-%02d", year, month + 1, day)
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },
                enabled = isAuthorized
            ) {
                Text(text = if (selectedDateText.isBlank()) "Выбрать дату" else "Дата: $selectedDateText")
            }

            Button(
                onClick = {
                    val now = Calendar.getInstance()
                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            calendar.set(Calendar.HOUR_OF_DAY, hour)
                            calendar.set(Calendar.MINUTE, minute)
                            selectedTimeText = String.format("%02d:%02d", hour, minute)
                        },
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                enabled = isAuthorized
            ) {
                Text(text = if (selectedTimeText.isBlank()) "Выбрать время" else "Время: $selectedTimeText")
            }

            Button(
                onClick = {
                    if (isAuthorized && currentLogin!!.isNotBlank() &&
                        selectedDateText.isNotBlank() && selectedTimeText.isNotBlank()
                    ) {
                        coroutineScope.launch {
                            try {
                                val appointment = AppointmentRequest(
                                    userLogin = currentLogin,
                                    specialistId = doctorId,
                                    dateTime = "${selectedDateText}T${selectedTimeText}",
                                    status = "pending"
                                )
                                val response = ApiClient.appointmentApi.createAppointment(appointment)
                                if (response.isSuccessful) {
                                    val responseText = response.body()?.string() ?: "Успешно"
                                    Toast.makeText(context, responseText, Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                } else {
                                    Toast.makeText(context, "Ошибка: ${response.message()}", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isAuthorized
            ) {
                Text("Подтвердить запись")
            }
        }
    }
}

