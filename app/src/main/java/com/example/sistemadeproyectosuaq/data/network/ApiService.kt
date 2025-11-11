package com.example.sistemadeproyectosuaq.data.network

import android.annotation.SuppressLint
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

private const val BASE_URL = "https://pmaster.elcilantro.site/api/"

// --- Development Only: Unsafe OkHttpClient to trust all certificates ---
// WARNING: This is insecure and should not be used in production.
@SuppressLint("CustomX509TrustManager")
private fun createUnsafeOkHttpClient(): OkHttpClient {
    val trustAllCerts = arrayOf<TrustManager>(
        object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        }
    )

    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, trustAllCerts, SecureRandom())

    return OkHttpClient.Builder()
        .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
        .hostnameVerifier { _, _ -> true }
        .connectTimeout(30, TimeUnit.SECONDS) // Added connect timeout
        .readTimeout(30, TimeUnit.SECONDS)    // Added read timeout
        .writeTimeout(30, TimeUnit.SECONDS)   // Added write timeout
        .build()
}
// ----------------------------------------------------------------------

private val json = Json { ignoreUnknownKeys = true }

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(createUnsafeOkHttpClient()) // Use the unsafe client
    .addConverterFactory(json.asConverterFactory(MediaType.parse("application/json")!!))
    .build()

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("login/getToken")
    suspend fun getToken(@Header("user_key") userKey: String): TokenResponse
}

object ApiClient {
    val service: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
