package com.example.sistemadeproyectosuaq.ui.project_management

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
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
        loadProjects()
    }

    fun loadProjects() {
        viewModelScope.launch {
            uiState = ProjectListUiState.Loading
            try {
                val response = ApiClient.service.getProjects()
                uiState = ProjectListUiState.Success(response.projects)
            } catch (e: Exception) {
                uiState = ProjectListUiState.Error("Error al cargar los proyectos: ${e.message}")
            }
        }
    }
}

@Composable
fun ProjectManagementScreen(
    onProjectClick: (String) -> Unit,
    viewModel: ProjectListViewModel = viewModel()
) {
    val uiState = viewModel.uiState

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (uiState) {
            is ProjectListUiState.Loading -> {
                CircularProgressIndicator()
            }
            is ProjectListUiState.Error -> {
                Text(uiState.message, color = Color.Red)
            }
            is ProjectListUiState.Success -> {
                if (uiState.projects.isEmpty()) {
                    Text("No hay proyectos para mostrar.")
                } else {
                    ProjectList(projects = uiState.projects, onProjectClick = onProjectClick)
                }
            }
        }
    }
}

@Composable
fun ProjectList(projects: List<Project>, onProjectClick: (String) -> Unit) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(projects) {
            ProjectListItem(project = it, onClick = { onProjectClick(it.id) })
        }
    }
}

@Composable
fun ProjectListItem(project: Project, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(project.name, fontWeight = FontWeight.Bold)
            Text(project.description)
        }
    }
}
