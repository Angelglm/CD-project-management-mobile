package com.example.sistemadeproyectosuaq.ui.kanban

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sistemadeproyectosuaq.data.network.ApiTask
import com.example.sistemadeproyectosuaq.ui.theme.SistemaDeProyectosUAQTheme

// Mock data - replace with ViewModel data later
data class Comment(val id: Int, val author: String, val text: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetail(
    userRole: String,
    projectId: String,
    moduleId: String,
    taskId: Int,
    onNavigateBack: () -> Unit,
    viewModel: TaskDetailViewModel = viewModel()
) {
    LaunchedEffect(taskId) {
        viewModel.fetchTask(projectId, moduleId, taskId)
    }

    val uiState = viewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles Tarea") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (userRole == "1") { // Admin
                        IconButton(onClick = {
                            // TODO: viewModel.deleteTask(projectId, moduleId, taskId)
                        }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete Task"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is TaskDetailUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is TaskDetailUiState.Error -> {
                    Text(text = uiState.message, color = Color.Red)
                }

                is TaskDetailUiState.Success -> {
                    TaskDetailContent(
                        task = uiState.task,
                        userRole = userRole,
                        onUpdateStatus = { newStatus ->
                            viewModel.updateTaskStatus(
                                projectId,
                                moduleId,
                                uiState.task,
                                newStatus
                            )
                        }
                    )
                }

                TaskDetailUiState.TaskDeleted -> {
                    // Por ejemplo, regresar automáticamente cuando se borre
                    LaunchedEffect(Unit) {
                        onNavigateBack()
                    }
                }
            }
        }
    }
}

@Composable
fun TaskDetailContent(
    task: ApiTask,
    userRole: String,
    onUpdateStatus: (String) -> Unit
) {
    var newCommentText by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf(emptyList<Comment>()) } // Placeholder

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = task.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            val status = mapApiStatusToTaskStatus(task.status)
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(status.color)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(task.description)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Comentarios:")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            comments.forEach { comment ->
                Text(
                    "${comment.author}: ${comment.text}",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        OutlinedTextField(
            value = newCommentText,
            onValueChange = { newCommentText = it },
            label = { Text("Añadir comentario") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                if (newCommentText.isNotBlank()) {
                    val newId = (comments.maxOfOrNull { it.id } ?: 0) + 1
                    comments = comments + Comment(
                        id = newId,
                        author = "Tú",
                        text = newCommentText
                    )
                    newCommentText = ""
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Añadir")
        }

        // --- Status Update Button ---
        if (userRole == "1" || userRole == "2") { // Admin or User
            Spacer(Modifier.height(16.dp))
            val currentStatus = mapApiStatusToTaskStatus(task.status)
            if (currentStatus != TaskStatus.DONE) {
                Button(
                    onClick = { onUpdateStatus("3") }, // "3" is for DONE
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Marcar como Completada")
                }
            }
        }
    }
}

// Esta función es LOCAL a este archivo TaskDetail.kt
// y NO tiene nada que ver con la privada de KanbanScreen.kt
private fun mapApiStatusToTaskStatus(apiStatus: String): TaskStatus {
    return when (apiStatus) {
        "1" -> TaskStatus.TODO
        "2" -> TaskStatus.IN_PROGRESS
        "3" -> TaskStatus.DONE
        else -> TaskStatus.TODO // Default case
    }
}

@Preview(showBackground = true)
@Composable
fun TaskDetailPreview() {
    SistemaDeProyectosUAQTheme {
        TaskDetailContent(
            task = ApiTask(
                id = 1,
                title = "Preview Task",
                description = "Descripción de prueba",
                priority = "1",
                status = "1",
                user_ids = listOf(1, 2)
            ),
            userRole = "1",
            onUpdateStatus = {}
        )
    }
}
