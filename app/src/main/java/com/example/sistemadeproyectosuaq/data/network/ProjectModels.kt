package com.example.sistemadeproyectosuaq.data.network

import android.annotation.SuppressLint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// --- Models for Getting Projects and Tasks ---

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ProjectsResponse(
    val projects: List<Project>
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class Project(
    val id: String,
    val name: String,
    val description: String,
    val start: String,
    val end: String
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class TasksResponse(
    val tasks: List<ApiTask>
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ApiTask(
    val id: Int,
    val title: String,
    val description: String,
    val priority: String,
    val status: String,
    @SerialName("user_ids") val userIds: Int? = null
)

// --- Models for Creating a Project ---

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class CreateProjectRequest(
    val name: String,
    val description: String,
    @SerialName("client_id") val clientId: Int,
    @SerialName("team_leader_id") val teamLeaderId: Int,
    val start: String, // Expecting format like "YYYY-MM-DD"
    val end: String      // Expecting format like "YYYY-MM-DD"
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class CreateProjectResponse(
    @SerialName("projectID") val projectId: String
)

// --- Models for Project Members ---

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ProjectMembersResponse(
    val members: List<ProjectMember>
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ProjectMember(
    val id: Int,
    val name: String
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class AddMemberRequest(
    @SerialName("project_id") val projectId: String,
    @SerialName("user_id") val userId: Int
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class MessageResponse(
    val message: String
)
