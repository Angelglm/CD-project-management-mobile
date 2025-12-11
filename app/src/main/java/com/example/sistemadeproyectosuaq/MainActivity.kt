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
import androidx.compose.material.icons.filled.Build
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
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    val onLogout = {
        userRole = null
        selectedProject = null
        selectedTaskId = null
        SessionManager.onLogout()
        currentDestination = AppDestinations.HOME
    }

    if (userRole != null) {
        val availableDestinations = if (userRole == "1") {
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
                        }
                    )
                }
            }
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize()
            ) { paddingValues ->
                val modifier = Modifier.padding(paddingValues)

                when (currentDestination) {
                    AppDestinations.HOME -> {
                        if (selectedProject == null) {
                            ProjectListScreen(
                                onProjectClick = { project ->
                                    selectedProject = project
                                }
                            )
                        } else if (selectedTaskId == null) {
                            KanbanScreen(
                                project = selectedProject!!,
                                userRole = userRole!!,
                                onTaskClick = { task ->
                                    selectedTaskId = task.id
                                },
                                onAddTaskClick = { /* TODO */ },
                                onNavigateBack = { selectedProject = null }
                            )
                        } else {
                            TaskDetail(
                                userRole = userRole!!,
                                projectId = selectedProject!!.id,
                                moduleId = "1",
                                taskId = selectedTaskId!!,
                                onNavigateBack = { selectedTaskId = null }
                            )
                        }
                    }

                    AppDestinations.PROJECT_MANAGEMENT -> {
                        ProjectListScreen(onProjectClick = { project ->
                            selectedProject = project
                            currentDestination = AppDestinations.HOME
                        })
                    }

                    AppDestinations.ADD_PROJECT -> {
                        AddProjectScreen(
                            onProjectCreated = {
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
    PROJECT_MANAGEMENT("Projects", Icons.Default.Build, true),
    ADD_PROJECT("Add Project", Icons.Default.Add, true),
    PROFILE("Profile", Icons.Default.AccountBox, true),
}
