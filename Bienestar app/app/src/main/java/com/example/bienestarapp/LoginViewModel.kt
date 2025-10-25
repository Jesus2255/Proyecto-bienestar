package com.example.bienestarapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    // --- ESTADO DE LA UI ---
    // El ViewModel es el "dueño" de estos datos. La UI solo los lee.
    var username by mutableStateOf("")
        private set // 'private set' asegura que solo el ViewModel pueda cambiar este valor.

    var password by mutableStateOf("")
        private set

    var loginStatus by mutableStateOf("") // Mensaje para el usuario (Cargando, Éxito, Error)
        private set

    // --- ¡NUEVO! ---
    // Variable que le dice a la UI que es momento de navegar.
    var navigateToHome by mutableStateOf(false)
        private set
    // --- FIN DE LO NUEVO ---

    // --- EVENTOS DE LA UI ---
    // Funciones que la UI llama para notificar al ViewModel sobre acciones del usuario.
    fun onUsernameChange(newUsername: String) {
        username = newUsername
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    // --- ¡NUEVO! ---
    // Función para que la UI nos avise que ya ha navegado.
    fun onNavigationDone() {
        navigateToHome = false
    }
    // --- FIN DE LO NUEVO ---

    /**
     * Ejecuta la lógica para iniciar sesión en una coroutine segura.
     */
    fun performLogin() {
        // viewModelScope es la forma correcta de lanzar coroutines desde un ViewModel.
        // Se cancelan automáticamente si el usuario sale de la pantalla.
        viewModelScope.launch {
            try {
                loginStatus = "Cargando..."
                val response = NetworkModule.api.login(username, password)

                if (response.isSuccessful) {
                    // Obtener información del usuario incluyendo el rol
                    try {
                        val userInfo = NetworkModule.api.getUserInfo()
                        DebugUtils.logApiResponse("getUserInfo", userInfo)
                        
                        // Determinar el rol del usuario
                        val role = when {
                            userInfo.role?.contains("ADMIN", ignoreCase = true) == true -> UserRole.ADMIN
                            userInfo.role?.contains("USER", ignoreCase = true) == true -> UserRole.USER
                            userInfo.role?.contains("CLIENT", ignoreCase = true) == true -> UserRole.USER
                            else -> UserRole.USER // Por defecto, usuario normal
                        }
                        
                        // Guardar la sesión del usuario (userId puede ser 0L por ahora)
                        UserSession.login(username, role, 0L)
                        DebugUtils.logUserSession()
                        
                        loginStatus = "¡Login exitoso! Bienvenido ${if (role == UserRole.ADMIN) "Administrador" else "Usuario"}"
                    } catch (e: Exception) {
                        // Si falla obtener el rol, asumimos usuario normal
                        UserSession.login(username, UserRole.USER, 0L)
                        DebugUtils.logUserSession()
                        loginStatus = "¡Login exitoso!"
                        e.printStackTrace()
                    }
                    
                    // Activamos la navegación
                    navigateToHome = true
                } else {
                    // El servidor respondió con un error (ej: 401 Unauthorized)
                    loginStatus = "Error: Usuario o contraseña incorrectos."
                }
            } catch (e: Exception) {
                // No se pudo conectar al servidor (IP incorrecta, sin WiFi, servidor caído)
                loginStatus = "Error de conexión: No se pudo contactar al servidor."
                e.printStackTrace() // Imprime el error detallado en el Logcat para depuración
            }
        }
    }
}
