package com.example.sistemadeproyectosuaq.data.network

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * A simple object to manage the user's session token.
 * In a real-world app, this should be stored securely (e.g., EncryptedSharedPreferences).
 */
object SessionManager {
    var userToken: String? by mutableStateOf(null)
        private set

    fun onLoginSuccess(token: String) {
        userToken = token
    }

    fun onLogout() {
        userToken = null
    }
}