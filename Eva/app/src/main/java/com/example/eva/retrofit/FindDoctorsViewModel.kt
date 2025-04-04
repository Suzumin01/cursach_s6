package com.example.eva.retrofit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class FindDoctorsViewModel : ViewModel() {

    private val _doctors = MutableStateFlow<List<Doctor>>(emptyList())
    private val _isLoading = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)

    val doctors: StateFlow<List<Doctor>> = _doctors
    val isLoading: StateFlow<Boolean> = _isLoading
    val error: StateFlow<String?> = _error.asStateFlow()

    private var lastSearchQuery: String = ""

    init {
        loadDoctors()
    }

    fun loadDoctors(query: String = "") {
        lastSearchQuery = query
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val response = ApiClient.apiService.getDoctors()
                _doctors.value = response
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка загрузки данных"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun retry() {
        loadDoctors(lastSearchQuery)
    }

    private fun handleError(error: Throwable) {
        _error.value = when {
            isNoInternet(error) -> "Нет подключения к интернету"
            else -> "Ошибка загрузки данных"
        }
    }

    private fun isNoInternet(error: Throwable): Boolean {
        return error is UnknownHostException || error is SocketTimeoutException
    }
}
