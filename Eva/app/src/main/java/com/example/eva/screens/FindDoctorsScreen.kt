package com.example.eva.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.eva.R
import com.example.eva.retrofit.DoctorWithNames
import com.example.eva.retrofit.FindDoctorsViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindDoctorsScreen(
    navController: NavHostController,
    initialSpecialization: String? = null,
    viewModel: FindDoctorsViewModel = viewModel(
        factory = FindDoctorsViewModel.provideFactory(LocalContext.current)
    )
) {
    val query = rememberSaveable { mutableStateOf(initialSpecialization.orEmpty()) }
    var searchText by rememberSaveable { mutableStateOf(query.value) }
    var active by rememberSaveable { mutableStateOf(false) }
    var isSearchFinished by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val history by viewModel.searchHistory.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val doctorsWithNames by viewModel.doctorsUi.collectAsState()

    var hasInitialSearchRun by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!hasInitialSearchRun && query.value.isNotBlank()) {
            hasInitialSearchRun = true
            searchText = query.value
            active = true
            delay(1000)
            active = false
            viewModel.loadDoctors(query.value)
            viewModel.saveQueryToHistory(query.value)
        }
    }

    LaunchedEffect(searchText) {
        if (searchText.isNotEmpty()) {
            delay(2000)
            viewModel.saveQueryToHistory(searchText)
        }
    }

    LaunchedEffect(isSearchFinished) {
        if (isSearchFinished) {
            viewModel.addToHistory(searchText)
            isSearchFinished = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.specialists)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                query = searchText,
                onQueryChange = { newText ->
                    searchText = newText
                    viewModel.loadDoctors(newText)
                    //if (newText.isNotEmpty()) active = true
                },
                onSearch = {
                    keyboardController?.hide()
                    viewModel.saveQueryToHistory(searchText)
                    viewModel.loadDoctors(searchText)
                    active = false
                },
                active = active,
                onActiveChange = { active = it },
                placeholder = { Text(stringResource(R.string.search)) },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = {
                            searchText = ""
                            viewModel.loadDoctors("")
                            keyboardController?.hide()
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Очистить")
                        }
                    }
                },
                content = {
                    LazyColumn {
                        items(history) { query ->
                            Text(
                                text = query,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        searchText = query
                                        viewModel.loadDoctors(query)
                                        keyboardController?.hide()
                                    }
                                    .padding(16.dp)
                            )
                        }

                        item {
                            if (history.isNotEmpty()) {
                                Button(
                                    onClick = { viewModel.clearHistory() },
                                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                                ) {
                                    Text("Очистить историю")
                                }
                            }
                        }
                    }
                }
            )

            when {
                isLoading -> LoadingIndicator()

                error != null -> {
                    ErrorPlaceholder(
                        errorMessage = if (error!!.contains("resolve host")) {
                            stringResource(R.string.no_internet_connection)
                        } else {
                            error!!
                        },
                        onRetry = { viewModel.retry() }
                    )
                }

                doctorsWithNames.isEmpty() -> {
                    EmptyPlaceholder(searchText)
                }

                else -> {
                    DoctorsList(doctorsWithNames)
                }
            }
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun DoctorsList(doctors: List<DoctorWithNames>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(doctors) { doctor ->
            SpecialistCard2(
                name = doctor.fullName,
                speciality = doctor.speciality
            )
        }
    }
}


@Composable
fun ErrorPlaceholder(errorMessage: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(8.dp)
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(stringResource(R.string.refresh))
        }
    }
}

@Composable
fun EmptyPlaceholder(searchQuery: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (searchQuery.isBlank()) {
                stringResource(R.string.no_data_about_specialists)
            } else {
                stringResource(R.string.no_specialists_for_the_query) +
                        ' ' + searchQuery +
                        ' ' + stringResource(R.string.were_found)
            },
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SpecialistCard2(name: String, speciality: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier.size(100.dp),
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
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = speciality,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

