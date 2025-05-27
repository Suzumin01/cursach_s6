package com.example.eva.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.eva.retrofit.ApiClient
import com.example.eva.retrofit.AppointmentWithSpecialistDTO
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDetailsScreen(
    appointmentId: Int,
    navController: NavHostController
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var appointment by remember { mutableStateOf<AppointmentWithSpecialistDTO?>(null) }

    LaunchedEffect(appointmentId) {
        try {
            val response = ApiClient.appointmentApi.getAppointmentById(appointmentId)
            if (response.isSuccessful) {
                appointment = response.body()
            } else {
                Toast.makeText(context, "Ошибка: ${response.message()}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Детали приема") })
        }
    ) { padding ->
        appointment?.let {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text("Врач: ${it.specialistFullName}", style = MaterialTheme.typography.titleMedium)
                Text("Специальность: ${it.speciality}")
                Text("Дата: ${it.dateTime}")
                Text("Филиал: ${it.branchName}")
                Text("Статус: ${it.status}")

                Spacer(modifier = Modifier.height(24.dp))

                if (it.status == "pending") {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    val response = ApiClient.appointmentApi.updateStatus(it.id, "cancelled")
                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "Прием отменен", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    } else {
                                        Toast.makeText(context, "Ошибка: ${response.message()}", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Ошибка: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    ) {
                        Text("Отменить прием")
                    }
                }
            }
        } ?: run {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}