package com.example.eva.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.eva.R
import com.example.eva.ui.theme.ThemeViewModel

@Composable
fun ProfileScreen(viewModel: ThemeViewModel, navController: NavHostController) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Button(
            onClick = { navController.navigate("profile_list") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Мои профили")
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.dark_theme), modifier = Modifier.weight(1f))
            Switch(
                checked = isDarkTheme,
                onCheckedChange = { viewModel.toggleTheme() }
            )
        }
    }
}