package com.example.bienestarapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

// CAMBIO: El nombre del Composable ahora es ServiciosScreen
@Composable
fun ServiciosScreen(
    navController: NavController, // <-- AÑADIR NAVCONTROLLER
    homeViewModel: HomeViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            AppToolBar(
                title = "Servicios",
                canNavigateBack = true,
                onNavigateUp = { navController.navigateUp() } // Acción para regresar
            )
        }
    ) { innerPadding -> // El contenido de la pantalla debe ir dentro del lambda del Scaffold
        val uiState = homeViewModel.uiState
        Box(modifier = Modifier.padding(innerPadding)) { // Usar el padding que nos da Scaffold
            when (uiState) {
                is HomeUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is HomeUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.servicios) { servicio ->
                            ServicioItem(servicio)
                        }
                    }
                }
                is HomeUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = uiState.message)
                    }
                }
            }
        }
    }
}

// Este Composable de item no necesita cambios
@Composable
fun ServicioItem(servicio: Servicio) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = servicio.nombre, style = MaterialTheme.typography.titleMedium)
        Text(text = servicio.descripcion, style = MaterialTheme.typography.bodySmall)
    }
}
