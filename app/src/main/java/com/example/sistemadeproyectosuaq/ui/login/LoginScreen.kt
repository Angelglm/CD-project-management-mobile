package com.example.sistemadeproyectosuaq.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sistemadeproyectosuaq.ui.theme.SistemaDeProyectosUAQTheme


@Composable
fun LoginScreen(onLoginSuccess: (LoginSuccessData) -> Unit, loginViewModel: LoginViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState = loginViewModel.uiState

    // Validaciones locales para email y contraseña
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    // Expresión regular para validar el correo electrónico
    fun isEmailValid(email: String): Boolean {
        val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        return email.matches(Regex(emailPattern))
    }

    // Expresión regular para validar contraseñas
    fun isPasswordValid(password: String): Boolean {
        val passwordPattern = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*(),.?\":{}|<>_]).{6,}$"
        return password.matches(Regex(passwordPattern))
    }

    // Gestionar la navegación tras un inicio de sesión exitoso
    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            onLoginSuccess(uiState.data)
            loginViewModel.resetState() // Reset state after navigation
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "INICIO DE SESION")
        Spacer(modifier = Modifier.height(24.dp))

        // Email input con validación
        OutlinedTextField(
            value = email,
            onValueChange = { 
                email = it 
                emailError = if (isEmailValid(it)) "" else "Correo electrónico no válido"
            },
            label = { Text("Correo Electronico o usuario") },
            isError = emailError.isNotEmpty()
        )
        if (emailError.isNotEmpty()) {
            Text(text = emailError, color = Color.Red, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Password input con validación
        OutlinedTextField(
            value = password,
            onValueChange = { 
                password = it 
                passwordError = if (isPasswordValid(it)) "" else "La contraseña debe tener al menos 6 caracteres, una mayúscula, un número y un carácter especial"
            },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = passwordError.isNotEmpty()
        )
        if (passwordError.isNotEmpty()) {
            Text(text = passwordError, color = Color.Red, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState is LoginUiState.Loading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { 
                    if (emailError.isEmpty() && passwordError.isEmpty()) {
                        loginViewModel.login(email.trim(), password.trim()) // Trim whitespace
                    }
                }, 
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF061B2E),
                    contentColor = Color.White
                ),
                enabled = emailError.isEmpty() && passwordError.isEmpty()
            ) {
                Text("INICIAR")
            }
        }

        if (uiState is LoginUiState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = uiState.message, color = Color.Red)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    SistemaDeProyectosUAQTheme {
        LoginScreen(onLoginSuccess = {})
    }
}