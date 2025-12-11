package com.example.sistemadeproyectosuaq.ui.kanban

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistemadeproyectosuaq.data.network.ApiClient
import com.example.sistemadeproyectosuaq.data.network.ApiTask
import com.example.sistemadeproyectosuaq.data.network.RemoveTaskRequest
import com.example.sistemadeproyectosuaq.data.network.UpdateTaskRequest
import kotlinx.coroutines.launch

sealed interface TaskDetailUiState {
    object Loading : TaskDetailUiState
    data class Success(val task: ApiTask) : TaskDetailUiState
    data class Error(val message: String) : TaskDetailUiState
    object TaskDeleted : TaskDetailUiState
}

class TaskDetailViewModel : ViewModel() {

    var uiState: TaskDetailUiState by mutableStateOf(TaskDetailUiState.Loading)
        private set

    fun fetchTask(projectId: String, moduleId: String, taskId: Int) {
        viewModelScope.launch {
            uiState = TaskDetailUiState.Loading
            try {
                val response = ApiClient.service.getTasks(projectId, moduleId)
                val task = response.tasks.find { it.id == taskId }
                if (task != null) {
                    uiState = TaskDetailUiState.Success(task)
                } else {
                    uiState = TaskDetailUiState.Error("Task not found.")
                }
            } catch (e: Exception) {
                uiState = TaskDetailUiState.Error("Error fetching task details: ${e.message}")
            }
        }
    }

    fun updateTaskStatus(projectId: String, moduleId: String, task: ApiTask, newStatus: String) {
        val currentState = uiState
        if (currentState is TaskDetailUiState.Success) {
            viewModelScope.launch {
                try {
                    val request = UpdateTaskRequest(
                        project_id = projectId,
                        module_id = moduleId,
                        task_id = task.id,
                        title = task.title,
                        description = task.description,
                        priority = task.priority,
                        status = newStatus,
                        user_ids = task.user_ids?.let { listOf(it) }
                    )
                    ApiClient.service.updateTask(request)

                    fetchTask(projectId, moduleId, task.id)

                } catch (e: Exception) {
                    uiState = TaskDetailUiState.Error("Error updating task: ${e.message}")
                }
            }
        }
    }

    fun deleteTask(projectId: String, moduleId: String, taskId: Int) {
        viewModelScope.launch {
            try {
                val request = RemoveTaskRequest(
                    project_id = projectId,
                    module_id = moduleId,
                    task_id = taskId
                )
                ApiClient.service.removeTask(request)
                uiState = TaskDetailUiState.TaskDeleted
            } catch (e: Exception) {
                uiState = TaskDetailUiState.Error("Error deleting task: ${e.message}")
            }
        }
    }
}
