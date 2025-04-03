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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.eva.EvaScreens
import com.example.eva.R
import com.example.eva.fakeapi

@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = dimensionResource(R.dimen.padding_medium))
    ) {
        item { SpecializationSection(navController) }
        item { SpecialistsSection(navController) }
        item { BranchesSection(navController) }
    }
}

@Composable
private fun SpecializationSection(navController: NavHostController) {
    SectionWithHeader(
        title = stringResource(R.string.specializations),
        actionText = stringResource(R.string.more),
        route = EvaScreens.SpecializationsList.route,
        navController = navController
    ) {
        Column {
            fakeapi.specializations.take(3).forEach { specialization ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = specialization,
                            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_medium))
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun SpecialistsSection(navController: NavHostController) {
    SectionWithHeader(
        title = stringResource(R.string.specialists),
        actionText = stringResource(R.string.more),
        route = EvaScreens.FindDoctors.route,
        navController = navController
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = dimensionResource(R.dimen.padding_medium)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {
            items(
                items = fakeapi.specialists.take(4),
                key = { it.id }
            ) { specialist ->
                SpecialistCard(name = specialist.name)
            }
        }
    }
}

@Composable
private fun BranchesSection(navController: NavHostController) {
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
            items(fakeapi.branches.take(4)) { branch ->
                BranchCard(address = branch)
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
                style = MaterialTheme.typography.titleLarge,
                color = colorResource(R.color.text_primary)
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
            colors = CardDefaults.cardColors(
                containerColor = colorResource(R.color.background)
            )
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
private fun BranchCard(address: String) {
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
            colors = CardDefaults.cardColors(
                containerColor = colorResource(R.color.background)
            )
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
            text = address,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
        )
    }
}