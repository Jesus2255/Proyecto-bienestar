package com.example.bienestarapp

import okhttp3.JavaNetCookieJar // <-- ¡NUEVA IMPORTACIÓN!
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager // <-- ¡NUEVA IMPORTACIÓN!
import java.util.concurrent.TimeUnit

object NetworkModule {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    // --- ¡NUEVO! ---
    // 1. Creamos un gestor de cookies. Esto almacenará las cookies en memoria.
    private val cookieManager = CookieManager()
    private val cookieJar = JavaNetCookieJar(cookieManager)
    // --- FIN DE LO NUEVO ---

    // --- ¡CAMBIO! ---
    // 2. Modificamos el cliente OkHttp para que use nuestro gestor de cookies.
    private val okHttpClient = OkHttpClient.Builder()
        .cookieJar(cookieJar) // <-- ¡LÍNEA CLAVE! Le decimos a OkHttp que use el CookieJar.
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    // --- FIN DEL CAMBIO ---

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Usamos el cliente OkHttp ya configurado con cookies.
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
    