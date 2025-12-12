package com.example.sistemadeproyectosuaq.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sistemadeproyectosuaq.ui.theme.SistemaDeProyectosUAQTheme
import kotlinx.coroutines.launch

data class AdminUser(val id: Int, val name: String, val email: String, val role: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAdminScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: UserAdminViewModel = viewModel()
) {
    var users by remember { mutableStateOf(emptyList<AdminUser>()) }
    var showAddUserDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val uiState = viewModel.uiState

    LaunchedEffect(uiState) {
        when (uiState) {
            is CreateUserUiState.Success -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        "${uiState.message}\nContraseña temporal: ${uiState.tempPassword}"
                    )
                }
                showAddUserDialog = false
                viewModel.resetState()
            }
            is CreateUserUiState.Error -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(uiState.message)
                }
                viewModel.resetState()
            }
            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("ADMINISTRACION DE USUARIOS") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        bottomBar = {
            OutlinedButton(
                onClick = { showAddUserDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Añadir")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            items(users) { user ->
                UserAdminItem(user = user)
                HorizontalDivider(color = Color(0xFF0D3B66))
            }
        }

        if (showAddUserDialog) {
            AddUserDialog(
                uiState = uiState,
                onDismissRequest = { 
                    showAddUserDialog = false
                    viewModel.resetState()
                },
                onConfirmation = { name, email, phone, role ->
                    viewModel.createUser(name, email, phone, role)
                }
            )
        }
    }
}

@Composable
fun UserAdminItem(user: AdminUser) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(user.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Correo: ${user.email}")
        Text("Contraseña: ••••••••")
        Text("Rol: ${user.role}")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddUserDialog(
    uiState: CreateUserUiState,
    onDismissRequest: () -> Unit,
    onConfirmation: (name: String, email: String, phone: String, role: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    val roles = listOf("1" to "Admin", "2" to "Usuario", "3" to "Cliente")
    var isRolesMenuExpanded by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf(roles[0]) }
    var emailError by remember { mutableStateOf("") }

    fun isEmailValid(email: String): Boolean {
        val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        return email.matches(Regex(emailPattern))
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Añadir Nuevo Usuario") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    enabled = uiState !is CreateUserUiState.Loading
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = if (isEmailValid(it)) "" else "Correo electrónico no válido"
                    },
                    label = { Text("Correo Electrónico") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = emailError.isNotEmpty(),
                    enabled = uiState !is CreateUserUiState.Loading
                )
                if (emailError.isNotEmpty()) {
                    Text(
                        text = emailError,
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Teléfono") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    enabled = uiState !is CreateUserUiState.Loading
                )

                ExposedDropdownMenuBox(
                    expanded = isRolesMenuExpanded,
                    onExpandedChange = { isRolesMenuExpanded = it }
                ) {
                    OutlinedTextField(
                        modifier = Modifier.menuAnchor(),
                        readOnly = true,
                        value = selectedRole.second,
                        onValueChange = {},
                        label = { Text("Rol") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isRolesMenuExpanded) },
                        enabled = uiState !is CreateUserUiState.Loading
                    )
                    ExposedDropdownMenu(
                        expanded = isRolesMenuExpanded,
                        onDismissRequest = { isRolesMenuExpanded = false }
                    ) {
                        roles.forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role.second) },
                                onClick = {
                                    selectedRole = role
                                    isRolesMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                if (uiState is CreateUserUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp))
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isEmailValid(email) && name.isNotBlank() && phone.isNotBlank()) {
                        onConfirmation(name, email, phone, selectedRole.first)
                    }
                },
                enabled = uiState !is CreateUserUiState.Loading
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                enabled = uiState !is CreateUserUiState.Loading
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun UserAdminScreenPreview() {
    SistemaDeProyectosUAQTheme {
        UserAdminScreen(onNavigateBack = {}, onLogout = {})
    }
}
