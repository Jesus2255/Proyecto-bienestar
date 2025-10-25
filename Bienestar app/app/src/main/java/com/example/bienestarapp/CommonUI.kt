package com.example.bienestarapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

/**
 * Una barra de aplicación superior reutilizable con un título y un botón de navegación opcional.
 * @param title El texto que se mostrará como título.
 * @param canNavigateBack Si es true, muestra un botón de flecha para ir hacia atrás.
 * @param onNavigateUp La acción a ejecutar cuando se presiona el botón de atrás.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppToolBar(
    title: String,
    canNavigateBack: Boolean,
    onNavigateUp: () -> Unit = {}
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar"
                    )
                }
            }
        }
    )
}

@Preview
@Composable
fun AppToolBarPreview() {
    AppToolBar(title = "Mi Pantalla", canNavigateBack = true)
}
