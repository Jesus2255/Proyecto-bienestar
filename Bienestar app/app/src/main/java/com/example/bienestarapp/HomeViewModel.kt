package com.example.bienestarapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// Definimos los posibles estados de la pantalla
sealed interface HomeUiState {
    data class Success(val servicios: List<Servicio>) : HomeUiState
    data class Error(val message: String) : HomeUiState
    object Loading : HomeUiState
}

class HomeViewModel : ViewModel() {

    var uiState: HomeUiState by mutableStateOf(HomeUiState.Loading)
        private set

    init {
        // Carga los servicios tan pronto como el ViewModel es creado.
        fetchServicios()
    }

    private fun fetchServicios() {
        viewModelScope.launch {
            uiState = HomeUiState.Loading
            try {
                val servicios = NetworkModule.api.getServicios()
                uiState = HomeUiState.Success(servicios)
            } catch (e: Exception) {
                uiState = HomeUiState.Error("Error al cargar los servicios.")
                e.printStackTrace() // Imprime el error real en Logcat para depuración
            }
        }
    }
}
