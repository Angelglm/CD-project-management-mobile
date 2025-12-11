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

        viewModelScope.launch {
            uiState = AddProjectUiState.Loading
            try {
                val request = CreateProjectRequest(name, description, clientId, teamLeaderId, start, end)
                Log.d("AddProjectViewModel", "Sending request: $request")

                val response = ApiClient.service.createProject(request)
                Log.d("AddProjectViewModel", "Received response: $response")

                if (!response.projectId.isNullOrBlank()) {
                    uiState = AddProjectUiState.Success(response.projectId)
                    Log.d("AddProjectViewModel", "Project created successfully with ID: ${response.projectId}")
                } else {
                    uiState = AddProjectUiState.Error("El servidor no devolvió una respuesta válida (projectID nulo).")
                    Log.w("AddProjectViewModel", "Server response was successful but projectId was null or blank.")
                }

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("AddProjectViewModel", "HttpException: ${e.code()} - $errorBody", e)
                uiState = AddProjectUiState.Error("Error al crear el proyecto (Código: ${e.code()})")
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
