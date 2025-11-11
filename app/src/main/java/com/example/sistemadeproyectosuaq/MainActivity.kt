package com.example.sistemadeproyectosuaq

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
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
import com.example.sistemadeproyectosuaq.ui.kanban.KanbanScreen
import com.example.sistemadeproyectosuaq.ui.kanban.TaskDetail
import com.example.sistemadeproyectosuaq.ui.login.LoginScreen
import com.example.sistemadeproyectosuaq.ui.login.LoginSuccessData
import com.example.sistemadeproyectosuaq.ui.profile.UserAdminScreen
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
    var selectedTaskId by rememberSaveable { mutableStateOf<Int?>(null) }
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    if (userRole != null) {
        val availableDestinations = if (userRole == "Admin") {
            AppDestinations.entries
        } else {
            AppDestinations.entries.filter { it != AppDestinations.PROFILE }
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
                            selectedTaskId = null // Reset task detail when switching tabs
                        }
                    )
                }
            }
        ) {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                when (currentDestination) {
                    AppDestinations.HOME -> {
                        if (selectedTaskId == null) {
                            KanbanScreen(onTaskClick = { task -> selectedTaskId = task.id })
                        } else {
                            TaskDetail(taskId = selectedTaskId!!, onNavigateBack = { selectedTaskId = null })
                        }
                    }

                    AppDestinations.PROFILE -> {
                        UserAdminScreen(onNavigateBack = { currentDestination = AppDestinations.HOME })
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
) {
    HOME("Home", Icons.Default.Home),
    PROFILE("Profile", Icons.Default.AccountBox),
}
