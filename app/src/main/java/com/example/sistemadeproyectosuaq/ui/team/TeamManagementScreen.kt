package com.example.sistemadeproyectosuaq.ui.team

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sistemadeproyectosuaq.data.network.Project
import com.example.sistemadeproyectosuaq.data.network.Team
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamManagementScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: TeamManagementViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var newTeamName by remember { mutableStateOf("") }
    var newTeamDesc by remember { mutableStateOf("") }
    var editingTeam by remember { mutableStateOf<Team?>(null) }
    var editName by remember { mutableStateOf("") }
    var editDesc by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadProjects()
    }

    LaunchedEffect(viewModel.message) {
        viewModel.message?.let { msg ->
            scope.launch { snackbarHostState.showSnackbar(msg) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administración de equipos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            androidx.compose.material3.Card(
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = androidx.compose.ui.graphics.Color(0xFFFFF3CD)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text(
                        "⚠️",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        "Funciones como asignar miembros a equipos no están disponibles por falta de endpoints.",
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                    )
                }
            }
            ProjectPicker(
                projects = viewModel.projects,
                selectedProject = viewModel.selectedProject,
                onSelect = { viewModel.selectProject(it) }
            )

            if (viewModel.selectedProject != null) {
                OutlinedTextField(
                    value = newTeamName,
                    onValueChange = { newTeamName = it },
                    label = { Text("Nombre del equipo") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = newTeamDesc,
                    onValueChange = { newTeamDesc = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        viewModel.createTeam(newTeamName, newTeamDesc)
                        newTeamName = ""
                        newTeamDesc = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !viewModel.isLoading
                ) {
                    Text("Crear equipo")
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(viewModel.teams) { team ->
                        TeamRow(
                            team = team,
                            onEdit = {
                                editingTeam = team
                                editName = team.name
                                editDesc = team.description
                            },
                            onDelete = { viewModel.removeTeam(team.id) }
                        )
                    }
                }
            } else {
                Text("Selecciona un proyecto para administrar sus equipos.")
            }
        }
    }

    editingTeam?.let { team ->
        AlertDialog(
            onDismissRequest = { editingTeam = null },
            title = { Text("Editar equipo") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editDesc,
                        onValueChange = { editDesc = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateTeam(team.id, editName, editDesc)
                    editingTeam = null
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingTeam = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun ProjectPicker(
    projects: List<Project>,
    selectedProject: Project?,
    onSelect: (Project) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val label = selectedProject?.name ?: "Selecciona un proyecto"

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Proyecto")
        Button(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(label)
        }
        androidx.compose.material3.DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            projects.forEach { project ->
                androidx.compose.material3.DropdownMenuItem(
                    text = { Text(project.name) },
                    onClick = {
                        onSelect(project)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun TeamRow(
    team: Team,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(team.name)
                Text(team.description)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onEdit) { Text("Editar") }
                TextButton(onClick = onDelete) { Text("Eliminar") }
            }
        }
    }
}
