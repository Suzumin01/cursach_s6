package com.example.eva.retrofit

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FindDoctorsViewModel(
    private val historyManager: SearchHistoryManager
) : ViewModel() {
    private var searchJob: Job? = null
    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()
    private val _doctors = MutableStateFlow<List<Doctor>>(emptyList())
    private val _isLoading = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)

    val doctors: StateFlow<List<Doctor>> = _doctors
    val isLoading: StateFlow<Boolean> = _isLoading
    val error: StateFlow<String?> = _error.asStateFlow()

    private var lastSearchQuery: String = ""

    init {
        loadDoctors("")
        loadHistory()
    }

    private fun loadHistory() {
        _searchHistory.value = historyManager.getHistory()
    }

    fun saveQueryToHistory(query: String) {
        if (query.isNotBlank()) {
            historyManager.saveQuery(query)
            _searchHistory.value = historyManager.getHistory()
        }
    }

    fun addToHistory(query: String) {
        if (query.isNotBlank()) {
            historyManager.saveQuery(query)
            _searchHistory.value = historyManager.getHistory()
        }
    }

    fun clearHistory() {
        historyManager.clearHistory()
        _searchHistory.value = emptyList()
        loadDoctors("")
    }

    fun loadDoctors(query: String = "") {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val response = ApiClient.apiService.getDoctors(query)
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



    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FindDoctorsViewModel(
                        historyManager = SearchHistoryManager(context)
                    ) as T
                }
            }
        }
    }
}
