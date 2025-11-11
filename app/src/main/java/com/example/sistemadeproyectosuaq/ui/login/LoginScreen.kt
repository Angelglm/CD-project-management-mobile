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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sistemadeproyectosuaq.ui.theme.SistemaDeProyectosUAQTheme

@Composable
fun LoginScreen(onLoginSuccess: (LoginSuccessData) -> Unit, loginViewModel: LoginViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState = loginViewModel.uiState

    // Handle navigation on successful login
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

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electronico o usuario") },
            isError = uiState is LoginUiState.Error
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = uiState is LoginUiState.Error
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState is LoginUiState.Loading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { loginViewModel.login(email, password) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF061B2E)
                )
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
        // In preview, we don't have a real ViewModel, so we pass a dummy lambda.
        LoginScreen(onLoginSuccess = {})
    }
}
