package com.example.sistemadeproyectosuaq.ui.kanban

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

enum class TaskStatus(val title: String, val color: Color) {
    TODO("TO DO", Color.Red),
    IN_PROGRESS("IN PROGRESS", Color(0xFFFFA500)), // Orange
    DONE("DONE", Color.Green)
}

data class Task(val id: Int, val title: String, val description: String, val status: TaskStatus)

@Composable
fun KanbanScreen(
    onTaskClick: (Task) -> Unit,
    viewModel: KanbanViewModel = viewModel()
) {
    val uiState = viewModel.uiState

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (uiState) {
            is KanbanUiState.Loading -> {
                CircularProgressIndicator()
            }
            is KanbanUiState.Error -> {
                Text(text = uiState.message, color = Color.Red)
            }
            is KanbanUiState.Success -> {
                val projectName = uiState.data.project.name
                val tasks = uiState.data.tasks.map { mapApiTaskToTask(it) }
                KanbanView(projectName = projectName, tasks = tasks, onTaskClick = onTaskClick)
            }
        }
    }
}

@Composable
fun KanbanView(projectName: String, tasks: List<Task>, onTaskClick: (Task) -> Unit) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text(
            text = projectName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 20.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.horizontalScroll(scrollState)) {
            TaskStatus.entries.forEach { status ->
                KanbanColumn(
                    status = status,
                    tasks = tasks.filter { it.status == status },
                    onTaskClick = onTaskClick
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}

private fun mapApiTaskToTask(apiTask: ApiTask): Task {
    val status = when (apiTask.status) {
        "1" -> TaskStatus.TODO
        "2" -> TaskStatus.IN_PROGRESS
        "3" -> TaskStatus.DONE
        else -> TaskStatus.TODO // Default case
    }
    return Task(
        id = apiTask.id,
        title = apiTask.title,
        description = apiTask.description,
        status = status
    )
}

@Composable
fun KanbanColumn(status: TaskStatus, tasks: List<Task>, onTaskClick: (Task) -> Unit) {
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
fun TaskCard(task: Task, onTaskClick: (Task) -> Unit) {
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
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(task.status.color)
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 600)
@Composable
fun KanbanScreenPreview() {
    SistemaDeProyectosUAQTheme {
        KanbanView(projectName = "PREVIEW PROJECT", tasks = listOf(), onTaskClick = {})
    }
}
