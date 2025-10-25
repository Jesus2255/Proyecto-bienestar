package com.example.bienestarapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Singleton para manejar la sesión del usuario en toda la aplicación.
 * Almacena información del usuario después del login.
 */
object UserSession {
    var username by mutableStateOf("")
        private set
    
    var userRole by mutableStateOf(UserRole.GUEST)
        private set
    
    var userId by mutableStateOf(0L)
        private set
    
    var isAuthenticated by mutableStateOf(false)
        private set
    
    /**
     * Inicializa la sesión del usuario después de un login exitoso.
     */
    fun login(username: String, role: UserRole, userId: Long = 0L) {
        this.username = username
        this.userRole = role
        this.userId = userId
        this.isAuthenticated = true
    }
    
    /**
     * Limpia la sesión del usuario al cerrar sesión.
     */
    fun logout() {
        username = ""
        userRole = UserRole.GUEST
        userId = 0L
        isAuthenticated = false
    }
    
    /**
     * Verifica si el usuario actual es administrador.
     */
    fun isAdmin(): Boolean = userRole == UserRole.ADMIN
    
    /**
     * Verifica si el usuario actual es un cliente regular.
     */
    fun isClient(): Boolean = userRole == UserRole.USER
    
    /**
     * Verifica si el usuario tiene permisos de administrador o superior.
     */
    fun hasAdminPermissions(): Boolean = isAdmin()
    
    /**
     * Verifica si el usuario está autenticado (no es GUEST).
     */
    fun isLoggedIn(): Boolean = isAuthenticated && userRole != UserRole.GUEST
}
