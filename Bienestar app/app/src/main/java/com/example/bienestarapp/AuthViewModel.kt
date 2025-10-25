package com.example.bienestarapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = NetworkModule.api.logout()
                if (response.isSuccessful) {
                    onLogoutSuccess()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Incluso si la llamada falla (ej. sin red), forzamos el logout en el cliente
                onLogoutSuccess()
            }
        }
    }
}
