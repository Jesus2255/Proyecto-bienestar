package com.example.bienestarapp

data class Cliente(
    val id: Long = 0,
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val direccion: String = ""
)

data class Servicio(
    val id: Long = 0,
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val duracion: Int = 0 // Duración en minutos
)

data class Cita(
    val id: Long = 0,
    val clienteId: Long = 0,
    val servicioId: Long = 0,
    val fecha: String = "", // Formato: "yyyy-MM-dd"
    val hora: String = "", // Formato: "HH:mm"
    val estado: String = "PENDIENTE", // PENDIENTE, CONFIRMADA, CANCELADA, COMPLETADA
    val notas: String = "",
    // Campos adicionales para mostrar info relacionada
    val clienteNombre: String? = null,
    val servicioNombre: String? = null
)

// Modelo para la respuesta de login que incluye el rol
data class LoginResponse(
    val success: Boolean,
    val message: String? = null,
    val role: String? = null // "ADMIN" o "USER"
)

// Enumeración de roles
enum class UserRole {
    ADMIN,
    USER,
    GUEST
}
