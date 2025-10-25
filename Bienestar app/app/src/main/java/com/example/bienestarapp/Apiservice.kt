package com.example.bienestarapp

import retrofit2.Response
import retrofit2.http.*

/**
 * Define los endpoints (las URLs específicas) de tu API para que Retrofit las use.
 */
interface ApiService {

    // ==================== AUTENTICACIÓN ====================
    /**
     * Realiza el login usando la codificación de formulario que espera Spring Security.
     * @param username El nombre de usuario.
     * @param password La contraseña.
     * @return Una respuesta vacía. Un código 200 (OK) significa login exitoso.
     */
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<Unit>

    @POST("logout")
    suspend fun logout(): Response<Unit>

    /**
     * Obtiene información del usuario autenticado, incluyendo su rol.
     */
    @GET("api/auth/user-info")
    suspend fun getUserInfo(): LoginResponse

    // ==================== SERVICIOS ====================
    @GET("api/servicios")
    suspend fun getServicios(): List<Servicio>

    @GET("api/servicios/{id}")
    suspend fun getServicio(@Path("id") id: Long): Servicio

    @POST("api/servicios")
    suspend fun createServicio(@Body servicio: Servicio): Servicio

    @PUT("api/servicios/{id}")
    suspend fun updateServicio(@Path("id") id: Long, @Body servicio: Servicio): Servicio

    @DELETE("api/servicios/{id}")
    suspend fun deleteServicio(@Path("id") id: Long): Response<Unit>

    // ==================== CLIENTES ====================
    @GET("api/clientes")
    suspend fun getClientes(): List<Cliente>

    @GET("api/clientes/{id}")
    suspend fun getCliente(@Path("id") id: Long): Cliente

    @POST("api/clientes")
    suspend fun createCliente(@Body cliente: Cliente): Cliente

    @PUT("api/clientes/{id}")
    suspend fun updateCliente(@Path("id") id: Long, @Body cliente: Cliente): Cliente

    @DELETE("api/clientes/{id}")
    suspend fun deleteCliente(@Path("id") id: Long): Response<Unit>

    // ==================== CITAS ====================
    @GET("api/citas")
    suspend fun getCitas(): List<Cita>

    @GET("api/citas/{id}")
    suspend fun getCita(@Path("id") id: Long): Cita

    @POST("api/citas")
    suspend fun createCita(@Body cita: Cita): Cita

    @PUT("api/citas/{id}")
    suspend fun updateCita(@Path("id") id: Long, @Body cita: Cita): Cita

    @DELETE("api/citas/{id}")
    suspend fun deleteCita(@Path("id") id: Long): Response<Unit>
}