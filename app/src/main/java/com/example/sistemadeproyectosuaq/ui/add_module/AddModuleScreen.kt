package com.example.sistemadeproyectosuaq.ui.add_module

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sistemadeproyectosuaq.data.network.Project

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddModuleScreen(
    project: Project,
    onModuleCreated: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AddModuleViewModel = viewModel()
) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    // Prioridades: "1"=Low, "2"=Medium, "3"=High (como strings)
    val priorities = listOf("1" to "Low", "2" to "Medium", "3" to "High")
    var isPriorityMenuExpanded by remember { mutableStateOf(false) }
    var selectedPriority by remember { mutableStateOf(priorities[1]) } // Default: Medium
    
    // Estados: "1"=TODO, "2"=IN_PROGRESS, "3"=DONE (como strings)
    val statuses = listOf("1" to "TODO", "2" to "IN_PROGRESS", "3" to "DONE")
    var isStatusMenuExpanded by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf(statuses[0]) } // Default: TODO

    LaunchedEffect(uiState) {
        when (uiState) {
            is AddModuleUiState.Success -> {
                snackbarHostState.showSnackbar(uiState.message)
                onModuleCreated()
                viewModel.resetState()
            }
            is AddModuleUiState.Error -> {
                snackbarHostState.showSnackbar(uiState.message)
            }
            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Crear Módulo - ${project.name}") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título del Módulo") },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is AddModuleUiState.Loading
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                enabled = uiState !is AddModuleUiState.Loading
            )
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = isPriorityMenuExpanded,
                onExpandedChange = { isPriorityMenuExpanded = it }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
                    value = selectedPriority.second,
                    onValueChange = {},
                    label = { Text("Prioridad") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isPriorityMenuExpanded) },
                    enabled = uiState !is AddModuleUiState.Loading
                )
                ExposedDropdownMenu(
                    expanded = isPriorityMenuExpanded,
                    onDismissRequest = { isPriorityMenuExpanded = false }
                ) {
                    priorities.forEach { priority ->
                        DropdownMenuItem(
                            text = { Text(priority.second) },
                            onClick = {
                                selectedPriority = priority
                                isPriorityMenuExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = isStatusMenuExpanded,
                onExpandedChange = { isStatusMenuExpanded = it }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
                    value = selectedStatus.second,
                    onValueChange = {},
                    label = { Text("Estado") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStatusMenuExpanded) },
                    enabled = uiState !is AddModuleUiState.Loading
                )
                ExposedDropdownMenu(
                    expanded = isStatusMenuExpanded,
                    onDismissRequest = { isStatusMenuExpanded = false }
                ) {
                    statuses.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.second) },
                            onClick = {
                                selectedStatus = status
                                isStatusMenuExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            if (uiState is AddModuleUiState.Loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        viewModel.createModule(
                            projectId = project.id,
                            title = title,
                            description = description,
                            priority = selectedPriority.first,
                            status = selectedStatus.first
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Crear Módulo")
                }
            }
        }
    }
}
