package com.example.eva.ui.theme

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ThemeViewModel(context: Context) : ViewModel() {
    private val sharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    private val _isDarkTheme = MutableStateFlow(sharedPreferences.getBoolean("is_dark_theme", false))
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
        viewModelScope.launch {
            sharedPreferences.edit().putBoolean("is_dark_theme", _isDarkTheme.value).apply()
        }
    }
}