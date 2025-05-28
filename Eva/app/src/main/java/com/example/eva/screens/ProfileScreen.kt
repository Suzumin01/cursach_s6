package com.example.eva.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.eva.R
import com.example.eva.auth_server.AuthViewModel
import com.example.eva.auth_server.AuthState
import com.example.eva.ui.theme.ThemeViewModel

@Composable
fun ProfileScreen(
    viewModel: ThemeViewModel,
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val authState by authViewModel.authState.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {

        if (authState is AuthState.Success && authViewModel.currentLogin != null) {
            Text(
                "Привет, ${authViewModel.currentLogin}!",
                Modifier.padding(bottom = 16.dp),
                style = MaterialTheme.typography.titleLarge
            )
        }

        Button(
            onClick = { navController.navigate("profile_list") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Мой профиль")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(stringResource(R.string.dark_theme), modifier = Modifier.weight(1f))
            Switch(
                checked = isDarkTheme,
                onCheckedChange = { viewModel.toggleTheme() }
            )
        }
    }
}
