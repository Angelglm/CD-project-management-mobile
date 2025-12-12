package com.example.sistemadeproyectosuaq.ui.profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistemadeproyectosuaq.data.network.ApiClient
import com.example.sistemadeproyectosuaq.data.network.CreateUserRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException

sealed interface CreateUserUiState {
    object Idle : CreateUserUiState
    object Loading : CreateUserUiState
    data class Success(val message: String, val tempPassword: String) : CreateUserUiState
    data class Error(val message: String) : CreateUserUiState
}

class UserAdminViewModel : ViewModel() {

    var uiState: CreateUserUiState by mutableStateOf(CreateUserUiState.Idle)
        private set

    fun createUser(name: String, email: String, phone: String, role: String) {
        if (name.isBlank() || email.isBlank() || phone.isBlank() || role.isBlank()) {
            uiState = CreateUserUiState.Error("Por favor, completa todos los campos.")
            return
        }

        viewModelScope.launch {
            uiState = CreateUserUiState.Loading
            try {
                val request = CreateUserRequest(
                    name = name,
                    email = email,
                    phone = phone,
                    role = role
                )
                Log.d("UserAdminViewModel", "Creating user with request: $request")
                Log.d("UserAdminViewModel", "Role value: '$role'")
                
                val response = ApiClient.service.createUser(request)
                Log.d("UserAdminViewModel", "User created successfully: $response")
                uiState = CreateUserUiState.Success(response.message, response.tempPassword)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("UserAdminViewModel", "HttpException: ${e.code()} - $errorBody", e)
                uiState = CreateUserUiState.Error("Error HTTP ${e.code()}: $errorBody")
            } catch (e: Exception) {
                Log.e("UserAdminViewModel", "Exception creating user", e)
                uiState = CreateUserUiState.Error("Error al crear usuario: ${e.message}")
            }
        }
    }

    fun resetState() {
        uiState = CreateUserUiState.Idle
    }
}
