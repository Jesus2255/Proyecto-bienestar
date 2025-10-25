package com.example.bienestarapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun ClientesScreen(
    navController: NavController,
    viewModel: ClientesViewModel = viewModel()
) {
    var showDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar Snackbar cuando hay un mensaje de operación
    LaunchedEffect(viewModel.operationStatus) {
        if (viewModel.operationStatus.isNotEmpty()) {
            snackbarHostState.showSnackbar(
                message = viewModel.operationStatus,
                duration = SnackbarDuration.Short
            )
            viewModel.clearStatus()
        }
    }

    Scaffold(
        topBar = {
            AppToolBar(
                title = "Clientes",
                canNavigateBack = true,
                onNavigateUp = { navController.navigateUp() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.clearForm()
                    viewModel.cancelEditing()
                    showDialog = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Cliente")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val state = viewModel.uiState) {
                is CrudUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is CrudUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.items) { cliente ->
                            ClienteItem(
                                cliente = cliente,
                                onEdit = {
                                    viewModel.startEditing(cliente)
                                    showDialog = true
                                },
                                onDelete = { viewModel.deleteCliente(cliente.id) }
                            )
                        }
                    }
                }
                is CrudUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.message)
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { viewModel.fetchClientes() }) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
            }
        }

        if (showDialog) {
            ClienteDialog(
                viewModel = viewModel,
                onDismiss = {
                    showDialog = false
                    viewModel.cancelEditing()
                },
                onSave = {
                    val cliente = Cliente(
                        id = viewModel.currentCliente?.id ?: 0,
                        nombre = viewModel.nombre,
                        email = viewModel.email,
                        telefono = viewModel.telefono,
                        direccion = viewModel.direccion
                    )
                    
                    if (viewModel.isEditing && viewModel.currentCliente != null) {
                        viewModel.updateCliente(viewModel.currentCliente!!.id, cliente) {
                            showDialog = false
                        }
                    } else {
                        viewModel.createCliente(cliente) {
                            showDialog = false
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun ClienteItem(
    cliente: Cliente,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cliente.nombre,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = cliente.email,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = cliente.telefono,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "Editar")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Eliminar")
                }
            }
        }
    }
}

@Composable
fun ClienteDialog(
    viewModel: ClientesViewModel,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(if (viewModel.isEditing) "Editar Cliente" else "Nuevo Cliente")
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = viewModel.nombre,
                    onValueChange = { viewModel.onNombreChange(it) },
                    label = { Text("Nombre *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = viewModel.nombreError != null,
                    supportingText = {
                        viewModel.nombreError?.let { error ->
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    enabled = !viewModel.isLoading
                )
                
                OutlinedTextField(
                    value = viewModel.email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = { Text("Email *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = viewModel.emailError != null,
                    supportingText = {
                        viewModel.emailError?.let { error ->
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    enabled = !viewModel.isLoading
                )
                
                OutlinedTextField(
                    value = viewModel.telefono,
                    onValueChange = { viewModel.onTelefonoChange(it) },
                    label = { Text("Teléfono *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = viewModel.telefonoError != null,
                    supportingText = {
                        viewModel.telefonoError?.let { error ->
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    enabled = !viewModel.isLoading
                )
                
                OutlinedTextField(
                    value = viewModel.direccion,
                    onValueChange = { viewModel.onDireccionChange(it) },
                    label = { Text("Dirección") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !viewModel.isLoading
                )

                Text(
                    text = "* Campos obligatorios",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = viewModel.isFormValid() && !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(if (viewModel.isLoading) "Guardando..." else "Guardar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !viewModel.isLoading
            ) {
                Text("Cancelar")
            }
        }
    )
}
