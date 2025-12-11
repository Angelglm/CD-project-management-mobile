package com.example.sistemadeproyectosuaq.ui.add_project

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistemadeproyectosuaq.data.network.ApiClient
import com.example.sistemadeproyectosuaq.data.network.CreateProjectRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException

sealed interface AddProjectUiState {
    object Idle : AddProjectUiState
    object Loading : AddProjectUiState
    data class Success(val projectId: String) : AddProjectUiState
    data class Error(val message: String) : AddProjectUiState
}

class AddProjectViewModel : ViewModel() {

    var uiState: AddProjectUiState by mutableStateOf(AddProjectUiState.Idle)
        private set

    fun createProject(name: String, description: String, clientId: Int, teamLeaderId: Int, start: String, end: String) {
        if (name.isBlank() || description.isBlank() || start.isBlank() || end.isBlank()) {
            uiState = AddProjectUiState.Error("Por favor, completa todos los campos.")
            return
        }
        if (clientId <= 0 || teamLeaderId <= 0) {
            uiState = AddProjectUiState.Error("IDs inválidos: cliente y líder deben ser mayores a 0.")
            return
        }

        fun toIso8601(date: String): String {
            if (!date.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) return date
            return if (date == start) "${date}T00:00:00.000Z" else "${date}T23:59:59.000Z"
        }

        viewModelScope.launch {
            uiState = AddProjectUiState.Loading
            try {
                val iso8601Start = toIso8601(start)
                val iso8601End = toIso8601(end)
                
                if (!start.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) || !end.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                    uiState = AddProjectUiState.Error("Las fechas deben estar en formato YYYY-MM-DD (ej: 2025-12-11)")
                    return@launch
                }
                
                Log.d("AddProjectViewModel", "Converting to ISO 8601: $start -> $iso8601Start, $end -> $iso8601End")
                
                val request = CreateProjectRequest(
                    name = name,
                    description = description,
                    clientId = clientId,
                    teamLeaderId = teamLeaderId,
                    start = iso8601Start,
                    end = iso8601End
                )
                Log.d("AddProjectViewModel", "Sending request: $request")

                val response = ApiClient.service.createProject(request)
                Log.d("AddProjectViewModel", "=== RAW RESPONSE ===")
                Log.d("AddProjectViewModel", "Received response object: $response")
                Log.d("AddProjectViewModel", "projectId field = '${response.projectId}'")
                Log.d("AddProjectViewModel", "projectId isNull: ${response.projectId == null}")
                Log.d("AddProjectViewModel", "projectId isBlank: ${response.projectId?.isBlank()}")
                Log.d("AddProjectViewModel", "projectId length: ${response.projectId?.length ?: 0}")

                if (!response.projectId.isNullOrBlank()) {
                    uiState = AddProjectUiState.Success(response.projectId)
                    Log.d("AddProjectViewModel", "✓ Project created successfully with ID: ${response.projectId}")
                } else {
                    uiState = AddProjectUiState.Error(
                        "El servidor rechazó el proyecto (projectId=null). " +
                        "Verifica que clientId=$clientId y teamLeaderId=$teamLeaderId existan y sean válidos."
                    )
                    Log.e("AddProjectViewModel", "❌ Server returned null/blank projectId. Project was NOT created.")
                    Log.e("AddProjectViewModel", "Used IDs: clientId=$clientId, teamLeaderId=$teamLeaderId")
                    Log.e("AddProjectViewModel", "Try using different valid IDs from your server.")
                }

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("AddProjectViewModel", "HttpException: ${e.code()} - $errorBody", e)
                uiState = AddProjectUiState.Error("Error HTTP ${e.code()}: $errorBody")
            } catch (e: Exception) {
                Log.e("AddProjectViewModel", "Generic exception", e)
                uiState = AddProjectUiState.Error("Error al crear el proyecto: ${e.message}")
            }
        }
    }

    fun resetState() {
        uiState = AddProjectUiState.Idle
    }
}
