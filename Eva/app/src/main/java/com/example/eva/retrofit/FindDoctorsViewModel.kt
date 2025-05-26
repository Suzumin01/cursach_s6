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
    private val _specialities = MutableStateFlow<List<Speciality>>(emptyList())
    private val _branches = MutableStateFlow<List<Branch>>(emptyList())
    private val _doctorsUi = MutableStateFlow<List<DoctorWithNames>>(emptyList())
    val doctorsUi: StateFlow<List<DoctorWithNames>> = _doctorsUi.asStateFlow()

    val doctors: StateFlow<List<Doctor>> = _doctors
    val isLoading: StateFlow<Boolean> = _isLoading
    val error: StateFlow<String?> = _error.asStateFlow()
    val specialities = _specialities.asStateFlow()
    val branches = _branches.asStateFlow()

    private var lastSearchQuery: String = ""

    init {
        loadHistory()
        loadReferenceDataAndDoctors()
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

                val response = ApiClient.apiService.getDoctors()

                _doctors.value = response

                val filteredDoctors = if (query.isBlank()) {
                    response
                } else {
                    response.filter {
                        it.fullName.contains(query, ignoreCase = true)
                                || getSpecialityName(it.specialityId).contains(query, ignoreCase = true)
                                || getBranchName(it.branchId).contains(query, ignoreCase = true)
                    }
                }

                val mappedDoctors = filteredDoctors.map { mapDoctorToUi(it) }
                _doctorsUi.value = mappedDoctors

            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка загрузки данных"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getSpecialityName(specialityId: Int): String {
        return specialities.value.find { it.id == specialityId }?.name ?: ""
    }

    private fun getBranchName(branchId: Int): String {
        return branches.value.find { it.id == branchId }?.name ?: ""
    }

    fun mapDoctorToUi(doctor: Doctor): DoctorWithNames {
        val specialityName = specialities.value.find { it.id == doctor.specialityId }?.name ?: "Неизвестно"
        val branch = branches.value.find { it.id == doctor.branchId }
        val branchName = branch?.name ?: "Неизвестно"
        val branchAddress = branch?.address ?: "Адрес не указан"
        return DoctorWithNames(
            id = doctor.id,
            fullName = doctor.fullName,
            speciality = specialityName,
            branch = branchName,
            branchAddress = branchAddress
        )
    }

    fun retry() {
        loadDoctors(lastSearchQuery)
    }

    private fun loadReferenceDataAndDoctors() {
        viewModelScope.launch {
            try {
                _specialities.value = ApiClient.apiService.getSpecialities()
                _branches.value = ApiClient.apiService.getBranches()
                loadDoctors("")
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка загрузки справочников"
            }
        }
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
