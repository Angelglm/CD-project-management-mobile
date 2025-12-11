package com.example.sistemadeproyectosuaq

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.example.sistemadeproyectosuaq.data.network.Project
import com.example.sistemadeproyectosuaq.data.network.SessionManager
import com.example.sistemadeproyectosuaq.ui.add_project.AddProjectScreen
import com.example.sistemadeproyectosuaq.ui.kanban.AddTaskScreen
import com.example.sistemadeproyectosuaq.ui.kanban.KanbanScreen
import com.example.sistemadeproyectosuaq.ui.kanban.TaskDetail
import com.example.sistemadeproyectosuaq.ui.login.LoginScreen
import com.example.sistemadeproyectosuaq.ui.profile.UserAdminScreen
import com.example.sistemadeproyectosuaq.ui.projects.ProjectListScreen
import com.example.sistemadeproyectosuaq.ui.theme.SistemaDeProyectosUAQTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SistemaDeProyectosUAQTheme {
                SistemaDeProyectosUAQApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun SistemaDeProyectosUAQApp() {
    var userRole by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedProject by rememberSaveable { mutableStateOf<Project?>(null) }
    var selectedTaskId by rememberSaveable { mutableStateOf<Int?>(null) }
    var isCreatingTask by rememberSaveable { mutableStateOf(false) }
    var projectListRefreshKey by rememberSaveable { mutableStateOf(0) }
    var kanbanRefreshKey by rememberSaveable { mutableStateOf(0) }
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    val onLogout = {
        userRole = null
        selectedProject = null
        selectedTaskId = null
        isCreatingTask = false
        SessionManager.onLogout()
        currentDestination = AppDestinations.HOME
    }

    if (userRole != null) {
        val availableDestinations = if (userRole == "1") { // Admin role
            AppDestinations.entries
        } else {
            AppDestinations.entries.filter { !it.isAdminOnly }
        }

        NavigationSuiteScaffold(
            navigationSuiteItems = {
                availableDestinations.forEach { destination ->
                    item(
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = destination.label
                            )
                        },
                        label = { Text(destination.label) },
                        selected = destination == currentDestination,
                        onClick = {
                            currentDestination = destination
                            selectedProject = null
                            selectedTaskId = null
                            isCreatingTask = false
                        }
                    )
                }
            }
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                val modifier = Modifier.padding(innerPadding)

                when (currentDestination) {
                    AppDestinations.HOME -> {
                        if (selectedProject == null) {
                            ProjectListScreen(
                                refreshTrigger = projectListRefreshKey,
                                onProjectClick = { project ->
                                    selectedProject = project
                                    isCreatingTask = false
                                }
                            )
                        } else if (isCreatingTask) {
                            AddTaskScreen(
                                modifier = modifier,
                                project = selectedProject!!,
                                onTaskCreated = {
                                    isCreatingTask = false
                                    kanbanRefreshKey += 1
                                },
                                onNavigateBack = {
                                    isCreatingTask = false
                                }
                            )
                        } else if (selectedTaskId == null) {
                            KanbanScreen(
                                modifier = modifier,
                                project = selectedProject!!,
                                userRole = userRole!!,
                                refreshKey = kanbanRefreshKey,
                                onTaskClick = { task ->
                                    selectedTaskId = task.id
                                },
                                onAddTaskClick = { isCreatingTask = true },
                                onNavigateBack = {
                                    selectedProject = null
                                    selectedTaskId = null
                                    isCreatingTask = false
                                }
                            )
                        } else {
                            TaskDetail(
                                userRole = userRole!!,
                                projectId = selectedProject!!.id,
                                moduleId = "1", // Placeholder: adjust if modules vary
                                taskId = selectedTaskId!!,
                                onNavigateBack = {
                                    selectedTaskId = null
                                    isCreatingTask = false
                                }
                            )
                        }
                    }

                    AppDestinations.ADD_PROJECT -> {
                        AddProjectScreen(
                            onProjectCreated = {
                                isCreatingTask = false
                                projectListRefreshKey += 1
                                currentDestination = AppDestinations.HOME
                            }
                        )
                    }

                    AppDestinations.PROFILE -> {
                        UserAdminScreen(
                            onNavigateBack = { currentDestination = AppDestinations.HOME },
                            onLogout = onLogout
                        )
                    }
                }
            }
        }
    } else {
        LoginScreen(onLoginSuccess = { successData ->
            userRole = successData.role
        })
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
    val isAdminOnly: Boolean = false
) {
    HOME("Home", Icons.Default.Home),
    ADD_PROJECT("Add Project", Icons.Default.Add, true),
    PROFILE("Profile", Icons.Default.AccountBox, true),
}
