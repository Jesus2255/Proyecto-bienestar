package com.example.bienestarapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

// CAMBIO RADICAL: HomeScreen ahora es un menú y recibe NavController
// ACTUALIZADO: Muestra opciones según el rol del usuario (ADMIN vs USER)
@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel() // Inyectar el nuevo ViewModel
) {
    // Log de debugging para verificar el estado de la sesión
    LaunchedEffect(Unit) {
        DebugUtils.logUserSession()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mostrar mensaje personalizado según el rol
        val welcomeText = if (UserSession.isAdmin()) {
            "Panel de Administración"
        } else {
            "Panel Principal"
        }
        
        Text(text = welcomeText, style = MaterialTheme.typography.headlineLarge)
        
        // Mostrar el nombre de usuario y rol
        Text(
            text = "Bienvenido, ${UserSession.username ?: "Usuario"} (${if (UserSession.isAdmin()) "Administrador" else "Cliente"})",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(48.dp))

        // Botón para navegar a la lista de servicios (disponible para todos)
        Button(
            onClick = { navController.navigate("servicios") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver Servicios")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para navegar a la lista de clientes (SOLO PARA ADMINISTRADORES)
        if (UserSession.isAdmin()) {
            Button(
                onClick = { navController.navigate("clientes") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver Clientes")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Botón para navegar a la lista de citas (disponible para todos)
        Button(
            onClick = { navController.navigate("citas") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver Citas")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón para cerrar sesión
        Button(
            onClick = {
                authViewModel.logout {
                    // Limpia la sesión del usuario
                    UserSession.logout()
                    // Navega al login y limpia TODO el historial de navegación
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red) // Color distintivo
        ) {
            Text("Cerrar Sesión")
        }
    }
}

