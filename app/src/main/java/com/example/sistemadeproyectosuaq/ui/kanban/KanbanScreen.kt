package com.example.sistemadeproyectosuaq.ui.kanban

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.sistemadeproyectosuaq.data.network.Project
import com.example.sistemadeproyectosuaq.ui.theme.SistemaDeProyectosUAQTheme

enum class TaskStatus(val title: String, val color: Color) {
    TODO("TO DO", Color.Red),
    IN_PROGRESS("IN PROGRESS", Color(0xFFFFA500)),
    DONE("DONE", Color.Green)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanbanScreen(
    modifier: Modifier = Modifier,
    project: Project,
    userRole: String,
    onTaskClick: (ApiTask) -> Unit,
    onAddTaskClick: () -> Unit,
    onAddModuleClick: () -> Unit = {},
    onNavigateBack: () -> Unit,
    refreshKey: Int = 0,
    viewModel: KanbanViewModel = viewModel()
) {
    LaunchedEffect(project, refreshKey) {
        viewModel.fetchTasksForProject(project)
    }

    val uiState = viewModel.uiState

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(project.name) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Projects")
                    }
                }
            )
        },
        floatingActionButton = {
            if (userRole == "1") {
                Column(horizontalAlignment = Alignment.End) {
                    FloatingActionButton(onClick = onAddModuleClick) {
                        Icon(Icons.Default.Info, contentDescription = "Add Module")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    FloatingActionButton(onClick = onAddTaskClick) {
                        Icon(Icons.Default.Add, contentDescription = "Add Task")
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is KanbanUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is KanbanUiState.Error -> {
                    Text(text = uiState.message, color = Color.Red)
                }
                is KanbanUiState.Success -> {
                    KanbanView(
                        tasks = uiState.data.tasks,
                        onTaskClick = onTaskClick,
                        contentPadding = paddingValues
                    )
                }
            }
        }
    }
}

@Composable
fun KanbanView(
    tasks: List<ApiTask>,
    onTaskClick: (ApiTask) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 16.dp)
            .horizontalScroll(scrollState)
    ) {
        val tasksByStatus = tasks.groupBy { it.status }

        TaskStatus.entries.forEach { status ->
            val apiStatus = mapTaskStatusToApiStatus(status)
            KanbanColumn(
                status = status,
                tasks = tasksByStatus[apiStatus] ?: emptyList(),
                onTaskClick = onTaskClick
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}

private fun mapApiStatusToTaskStatus(apiStatus: String): TaskStatus {
    return when (apiStatus) {
        "1" -> TaskStatus.TODO
        "2" -> TaskStatus.IN_PROGRESS
        "3" -> TaskStatus.DONE
        else -> TaskStatus.TODO
    }
}

private fun mapTaskStatusToApiStatus(status: TaskStatus): String {
    return when (status) {
        TaskStatus.TODO -> "1"
        TaskStatus.IN_PROGRESS -> "2"
        TaskStatus.DONE -> "3"
    }
}

@Composable
fun KanbanColumn(status: TaskStatus, tasks: List<ApiTask>, onTaskClick: (ApiTask) -> Unit) {
    Column(modifier = Modifier.width(280.dp)) {
        Text(text = status.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.fillMaxHeight()) {
            items(tasks) { task ->
                TaskCard(task = task, onTaskClick = onTaskClick)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun TaskCard(task: ApiTask, onTaskClick: (ApiTask) -> Unit) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .clickable { onTaskClick(task) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = task.title, fontWeight = FontWeight.Bold)
                Text(text = task.description)
            }
            Spacer(modifier = Modifier.width(8.dp))
            val status = mapApiStatusToTaskStatus(task.status)
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(status.color)
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 600)
@Composable
fun KanbanScreenPreview() {
    SistemaDeProyectosUAQTheme {
        val project = Project("1", "Preview Project", "Description", "", "")
        KanbanScreen(
            project = project,
            userRole = "1",
            onTaskClick = { _ -> },
            onAddTaskClick = {},
            onNavigateBack = {}
        )
    }
}
