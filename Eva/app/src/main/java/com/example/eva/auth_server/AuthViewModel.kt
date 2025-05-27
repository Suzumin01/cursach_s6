package com.example.eva.auth_server

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eva.retrofit.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    var currentLogin: String? = null
        private set

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val request = LoginRequest(username, password)
                val response = ApiClient.authApi.login(request)
                if (response.isSuccessful) {
                    val token = response.body()?.token
                    if (!token.isNullOrEmpty()) {
                        _authState.value = AuthState.Success(token)
                        currentLogin = username
                    } else {
                        _authState.value = AuthState.Error("Пустой токен")
                    }
                } else {
                    _authState.value = AuthState.Error("Ошибка авторизации")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Ошибка: ${e.message}")
            }
        }
    }

    fun register(login: String, password: String, email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val request = RegisterRequest(login, password, email)
                val response = ApiClient.authApi.register(request)
                handleAuthResponse(response)
                currentLogin = login // 💾 сохраняем логин при успешной регистрации
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Ошибка сети: ${e.message}")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    private fun handleAuthResponse(response: Response<AuthResponse>) {
        if (response.isSuccessful) {
            val token = response.body()?.token
            if (!token.isNullOrEmpty()) {
                _authState.value = AuthState.Success(token)
            } else {
                _authState.value = AuthState.Error("Пустой токен")
            }
        } else {
            _authState.value = AuthState.Error("Ошибка: ${response.code()} ${response.message()}")
        }
    }
}

// Состояние авторизации
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val token: String) : AuthState()
    data class Error(val message: String) : AuthState()
}
