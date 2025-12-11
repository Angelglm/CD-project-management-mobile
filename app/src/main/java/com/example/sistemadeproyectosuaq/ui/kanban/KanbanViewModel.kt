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


data class KanbanScreenData(val project: Project, val tasks: List<ApiTask>)

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
                val tasksResponse = ApiClient.service.getTasks(project.id, "1")

                uiState = KanbanUiState.Success(KanbanScreenData(project, tasksResponse.tasks))

            } catch (e: Exception) {
                uiState = KanbanUiState.Error("Error al cargar las tareas del proyecto: ${e.message}")
            }
        }
    }
}
