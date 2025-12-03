package com.example.sistemadeproyectosuaq.ui.add_project

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistemadeproyectosuaq.data.network.ApiClient
import com.example.sistemadeproyectosuaq.data.network.CreateProjectRequest
import kotlinx.coroutines.launch

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
        // Basic validation
        if (name.isBlank() || description.isBlank() || start.isBlank() || end.isBlank()) {
            uiState = AddProjectUiState.Error("Por favor, completa todos los campos.")
            return
        }

        viewModelScope.launch {
            uiState = AddProjectUiState.Loading
            try {
                val request = CreateProjectRequest(name, description, clientId, teamLeaderId, start, end)
                val response = ApiClient.service.createProject(request)
                uiState = AddProjectUiState.Success(response.projectId)
            } catch (e: Exception) {
                uiState = AddProjectUiState.Error("Error al crear el proyecto: ${e.message}")
            }
        }
    }

    fun resetState() {
        uiState = AddProjectUiState.Idle
    }
}