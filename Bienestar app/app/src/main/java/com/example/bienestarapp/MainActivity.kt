package com.example.bienestarapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bienestarapp.ui.theme.BienestarappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BienestarappTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 1. Creamos el controlador de navegación
                    val navController = rememberNavController()

                    // 2. NavHost define el grafo de navegación
                    NavHost(
                        navController = navController,
                        startDestination = "login" // La pantalla inicial es "login"
                    ) {
                        // 3. Definimos cada pantalla (ruta)
                        composable("login") {
                            LoginScreen(
                                // Le pasamos una función para que nos avise cuándo navegar
                                onLoginSuccess = {
                                    // Navega a "home" y limpia el historial para que
                                    // el usuario no pueda volver al login con el botón "atrás".
                                    navController.navigate("home") {
                                        popUpTo("login") {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        }

                        // --- CAMBIO CLAVE ---
                        // Ahora HomeScreen recibe el NavController para poder navegar a otras pantallas
                        composable("home") {
                            HomeScreen(navController = navController)
                        }

                        // --- NUEVAS RUTAS ---
                        composable("servicios") {
                            // Por ahora, esta pantalla mostrará los servicios.
                            // En el futuro, podría tener su propio ViewModel.
                            ServiciosScreen(navController = navController) // Pasar navController
                        }
                        composable("clientes") {
                            // Pantalla CRUD completa para clientes
                            ClientesScreen(navController = navController)
                        }
                        composable("citas") {
                            // Pantalla CRUD completa para citas
                            CitasScreen(navController = navController)
                        }
                        // --- FIN DE NUEVAS RUTAS ---
                    }
                }
            }
        }
    }
}