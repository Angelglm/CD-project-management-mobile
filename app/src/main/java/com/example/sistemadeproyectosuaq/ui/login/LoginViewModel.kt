package com.example.sistemadeproyectosuaq.ui.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistemadeproyectosuaq.data.network.ApiClient
import com.example.sistemadeproyectosuaq.data.network.LoginRequest
import com.example.sistemadeproyectosuaq.data.network.SessionManager
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
        if (email.isBlank() || password.isBlank()) {
            uiState = LoginUiState.Error("Por favor, ingresa tu correo y contraseña.")
            return
        }

        if (uiState is LoginUiState.Loading) return

        viewModelScope.launch {
            uiState = LoginUiState.Loading
            try {
                Log.d("LoginViewModel", "Attempting login for: $email")
                val loginResponse = ApiClient.service.login(LoginRequest(email, password))
                Log.d("LoginViewModel", "Login response: $loginResponse")

                Log.d("LoginViewModel", "Attempting to get token with key: ${loginResponse.userKey}")
                val tokenResponse = ApiClient.service.getToken(loginResponse.userKey)
                Log.d("LoginViewModel", "Token response: $tokenResponse")

                SessionManager.onLoginSuccess(tokenResponse.token)
                Log.d("LoginViewModel", "Token saved successfully.")

                val successData = LoginSuccessData(
                    userId = loginResponse.userID,
                    role = loginResponse.role,
                    token = tokenResponse.token,
                    tempPassword = loginResponse.tempPassword
                )
                uiState = LoginUiState.Success(successData)
                Log.d("LoginViewModel", "Login successful. State updated.")

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("LoginViewModel", "HttpException: ${e.code()} - $errorBody", e)
                val errorMessage = if (e.code() == 401 || e.code() == 400) {
                    "Credenciales inválidas"
                } else {
                    "Error de red (Código: ${e.code()})"
                }
                uiState = LoginUiState.Error(errorMessage)
            } catch (e: SerializationException) {
                Log.e("LoginViewModel", "SerializationException", e)
                uiState = LoginUiState.Error("Error en los datos recibidos del servidor.")
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Generic network error", e)
                uiState = LoginUiState.Error("Error de conexión. Revisa tu internet.")
            }
        }
    }

    fun resetState() {
        uiState = LoginUiState.Idle
    }
}
