package com.example.sistemadeproyectosuaq.ui.add_module

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistemadeproyectosuaq.data.network.ApiClient
import com.example.sistemadeproyectosuaq.data.network.CreateModuleRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException

sealed interface AddModuleUiState {
    object Idle : AddModuleUiState
    object Loading : AddModuleUiState
    data class Success(val message: String) : AddModuleUiState
    data class Error(val message: String) : AddModuleUiState
}

class AddModuleViewModel : ViewModel() {

    var uiState: AddModuleUiState by mutableStateOf(AddModuleUiState.Idle)
        private set

    fun createModule(
        projectId: String,
        title: String,
        description: String,
        priority: String,
        status: String
    ) {
        if (title.isBlank() || description.isBlank()) {
            uiState = AddModuleUiState.Error("El título y descripción son requeridos.")
            return
        }

        viewModelScope.launch {
            uiState = AddModuleUiState.Loading
            try {
                val request = CreateModuleRequest(
                    projectId = projectId,
                    title = title,
                    description = description,
                    priority = priority,
                    status = status
                )
                Log.d("AddModuleViewModel", "Creating module: $request")
                
                val response = ApiClient.service.createModule(request)
                Log.d("AddModuleViewModel", "Module created: ${response.message}")
                uiState = AddModuleUiState.Success(response.message)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("AddModuleViewModel", "HttpException: ${e.code()} - $errorBody", e)
                uiState = AddModuleUiState.Error("Error HTTP ${e.code()}: $errorBody")
            } catch (e: Exception) {
                Log.e("AddModuleViewModel", "Exception creating module", e)
                uiState = AddModuleUiState.Error("Error al crear módulo: ${e.message}")
            }
        }
    }

    fun resetState() {
        uiState = AddModuleUiState.Idle
    }
}
