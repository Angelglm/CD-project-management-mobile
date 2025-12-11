package com.example.sistemadeproyectosuaq.ui.add_project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sistemadeproyectosuaq.ui.theme.SistemaDeProyectosUAQTheme

@Composable
fun AddProjectScreen(
    onProjectCreated: () -> Unit,
    viewModel: AddProjectViewModel = viewModel()
) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var clientId by remember { mutableStateOf("") }
    var teamLeaderId by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(text = "Crear Nuevo Proyecto")
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre del Proyecto") })
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") })
            OutlinedTextField(value = clientId, onValueChange = { clientId = it }, label = { Text("ID del Cliente") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = teamLeaderId, onValueChange = { teamLeaderId = it }, label = { Text("ID del Líder de Equipo") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = startDate, onValueChange = { startDate = it }, label = { Text("Fecha de Inicio (YYYY-MM-DD)") })
            OutlinedTextField(value = endDate, onValueChange = { endDate = it }, label = { Text("Fecha de Fin (YYYY-MM-DD)") })

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState is AddProjectUiState.Loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        viewModel.createProject(
                            name = name,
                            description = description,
                            clientId = clientId.toIntOrNull() ?: 0,
                            teamLeaderId = teamLeaderId.toIntOrNull() ?: 0,
                            start = startDate,
                            end = endDate
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Crear Proyecto")
                }
            }
        }
    }


    LaunchedEffect(uiState) {
        when (uiState) {
            is AddProjectUiState.Success -> {
                snackbarHostState.showSnackbar("¡Proyecto creado con éxito!")
                onProjectCreated()
                viewModel.resetState()
            }
            is AddProjectUiState.Error -> {
                snackbarHostState.showSnackbar(uiState.message)
            }
            else -> Unit
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddProjectScreenPreview() {
    SistemaDeProyectosUAQTheme {
        AddProjectScreen(onProjectCreated = {})
    }
}
