package com.example.sistemadeproyectosuaq.ui.kanban

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.DropdownMenuItem
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
import com.example.sistemadeproyectosuaq.data.network.Module
import com.example.sistemadeproyectosuaq.data.network.Project
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    modifier: Modifier = Modifier,
    project: Project,
    onTaskCreated: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AddTaskViewModel = viewModel()
) {
    val uiState = viewModel.uiState
    val modules = viewModel.modules
    val isModuleLoading = viewModel.isModuleLoading
    val moduleError = viewModel.moduleError

    var selectedModule by remember { mutableStateOf<Module?>(null) }
    var moduleDropdownExpanded by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("1") }
    var status by remember { mutableStateOf("1") }
    var userIdsInput by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(project.id) {
        viewModel.loadModules(project.id)
        viewModel.resetState()
    }

    LaunchedEffect(moduleError) {
        moduleError?.let { message ->
            scope.launch { snackbarHostState.showSnackbar(message) }
        }
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is AddTaskUiState.Success -> {
                scope.launch { snackbarHostState.showSnackbar(uiState.message) }
                onTaskCreated()
                viewModel.resetState()
            }
            is AddTaskUiState.Error -> {
                scope.launch { snackbarHostState.showSnackbar(uiState.message) }
            }
            else -> Unit
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Nueva tarea") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            Text(text = "Proyecto: ${project.name}")
            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = moduleDropdownExpanded,
                onExpandedChange = { moduleDropdownExpanded = !moduleDropdownExpanded }
            ) {
                val moduleLabel = selectedModule?.title ?: if (isModuleLoading) "Cargando modulos..." else "Selecciona un modulo"
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    value = moduleLabel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Modulo") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = moduleDropdownExpanded) },
                    enabled = modules.isNotEmpty()
                )
                DropdownMenu(
                    expanded = moduleDropdownExpanded,
                    onDismissRequest = { moduleDropdownExpanded = false }
                ) {
                    modules.forEach { module ->
                        DropdownMenuItem(
                            text = { Text(module.title) },
                            onClick = {
                                selectedModule = module
                                moduleDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titulo") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripcion") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = priority,
                onValueChange = { priority = it },
                label = { Text("Prioridad (1-3)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = status,
                onValueChange = { status = it },
                label = { Text("Estado (1-3)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = userIdsInput,
                onValueChange = { userIdsInput = it },
                label = { Text("IDs de usuarios (separados por coma)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState is AddTaskUiState.Loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        val tokens = userIdsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        val invalidToken = tokens.firstOrNull { it.toIntOrNull() == null }
                        if (invalidToken != null) {
                            scope.launch { snackbarHostState.showSnackbar("ID de usuario invalido: $invalidToken") }
                            return@Button
                        }
                        val userIds = if (tokens.isEmpty()) null else tokens.map { it.toInt() }
                        viewModel.createTask(
                            projectId = project.id,
                            moduleId = selectedModule?.id?.toString() ?: "",
                            title = title,
                            description = description,
                            priority = priority,
                            status = status,
                            userIds = userIds
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Crear tarea")
                }
            }
        }
    }
}
