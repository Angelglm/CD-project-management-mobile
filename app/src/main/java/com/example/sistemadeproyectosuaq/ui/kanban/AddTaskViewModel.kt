package com.example.sistemadeproyectosuaq.ui.kanban

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistemadeproyectosuaq.data.network.ApiClient
import com.example.sistemadeproyectosuaq.data.network.CreateTaskRequest
import com.example.sistemadeproyectosuaq.data.network.Module
import kotlinx.coroutines.launch
import retrofit2.HttpException

sealed interface AddTaskUiState {
    object Idle : AddTaskUiState
    object Loading : AddTaskUiState
    data class Success(val message: String) : AddTaskUiState
    data class Error(val message: String) : AddTaskUiState
}

class AddTaskViewModel : ViewModel() {

    var uiState: AddTaskUiState by mutableStateOf(AddTaskUiState.Idle)
        private set

    var modules: List<Module> by mutableStateOf(emptyList())
        private set

    var isModuleLoading: Boolean by mutableStateOf(false)
        private set

    var moduleError: String? by mutableStateOf(null)
        private set

    fun loadModules(projectId: String) {
        if (projectId.isBlank()) {
            modules = emptyList()
            return
        }

        viewModelScope.launch {
            isModuleLoading = true
            moduleError = null
            try {
                val response = ApiClient.service.getModules(projectId)
                modules = response.modules
            } catch (e: Exception) {
                moduleError = "Error al cargar modulos: ${e.message}"
            } finally {
                isModuleLoading = false
            }
        }
    }

    fun createTask(
        projectId: String,
        moduleId: String,
        title: String,
        description: String,
        priority: String,
        status: String,
        userIds: List<Int>?
    ) {
        if (title.isBlank()) {
            uiState = AddTaskUiState.Error("Ingresa un titulo")
            return
        }

        if (moduleId.isBlank()) {
            uiState = AddTaskUiState.Error("Selecciona un modulo")
            return
        }

        if (priority.isBlank()) {
            uiState = AddTaskUiState.Error("Ingresa la prioridad")
            return
        }

        if (status.isBlank()) {
            uiState = AddTaskUiState.Error("Ingresa el estado")
            return
        }

        viewModelScope.launch {
            uiState = AddTaskUiState.Loading
            try {
                val request = CreateTaskRequest(
                    project_id = projectId,
                    module_id = moduleId,
                    title = title,
                    description = description,
                    priority = priority,
                    status = status,
                    user_ids = userIds
                )
                val response = ApiClient.service.createTask(request)
                uiState = AddTaskUiState.Success(response.message)
            } catch (e: HttpException) {
                uiState = AddTaskUiState.Error("Error HTTP ${e.code()}")
            } catch (e: Exception) {
                uiState = AddTaskUiState.Error("Error al crear la tarea: ${e.message}")
            }
        }
    }

    fun resetState() {
        uiState = AddTaskUiState.Idle
    }
}
