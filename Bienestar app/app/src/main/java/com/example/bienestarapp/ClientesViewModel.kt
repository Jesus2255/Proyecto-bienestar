package com.example.bienestarapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// Estados genéricos para CRUD
sealed interface CrudUiState<T> {
    data class Success<T>(val items: List<T>) : CrudUiState<T>
    data class Error<T>(val message: String) : CrudUiState<T>
    class Loading<T> : CrudUiState<T>
}

// ViewModel para Clientes
class ClientesViewModel : ViewModel() {
    var uiState: CrudUiState<Cliente> by mutableStateOf(CrudUiState.Loading())
        private set

    var operationStatus by mutableStateOf("")
        private set

    var isEditing by mutableStateOf(false)
        private set

    var currentCliente by mutableStateOf<Cliente?>(null)
        private set

    // --- NUEVO: Estados de carga ---
    var isLoading by mutableStateOf(false)
        private set

    // --- NUEVO: Estados de validación para formulario ---
    var nombreError by mutableStateOf<String?>(null)
        private set
    
    var emailError by mutableStateOf<String?>(null)
        private set
    
    var telefonoError by mutableStateOf<String?>(null)
        private set

    // --- NUEVO: Campos del formulario ---
    var nombre by mutableStateOf("")
        private set
    
    var email by mutableStateOf("")
        private set
    
    var telefono by mutableStateOf("")
        private set
    
    var direccion by mutableStateOf("")
        private set

    init {
        fetchClientes()
    }

    // --- NUEVO: Funciones de validación en tiempo real ---
    fun onNombreChange(newNombre: String) {
        nombre = newNombre
        nombreError = when {
            newNombre.isBlank() -> "El nombre no puede estar vacío"
            newNombre.length < 3 -> "El nombre debe tener al menos 3 caracteres"
            else -> null
        }
    }

    fun onEmailChange(newEmail: String) {
        email = newEmail
        emailError = when {
            newEmail.isBlank() -> "El email no puede estar vacío"
            !newEmail.contains("@") || !newEmail.contains(".") -> "Email inválido"
            else -> null
        }
    }

    fun onTelefonoChange(newTelefono: String) {
        telefono = newTelefono
        telefonoError = when {
            newTelefono.isBlank() -> "El teléfono no puede estar vacío"
            newTelefono.length < 8 -> "El teléfono debe tener al menos 8 dígitos"
            else -> null
        }
    }

    fun onDireccionChange(newDireccion: String) {
        direccion = newDireccion
    }

    // Valida todo el formulario
    fun isFormValid(): Boolean {
        return nombreError == null && 
               emailError == null && 
               telefonoError == null &&
               nombre.isNotBlank() &&
               email.isNotBlank() &&
               telefono.isNotBlank()
    }

    // Carga datos de un cliente en el formulario
    fun loadClienteToForm(cliente: Cliente) {
        nombre = cliente.nombre
        email = cliente.email
        telefono = cliente.telefono
        direccion = cliente.direccion ?: ""
        // Re-validar
        onNombreChange(nombre)
        onEmailChange(email)
        onTelefonoChange(telefono)
    }

    // Limpia el formulario
    fun clearForm() {
        nombre = ""
        email = ""
        telefono = ""
        direccion = ""
        nombreError = null
        emailError = null
        telefonoError = null
    }

    fun fetchClientes() {
        viewModelScope.launch {
            uiState = CrudUiState.Loading()
            try {
                val clientes = NetworkModule.api.getClientes()
                uiState = CrudUiState.Success(clientes)
            } catch (e: Exception) {
                uiState = CrudUiState.Error("Error al cargar clientes: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun createCliente(cliente: Cliente, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                operationStatus = "Guardando..."
                NetworkModule.api.createCliente(cliente)
                operationStatus = "✓ Cliente creado exitosamente"
                fetchClientes()
                clearForm()
                onSuccess()
            } catch (e: Exception) {
                operationStatus = "✗ Error al crear cliente: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun updateCliente(id: Long, cliente: Cliente, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                operationStatus = "Actualizando..."
                NetworkModule.api.updateCliente(id, cliente)
                operationStatus = "✓ Cliente actualizado exitosamente"
                fetchClientes()
                clearForm()
                onSuccess()
            } catch (e: Exception) {
                operationStatus = "✗ Error al actualizar cliente: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteCliente(id: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                operationStatus = "Eliminando..."
                NetworkModule.api.deleteCliente(id)
                operationStatus = "✓ Cliente eliminado exitosamente"
                fetchClientes()
            } catch (e: Exception) {
                operationStatus = "✗ Error al eliminar cliente: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun startEditing(cliente: Cliente) {
        currentCliente = cliente
        isEditing = true
        loadClienteToForm(cliente)
    }

    fun cancelEditing() {
        currentCliente = null
        isEditing = false
        operationStatus = ""
        clearForm()
    }

    fun clearStatus() {
        operationStatus = ""
    }
}
