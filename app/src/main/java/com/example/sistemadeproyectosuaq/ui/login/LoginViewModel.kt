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

    // Expresión regular para validar el correo electrónico
    private fun isEmailValid(email: String): Boolean {
        val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        return email.matches(Regex(emailPattern))
    }

    fun login(email: String, password: String) {
        // Validación de los inputs
        if (email.isBlank() || password.isBlank()) {
            uiState = LoginUiState.Error("Por favor, ingresa tu correo y contraseña.")
            return
        }

        if (!isEmailValid(email)) {
            uiState = LoginUiState.Error("Correo electrónico no válido.")
            return
        }

        if (uiState is LoginUiState.Loading) return

        viewModelScope.launch {
            uiState = LoginUiState.Loading
            try {
                val loginResponse = ApiClient.service.login(LoginRequest(email, password))

                val tokenResponse = ApiClient.service.getToken(loginResponse.userKey)

                // Guardar el token
                SessionManager.onLoginSuccess(tokenResponse.token)

                val successData = LoginSuccessData(
                    userId = loginResponse.userID,
                    role = loginResponse.role,
                    token = tokenResponse.token,
                    tempPassword = loginResponse.tempPassword
                )
                uiState = LoginUiState.Success(successData)

            } catch (e: HttpException) {
                // Tratar los errores 400 y 401 como credenciales inválidas
                val errorMessage = if (e.code() == 401 || e.code() == 400) {
                    "Credenciales inválidas"
                } else {
                    "Error de red (Código: ${e.code()})"
                }
                uiState = LoginUiState.Error(errorMessage)
            } catch (e: SerializationException) {
                Log.e("LoginViewModel", "Error de serialización", e)
                uiState = LoginUiState.Error("Error en los datos recibidos del servidor.")
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error genérico de red", e)
                uiState = LoginUiState.Error("Error de conexión. Revisa tu internet.") // 
            }
        }
    }

    fun resetState() {
        uiState = LoginUiState.Idle
    }
}
