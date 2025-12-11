package com.example.sistemadeproyectosuaq.ui.projects

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistemadeproyectosuaq.data.network.ApiClient
import com.example.sistemadeproyectosuaq.data.network.Project
import kotlinx.coroutines.launch

sealed interface ProjectListUiState {
    object Loading : ProjectListUiState
    data class Success(val projects: List<Project>) : ProjectListUiState
    data class Error(val message: String) : ProjectListUiState
}

class ProjectListViewModel : ViewModel() {

    var uiState: ProjectListUiState by mutableStateOf(ProjectListUiState.Loading)
        private set

    init {
        fetchProjects()
    }

    fun fetchProjects() {
        viewModelScope.launch {
            uiState = ProjectListUiState.Loading
            try {
                val response = ApiClient.service.getProjects()
                uiState = ProjectListUiState.Success(response.projects)
            } catch (e: Exception) {
                uiState = ProjectListUiState.Error("Error fetching projects: ${e.message}")
            }
        }
    }
}
