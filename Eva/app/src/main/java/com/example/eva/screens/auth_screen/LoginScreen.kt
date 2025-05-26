package com.example.eva.screens.auth_screen

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eva.EvaScreens
import com.example.eva.auth_server.AuthState
import com.example.eva.auth_server.AuthViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(viewModel.authState.value) {
        when (val state = viewModel.authState.value) {
            is AuthState.Loading -> {
                // ProgressBar
            }
            is AuthState.Success -> {
                Toast.makeText(context, "Успешный вход!", Toast.LENGTH_SHORT).show()
//                navController.navigate(EvaScreens.Profile.route) {
//                    popUpTo(EvaScreens.Home.route) { inclusive = true }
//                }
                viewModel.resetState()
            }
            is AuthState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = login,
            onValueChange = { login = it },
            label = { Text("Логин") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (login.isNotEmpty() && password.isNotEmpty()) {
                    viewModel.login(login, password)
                    navController.navigate(EvaScreens.Profile.route) {
                        popUpTo(EvaScreens.Home.route) { inclusive = true }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Войти")
        }
    }
}