package com.example.bienestarapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// ViewModel para Citas
class CitasViewModel : ViewModel() {
    var uiState: CrudUiState<Cita> by mutableStateOf(CrudUiState.Loading())
        private set

    var operationStatus by mutableStateOf("")
        private set

    var isEditing by mutableStateOf(false)
        private set

    var currentCita by mutableStateOf<Cita?>(null)
        private set

    // Listas para los selectores en el formulario
    var clientes by mutableStateOf<List<Cliente>>(emptyList())
        private set

    var servicios by mutableStateOf<List<Servicio>>(emptyList())
        private set

    // --- NUEVO: Estados de carga ---
    var isLoading by mutableStateOf(false)
        private set

    // --- NUEVO: Campos del formulario ---
    var selectedClienteId by mutableStateOf<Long?>(null)
        private set
    
    var selectedServicioId by mutableStateOf<Long?>(null)
        private set
    
    var fecha by mutableStateOf("")
        private set
    
    var hora by mutableStateOf("")
        private set
    
    var estado by mutableStateOf("PENDIENTE")
        private set
    
    var notas by mutableStateOf("")
        private set

    // --- NUEVO: Estados de validación ---
    var clienteError by mutableStateOf<String?>(null)
        private set
    
    var servicioError by mutableStateOf<String?>(null)
        private set
    
    var fechaError by mutableStateOf<String?>(null)
        private set
    
    var horaError by mutableStateOf<String?>(null)
        private set

    init {
        fetchCitas()
        fetchClientesYServicios()
    }

    // --- NUEVO: Funciones de validación ---
    fun onClienteSelected(clienteId: Long?) {
        selectedClienteId = clienteId
        clienteError = if (clienteId == null) "Debe seleccionar un cliente" else null
    }

    fun onServicioSelected(servicioId: Long?) {
        selectedServicioId = servicioId
        servicioError = if (servicioId == null) "Debe seleccionar un servicio" else null
    }

    fun onFechaChange(newFecha: String) {
        fecha = newFecha
        fechaError = when {
            newFecha.isBlank() -> "La fecha no puede estar vacía"
            !isValidDate(newFecha) -> "Formato de fecha inválido (use YYYY-MM-DD)"
            else -> null
        }
    }

    fun onHoraChange(newHora: String) {
        hora = newHora
        horaError = when {
            newHora.isBlank() -> "La hora no puede estar vacía"
            !isValidTime(newHora) -> "Formato de hora inválido (use HH:MM)"
            else -> null
        }
    }

    fun onEstadoChange(newEstado: String) {
        estado = newEstado
    }

    fun onNotasChange(newNotas: String) {
        notas = newNotas
    }

    // Validaciones de formato
    private fun isValidDate(date: String): Boolean {
        return date.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))
    }

    private fun isValidTime(time: String): Boolean {
        return time.matches(Regex("\\d{2}:\\d{2}"))
    }

    // Valida todo el formulario
    fun isFormValid(): Boolean {
        return selectedClienteId != null &&
               selectedServicioId != null &&
               clienteError == null &&
               servicioError == null &&
               fechaError == null &&
               horaError == null &&
               fecha.isNotBlank() &&
               hora.isNotBlank()
    }

    // Carga datos de una cita en el formulario
    fun loadCitaToForm(cita: Cita) {
        selectedClienteId = cita.clienteId
        selectedServicioId = cita.servicioId
        fecha = cita.fecha
        hora = cita.hora
        estado = cita.estado
        notas = cita.notas ?: ""
        // Re-validar
        onClienteSelected(selectedClienteId)
        onServicioSelected(selectedServicioId)
        onFechaChange(fecha)
        onHoraChange(hora)
    }

    // Limpia el formulario
    fun clearForm() {
        selectedClienteId = null
        selectedServicioId = null
        fecha = ""
        hora = ""
        estado = "PENDIENTE"
        notas = ""
        clienteError = null
        servicioError = null
        fechaError = null
        horaError = null
    }

    fun fetchCitas() {
        viewModelScope.launch {
            uiState = CrudUiState.Loading()
            try {
                val citas = NetworkModule.api.getCitas()
                uiState = CrudUiState.Success(citas)
            } catch (e: Exception) {
                uiState = CrudUiState.Error("Error al cargar citas: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun fetchClientesYServicios() {
        viewModelScope.launch {
            try {
                clientes = NetworkModule.api.getClientes()
                servicios = NetworkModule.api.getServicios()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun createCita(cita: Cita, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                operationStatus = "Guardando..."
                NetworkModule.api.createCita(cita)
                operationStatus = "✓ Cita creada exitosamente"
                fetchCitas()
                clearForm()
                onSuccess()
            } catch (e: Exception) {
                operationStatus = "✗ Error al crear cita: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun updateCita(id: Long, cita: Cita, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                operationStatus = "Actualizando..."
                NetworkModule.api.updateCita(id, cita)
                operationStatus = "✓ Cita actualizada exitosamente"
                fetchCitas()
                clearForm()
                onSuccess()
            } catch (e: Exception) {
                operationStatus = "✗ Error al actualizar cita: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteCita(id: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                operationStatus = "Eliminando..."
                NetworkModule.api.deleteCita(id)
                operationStatus = "✓ Cita eliminada exitosamente"
                fetchCitas()
            } catch (e: Exception) {
                operationStatus = "✗ Error al eliminar cita: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun startEditing(cita: Cita) {
        currentCita = cita
        isEditing = true
        loadCitaToForm(cita)
    }

    fun cancelEditing() {
        currentCita = null
        isEditing = false
        operationStatus = ""
        clearForm()
    }

    fun clearStatus() {
        operationStatus = ""
    }
}
