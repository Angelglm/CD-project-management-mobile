package com.example.sistemadeproyectosuaq.ui.team

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistemadeproyectosuaq.data.network.ApiClient
import com.example.sistemadeproyectosuaq.data.network.CreateTeamRequest
import com.example.sistemadeproyectosuaq.data.network.Project
import com.example.sistemadeproyectosuaq.data.network.RemoveTeamRequest
import com.example.sistemadeproyectosuaq.data.network.Team
import com.example.sistemadeproyectosuaq.data.network.TeamsResponse
import com.example.sistemadeproyectosuaq.data.network.UpdateTeamRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException

class TeamManagementViewModel : ViewModel() {
    var projects by mutableStateOf<List<Project>>(emptyList())
        private set
    var selectedProject by mutableStateOf<Project?>(null)
        private set
    var teams by mutableStateOf<List<Team>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var message by mutableStateOf<String?>(null)
        private set

    fun loadProjects() {
        viewModelScope.launch {
            isLoading = true
            message = null
            try {
                val response = ApiClient.service.getProjects()
                projects = response.projects
            } catch (e: Exception) {
                message = friendlyError(e)
            } finally {
                isLoading = false
            }
        }
    }

    fun selectProject(project: Project) {
        selectedProject = project
        loadTeams(project.id)
    }

    fun loadTeams(projectId: String) {
        viewModelScope.launch {
            isLoading = true
            message = null
            try {
                val response: TeamsResponse = ApiClient.service.getTeams(projectId)
                teams = response.teams
            } catch (e: Exception) {
                message = friendlyError(e)
            } finally {
                isLoading = false
            }
        }
    }

    fun createTeam(name: String, description: String) {
        val project = selectedProject ?: return
        if (name.isBlank() || description.isBlank()) {
            message = "Nombre y descripción son requeridos"
            return
        }
        viewModelScope.launch {
            isLoading = true
            message = null
            try {
                ApiClient.service.createTeam(
                    CreateTeamRequest(projectId = project.id, name = name, description = description)
                )
                loadTeams(project.id)
                message = "Equipo creado"
            } catch (e: Exception) {
                message = friendlyError(e)
            } finally {
                isLoading = false
            }
        }
    }

    fun updateTeam(teamId: Int, name: String, description: String) {
        val project = selectedProject ?: return
        if (name.isBlank() || description.isBlank()) {
            message = "Nombre y descripción son requeridos"
            return
        }
        viewModelScope.launch {
            isLoading = true
            message = null
            try {
                ApiClient.service.updateTeam(
                    UpdateTeamRequest(
                        projectId = project.id,
                        teamId = teamId,
                        name = name,
                        description = description
                    )
                )
                loadTeams(project.id)
                message = "Equipo actualizado"
            } catch (e: Exception) {
                message = friendlyError(e)
            } finally {
                isLoading = false
            }
        }
    }

    fun removeTeam(teamId: Int) {
        val project = selectedProject ?: return
        viewModelScope.launch {
            isLoading = true
            message = null
            try {
                ApiClient.service.removeTeam(
                    RemoveTeamRequest(projectId = project.id, teamId = teamId)
                )
                loadTeams(project.id)
                message = "Equipo eliminado"
            } catch (e: Exception) {
                message = friendlyError(e)
            } finally {
                isLoading = false
            }
        }
    }

    private fun friendlyError(e: Exception): String {
        return when (e) {
            is HttpException -> "Error HTTP ${e.code()}"
            else -> e.message ?: "Error desconocido"
        }
    }
}
