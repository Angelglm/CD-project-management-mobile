@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
package com.example.sistemadeproyectosuaq.data.network

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class LoginResponse(
    val userID: Int,
    val userKey: String,
    val role: String,
    val tempPassword: Boolean
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class TokenResponse(
    val token: String
)
