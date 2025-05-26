package com.example.eva.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.eva.EvaScreens
import com.example.eva.retrofit.FindDoctorsViewModel

@Composable
fun SpecializationsListScreen(
    navController: NavHostController,
    viewModel: FindDoctorsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = FindDoctorsViewModel.provideFactory(LocalContext.current)
    )
) {
    val specializations by viewModel.specialities.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(specializations) { spec ->
            ListItem(
                headlineContent = { Text(spec.name) },
                modifier = Modifier.clickable {
                    navController.navigate(EvaScreens.FindDoctors.withSpecialization(spec.name))
                }
            )
        }
    }
}