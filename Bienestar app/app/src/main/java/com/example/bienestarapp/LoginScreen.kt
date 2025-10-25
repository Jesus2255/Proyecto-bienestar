package com.example.bienestarapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

// Le añadimos un nuevo parámetro: una función que se llamará cuando el login sea exitoso.
@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit // <-- ¡NUEVO!
) {
    val username = loginViewModel.username
    val password = loginViewModel.password
    val loginStatus = loginViewModel.loginStatus
    val navigateToHome = loginViewModel.navigateToHome

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        // ... Los OutlinedTextField para usuario y contraseña no cambian ...
        OutlinedTextField(
            value = username,
            onValueChange = { loginViewModel.onUsernameChange(it) },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { loginViewModel.onPasswordChange(it) },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (loginStatus.isNotEmpty()) {
            Text(text = loginStatus)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { loginViewModel.performLogin() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar")
        }
    }

    // Este "LaunchedEffect" escucha los cambios en `navigateToHome`.
    // Cuando cambia a `true`, ejecuta la navegación.
    LaunchedEffect(navigateToHome) {
        if (navigateToHome) {
            onLoginSuccess() // Llama a la función que nos pasó MainActivity.
            loginViewModel.onNavigationDone() // Resetea el estado de navegación.
        }
    }
}

