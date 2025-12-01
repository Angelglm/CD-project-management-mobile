package com.example.sistemadeproyectosuaq.ui.kanban

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistemadeproyectosuaq.data.network.ApiClient
import com.example.sistemadeproyectosuaq.data.network.ApiTask
import com.example.sistemadeproyectosuaq.data.network.Project
import kotlinx.coroutines.launch

// Data class to hold all the data for the Kanban screen
data class KanbanScreenData(val project: Project, val tasks: List<ApiTask>)

// Sealed interface for the UI state
sealed interface KanbanUiState {
    object Loading : KanbanUiState
    data class Success(val data: KanbanScreenData) : KanbanUiState
    data class Error(val message: String) : KanbanUiState
}

class KanbanViewModel : ViewModel() {

    var uiState: KanbanUiState by mutableStateOf(KanbanUiState.Loading)
        private set

    init {
        fetchProjectData()
    }

    fun fetchProjectData() {
        viewModelScope.launch {
            uiState = KanbanUiState.Loading
            try {
                // Step 1: Fetch projects
                val projectsResponse = ApiClient.service.getProjects()
                val firstProject = projectsResponse.projects.firstOrNull()

                if (firstProject == null) {
                    uiState = KanbanUiState.Error("No se encontraron proyectos.")
                    return@launch
                }

                // Step 2: Fetch tasks for the first project (assuming a moduleId of "1" for now)
                val tasksResponse = ApiClient.service.getTasks(firstProject.id, "1")

                // Step 3: Set success state
                uiState = KanbanUiState.Success(KanbanScreenData(firstProject, tasksResponse.tasks))

            } catch (e: Exception) {
                uiState = KanbanUiState.Error("Error al cargar los datos del proyecto: ${e.message}")
            }
        }
    }
}