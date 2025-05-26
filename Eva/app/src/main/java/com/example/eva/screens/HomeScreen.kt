package com.example.eva.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.eva.EvaScreens
import com.example.eva.R
import com.example.eva.retrofit.FindDoctorsViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: FindDoctorsViewModel = viewModel(
        factory = FindDoctorsViewModel.provideFactory(LocalContext.current)
    )
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = dimensionResource(R.dimen.padding_medium))
    ) {
        item { SpecializationSection(navController, viewModel) }
        item { SpecialistsSection(navController, viewModel) }
        item { BranchesSection(navController, viewModel) }
    }
}

@Composable
private fun SpecializationSection(
    navController: NavHostController,
    viewModel: FindDoctorsViewModel
) {
    val specializations by viewModel.specialities.collectAsState()

    SectionWithHeader(
        title = stringResource(R.string.specializations),
        actionText = stringResource(R.string.more),
        route = EvaScreens.SpecializationsList.route,
        navController = navController
    ) {
        Column {
            specializations.take(3).forEach { specialization ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = specialization.name,
                            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_medium))
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate(EvaScreens.FindDoctors.withSpecialization(specialization.name))
                        }
                )
            }
        }
    }
}

@Composable
private fun SpecialistsSection(navController: NavHostController, viewModel: FindDoctorsViewModel) {
    val doctors by viewModel.doctors.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    SectionWithHeader(
        title = stringResource(R.string.specialists),
        actionText = stringResource(R.string.more),
        route = EvaScreens.FindDoctors.route,
        navController = navController
    ) {
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (error?.contains("resolve host") == true) {
                            stringResource(R.string.no_internet_connection)
                        } else {
                            error ?: stringResource(R.string.unknown_error)
                        },
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = { viewModel.retry() }) {
                        Text(stringResource(R.string.refresh))
                    }
                }
            }
            doctors.isEmpty() -> {
                Text(stringResource(R.string.no_data_about_specialists))
            }
            else -> {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = dimensionResource(R.dimen.padding_medium)),
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
                ) {
                    items(doctors.take(3)) { doctor ->
                        SpecialistCard(name = doctor.fullName)
                    }
                }
            }
        }
    }
}

@Composable
private fun BranchesSection(navController: NavHostController, viewModel: FindDoctorsViewModel) {
    val branches by viewModel.branches.collectAsState()

    SectionWithHeader(
        title = stringResource(R.string.branches),
        actionText = stringResource(R.string.more),
        route = EvaScreens.BranchesList.route,
        navController = navController
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = dimensionResource(R.dimen.padding_medium)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {
            items(branches.take(4)) { branch ->
                BranchCard(name = branch.name, address = branch.address)
            }
        }
    }
}

@Composable
private fun SectionWithHeader(
    title: String,
    actionText: String,
    route: String,
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_large))) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.padding_medium))
                .padding(bottom = dimensionResource(R.dimen.padding_small)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = actionText,
                style = MaterialTheme.typography.labelSmall,
                color = colorResource(R.color.primary_green),
                modifier = Modifier.clickable { navController.navigate(route) }
            )
        }
        content()
    }
}

@Composable
fun SpecialistCard(name: String) {
    Column(
        modifier = Modifier
            .width(130.dp)
            .padding(bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .size(130.dp),
            elevation = CardDefaults.cardElevation(4.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = colorResource(R.color.primary_green),
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun BranchCard(name: String, address: String) {
    Column(
        modifier = Modifier
            .width(130.dp)
            .padding(bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .size(130.dp),
            elevation = CardDefaults.cardElevation(4.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = colorResource(R.color.primary_green),
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth()
        )
        Text(
            text = address,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}