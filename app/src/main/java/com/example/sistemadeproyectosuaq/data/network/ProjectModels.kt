package com.example.sistemadeproyectosuaq.data.network

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// --- Models for Getting Projects and Tasks ---

@Serializable
data class ProjectsResponse(
    val projects: List<Project>
)

@Serializable
@Parcelize
data class Project(
    val id: String,
    val name: String,
    val description: String,
    val start: String,
    val end: String
) : Parcelable

@Serializable
data class TasksResponse(
    val tasks: List<ApiTask>
)

@Serializable
data class ModulesResponse(
    val modules: List<Module>
)

@Serializable
data class Module(
    val id: Int,
    val title: String,
    val description: String,
    val priority: String,
    val status: String,
    @SerialName("team_ids") val teamIds: Int? = null
)

@Serializable
data class CreateModuleRequest(
    @SerialName("project_id") val projectId: String,
    val title: String,
    val description: String,
    val priority: String,
    val status: String
)

@Serializable
data class ApiTask(
    val id: Int,
    val title: String,
    val description: String,
    val priority: String,
    val status: String,
    @SerialName("user_ids") val user_ids: Int? = null
)

// --- Models for Creating a Project ---

@Serializable
data class CreateProjectRequest(
    val name: String,
    val description: String,
    @SerialName("client_id") val clientId: Int,
    @SerialName("team_leader_id") val teamLeaderId: Int,
    val start: String,
    val end: String      // Expecting format like "YYYY-MM-DD"
)

@Serializable
data class CreateProjectResponse(
    @SerialName("project_id") val projectId: String? = null
)

@Serializable
data class DeleteProjectRequest(
    @SerialName("project_id") val projectId: String
)

// --- Models for User Management ---

@Serializable
data class CreateUserRequest(
    val name: String,
    val email: String,
    val phone: String,
    val role: String
)

@Serializable
data class CreateUserResponse(
    val message: String,
    @SerialName("tempPassword") val tempPassword: String
)

// --- Models for Project Members ---

@Serializable
data class ProjectMembersResponse(
    val members: List<ProjectMember>
)

@Serializable
data class ProjectMember(
    val id: Int,
    val name: String
)

@Serializable
data class AddMemberRequest(
    @SerialName("project_id") val projectId: String,
    @SerialName("user_id") val userId: Int
)

@Serializable
data class RemoveMemberRequest(
    @SerialName("project_id") val projectId: String,
    @SerialName("user_id") val userId: Int
)

@Serializable
data class MessageResponse(
    val message: String
)

// --- Models for Teams ---

@Serializable
data class TeamsResponse(
    val teams: List<Team>
)

@Serializable
data class Team(
    val id: Int,
    val name: String,
    val description: String
)

@Serializable
data class CreateTeamRequest(
    @SerialName("project_id") val projectId: String,
    val name: String,
    val description: String
)

@Serializable
data class UpdateTeamRequest(
    @SerialName("project_id") val projectId: String,
    @SerialName("team_id") val teamId: Int,
    val name: String,
    val description: String
)

@Serializable
data class RemoveTeamRequest(
    @SerialName("project_id") val projectId: String,
    @SerialName("team_id") val teamId: Int
)

@Serializable
data class CreateTaskRequest(
    val project_id: String,
    val module_id: String,
    val title: String,
    val description: String,
    val priority: String,
    val status: String,
    val user_ids: List<Int>?
)

// --- Models for Task Updates ---

@Serializable
data class UpdateTaskRequest(
    val project_id: String,
    val module_id: String,
    val task_id: Int,
    val title: String,
    val description: String,
    val priority: String,
    val status: String,
    val user_ids: List<Int>?
)

@Serializable
data class RemoveTaskRequest(
    val project_id: String,
    val module_id: String,
    val task_id: Int
)
