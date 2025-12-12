package com.example.sistemadeproyectosuaq.ui.projects

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sistemadeproyectosuaq.data.network.Project
import com.example.sistemadeproyectosuaq.ui.theme.SistemaDeProyectosUAQTheme

@Composable
fun ProjectListScreen(
    onProjectClick: (Project) -> Unit,
    refreshTrigger: Int = 0,
    viewModel: ProjectListViewModel = viewModel()
) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var projectToDelete by remember { mutableStateOf<Project?>(null) }

    LaunchedEffect(refreshTrigger) {
        viewModel.fetchProjects()
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(top = 16.dp)) {
        when (uiState) {
            is ProjectListUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ProjectListUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.message, color = Color.Red)
                }
            }
            is ProjectListUiState.Success -> {
                LazyColumn(modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)) {
                    items(uiState.projects) { project ->
                        ProjectListItem(
                            project = project,
                            onClick = { onProjectClick(project) },
                            onDeleteClick = { projectToDelete = project }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }

        // Confirmation dialog
        projectToDelete?.let { project ->
            AlertDialog(
                onDismissRequest = { projectToDelete = null },
                title = { Text("Eliminar Proyecto") },
                text = {
                    Column {
                        Text("¿Estás seguro de eliminar el proyecto \"${project.name}\"?")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "⚠️ Esta acción es irreversible.",
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteProject(
                                projectId = project.id,
                                onSuccess = {
                                    projectToDelete = null
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Proyecto eliminado")
                                    }
                                },
                                onError = { error ->
                                    projectToDelete = null
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(error)
                                    }
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        )
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { projectToDelete = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun ProjectListItem(project: Project, onClick: () -> Unit, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(project.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(project.description, color = Color.Gray)
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar proyecto",
                    tint = Color.Red
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProjectListScreenPreview() {
    SistemaDeProyectosUAQTheme {
        val projects = listOf(
            Project("1", "Project Alpha", "Description for Alpha", "", ""),
            Project("2", "Project Beta", "Description for Beta", "", "")
        )
        LazyColumn {
            items(projects) {
                ProjectListItem(project = it, onClick = {}, onDeleteClick = {})
                HorizontalDivider()
            }
        }
    }
}
