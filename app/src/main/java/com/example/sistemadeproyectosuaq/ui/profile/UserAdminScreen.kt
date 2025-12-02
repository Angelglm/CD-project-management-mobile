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
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistemadeproyectosuaq.ui.theme.SistemaDeProyectosUAQTheme

data class AdminUser(val id: Int, val name: String, val email: String, val role: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAdminScreen(onNavigateBack: () -> Unit) {
    var users by remember {
        mutableStateOf(listOf(
            AdminUser(1, "NOMBRE", "correo@example.com", "Admin"),
            AdminUser(2, "NOMBRE", "correo2@example.com", "User"),
            AdminUser(3, "NOMBRE", "correo3@example.com", "User")
        ))
    }
    var showAddUserDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ADMINISTRACION DE USUARIOS") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                onDismissRequest = { showAddUserDialog = false },
                onConfirmation = { name, email, role ->
                    val newId = (users.maxOfOrNull { it.id } ?: 0) + 1
                    users = users + AdminUser(newId, name, email, role)
                    showAddUserDialog = false
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
    onDismissRequest: () -> Unit,
    onConfirmation: (name: String, email: String, role: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val roles = listOf("Admin", "User", "Cliente")
    var isRolesMenuExpanded by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf(roles[0]) }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    // Expresión regular para validar el correo electrónico
    fun isEmailValid(email: String): Boolean {
        val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        return email.matches(Regex(emailPattern))
    }

    // Expresión regular para validar contraseñas: al menos una mayúscula, un número, un carácter especial y longitud mínima de 6 caracteres.
    fun isPasswordValid(password: String): Boolean {
        val passwordPattern = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*(),.?\":{}|<>_]).{6,}$"
        return password.matches(Regex(passwordPattern))
    }

    fun passwordErrorMessage(password: String): String {
        return when {
            password.length < 6 -> "La contraseña debe tener al menos 6 caracteres."
            !password.contains(Regex("[A-Z]")) -> "La contraseña debe tener al menos una mayúscula."
            !password.contains(Regex("[0-9]")) -> "La contraseña debe tener al menos un número."
            !password.contains(Regex("[!@#$%^&*(),.?\":{}|<>_]")) -> "La contraseña debe tener al menos un carácter especial."
            else -> ""
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Añadir Nuevo Usuario") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") }
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = if (isEmailValid(it)) "" else "Correo electrónico no válido"
                    },
                    label = { Text("Correo Electrónico") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = emailError.isNotEmpty()
                )
                if (emailError.isNotEmpty()) {
                    Text(
                        text = emailError,
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
                OutlinedTextField(
                    value = password,
                    onValueChange = { 
                        password = it
                        passwordError = passwordErrorMessage(it)  
                    },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = passwordError.isNotEmpty()
                )
                if (passwordError.isNotEmpty()) {
                    Text(
                        text = passwordError,
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }

                ExposedDropdownMenuBox(
                    expanded = isRolesMenuExpanded,
                    onExpandedChange = { isRolesMenuExpanded = it }
                ) {
                    OutlinedTextField(
                        modifier = Modifier.menuAnchor(),
                        readOnly = true,
                        value = selectedRole,
                        onValueChange = {},
                        label = { Text("Rol") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isRolesMenuExpanded) }
                    )
                    ExposedDropdownMenu(
                        expanded = isRolesMenuExpanded,
                        onDismissRequest = { isRolesMenuExpanded = false }
                    ) {
                        roles.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    selectedRole = selectionOption
                                    isRolesMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isEmailValid(email) && passwordError.isEmpty()) {
                        onConfirmation(name, email, selectedRole)
                    }
                }
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
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
        UserAdminScreen(onNavigateBack = {})
    }
}