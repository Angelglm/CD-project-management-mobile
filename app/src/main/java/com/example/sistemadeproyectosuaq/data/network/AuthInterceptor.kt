package com.example.sistemadeproyectosuaq.data.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Intercepts network requests to add the Authorization header if a token is available.
 */
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = SessionManager.userToken

        val requestBuilder = originalRequest.newBuilder()
        if (token != null) {
            requestBuilder.addHeader("Authorization", "bearer $token")
        }

        val newRequest = requestBuilder.build()
        return chain.proceed(newRequest)
    }
}
