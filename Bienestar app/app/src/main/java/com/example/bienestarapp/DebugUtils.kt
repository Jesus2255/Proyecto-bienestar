package com.example.bienestarapp

import android.util.Log

/**
 * Utilidades para debugging durante el desarrollo.
 */
object DebugUtils {
    private const val TAG = "BienestarApp"
    
    /**
     * Registra información de la sesión del usuario en el Logcat.
     */
    fun logUserSession() {
        Log.d(TAG, "=== USER SESSION INFO ===")
        Log.d(TAG, "Username: ${UserSession.username}")
        Log.d(TAG, "Role: ${UserSession.userRole}")
        Log.d(TAG, "Is Admin: ${UserSession.isAdmin()}")
        Log.d(TAG, "Is Authenticated: ${UserSession.isAuthenticated}")
        Log.d(TAG, "========================")
    }
    
    /**
     * Registra información de una respuesta de API.
     */
    fun logApiResponse(endpoint: String, response: Any?) {
        Log.d(TAG, "=== API RESPONSE ===")
        Log.d(TAG, "Endpoint: $endpoint")
        Log.d(TAG, "Response: $response")
        Log.d(TAG, "====================")
    }
}
