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

    fun fetchTasksForProject(project: Project) {
        viewModelScope.launch {
            uiState = KanbanUiState.Loading
            try {
                // Fetch tasks for the given project (assuming a moduleId of "1" for now)
                val tasksResponse = ApiClient.service.getTasks(project.id, "1")

                // Set success state
                uiState = KanbanUiState.Success(KanbanScreenData(project, tasksResponse.tasks))

            } catch (e: Exception) {
                uiState = KanbanUiState.Error("Error al cargar las tareas del proyecto: ${e.message}")
            }
        }
    }
}
