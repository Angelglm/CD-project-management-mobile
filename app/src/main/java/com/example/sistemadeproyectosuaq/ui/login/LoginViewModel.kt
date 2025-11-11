package com.example.sistemadeproyectosuaq.ui.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistemadeproyectosuaq.data.network.ApiClient
import com.example.sistemadeproyectosuaq.data.network.LoginRequest
import com.example.sistemadeproyectosuaq.data.network.TokenResponse
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import retrofit2.HttpException

data class LoginSuccessData(val userId: Int, val role: String, val token: String, val tempPassword: Boolean)

sealed interface LoginUiState {
    object Idle : LoginUiState
    object Loading : LoginUiState
    data class Success(val data: LoginSuccessData) : LoginUiState
    data class Error(val message: String) : LoginUiState
}

class LoginViewModel : ViewModel() {
    var uiState: LoginUiState by mutableStateOf(LoginUiState.Idle)
        private set

    fun login(email: String, password: String) {
        // Validate inputs before making the network call
        if (email.isBlank() || password.isBlank()) {
            uiState = LoginUiState.Error("Por favor, ingresa tu correo y contraseña.")
            return
        }

        if (uiState is LoginUiState.Loading) return

        viewModelScope.launch {
            uiState = LoginUiState.Loading
            try {
                val loginResponse = ApiClient.service.login(LoginRequest(email, password))

                val tokenResponse = ApiClient.service.getToken(loginResponse.userKey)

                val successData = LoginSuccessData(
                    userId = loginResponse.userID,
                    role = loginResponse.role,
                    token = tokenResponse.token,
                    tempPassword = loginResponse.tempPassword
                )
                uiState = LoginUiState.Success(successData)

            } catch (e: HttpException) {
                // Treat both 400 and 401 as invalid credentials in the login context
                val errorMessage = if (e.code() == 401 || e.code() == 400) {
                    "Credenciales inválidas"
                } else {
                    "Error de red (Código: ${e.code()})"
                }
                uiState = LoginUiState.Error(errorMessage)
            } catch (e: SerializationException) {
                Log.e("LoginViewModel", "Serialization error", e)
                uiState = LoginUiState.Error("Error en los datos recibidos del servidor.")
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Generic network error", e)
                uiState = LoginUiState.Error("Error de conexión. Revisa tu internet.") // Reverted to user-friendly message
            }
        }
    }

    fun resetState() {
        uiState = LoginUiState.Idle
    }
}