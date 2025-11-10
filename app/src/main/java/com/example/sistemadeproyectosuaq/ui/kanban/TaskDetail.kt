package com.example.sistemadeproyectosuaq.ui.kanban

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import com.example.sistemadeproyectosuaq.ui.theme.SistemaDeProyectosUAQTheme

// Mock data - replace with ViewModel data later
data class User(val id: Int, val name: String)
data class Comment(val id: Int, val author: String, val text: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetail(taskId: Int, onNavigateBack: () -> Unit) {
    // In a real app, you'd fetch this from a ViewModel based on taskId
    val task = Task(taskId, "NOMBRE DE LA TAREA", "Descripcion", TaskStatus.IN_PROGRESS)
    val users = listOf(User(1, "Usuario 1"), User(2, "usuario 2"), User(3, "Usuario 3"))
    val initialComments = listOf(
        Comment(1, "Usuario 1", "Lorem Ipsum is simply dummy text of the printing and typesetting industry"),
        Comment(2, "Usuario 2", "Lorem Ipsum is simply dummy text of the printing and typesetting industry")
    )
    var comments by remember { mutableStateOf(initialComments) }
    var newCommentText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles Tarea") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = task.title, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(task.status.color)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text("PROGRESO")
            LinearProgressIndicator(progress = { 0.6f }, modifier = Modifier.fillMaxWidth())
            Text("60%", modifier = Modifier.align(Alignment.End))
            Spacer(modifier = Modifier.height(8.dp))

            Text("Fecha limite: xx/xx/xxxx")
            Spacer(modifier = Modifier.height(16.dp))

            Text("Usuarios asignados:")
            users.forEach { user ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color.Gray, CircleShape)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(user.name)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text("Comentarios:")
            Column(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)) {
                comments.forEach { comment ->
                    Text("${comment.author}: ${comment.text}", style = MaterialTheme.typography.bodySmall)
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
                        comments = comments + Comment(id = newId, author = "Tú", text = newCommentText)
                        newCommentText = ""
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Añadir")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskDetailPreview() {
    SistemaDeProyectosUAQTheme {
        TaskDetail(taskId = 1, onNavigateBack = {})
    }
}
