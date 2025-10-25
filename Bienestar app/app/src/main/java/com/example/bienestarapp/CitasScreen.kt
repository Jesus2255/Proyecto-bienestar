package com.example.bienestarapp

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
fun CitasScreen(
    navController: NavController,
    viewModel: CitasViewModel = viewModel()
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
                title = "Citas",
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
                Icon(Icons.Default.Add, contentDescription = "Agregar Cita")
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
                        items(state.items) { cita ->
                            CitaItem(
                                cita = cita,
                                clientes = viewModel.clientes,
                                servicios = viewModel.servicios,
                                onEdit = {
                                    viewModel.startEditing(cita)
                                    showDialog = true
                                },
                                onDelete = { viewModel.deleteCita(cita.id) }
                            )
                        }
                    }
                }
                is CrudUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.message)
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { viewModel.fetchCitas() }) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
            }
        }

        if (showDialog) {
            CitaDialog(
                viewModel = viewModel,
                onDismiss = {
                    showDialog = false
                    viewModel.cancelEditing()
                },
                onSave = {
                    val cita = Cita(
                        id = viewModel.currentCita?.id ?: 0,
                        clienteId = viewModel.selectedClienteId!!,
                        servicioId = viewModel.selectedServicioId!!,
                        fecha = viewModel.fecha,
                        hora = viewModel.hora,
                        estado = viewModel.estado,
                        notas = viewModel.notas
                    )
                    
                    if (viewModel.isEditing && viewModel.currentCita != null) {
                        viewModel.updateCita(viewModel.currentCita!!.id, cita) {
                            showDialog = false
                        }
                    } else {
                        viewModel.createCita(cita) {
                            showDialog = false
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun CitaItem(
    cita: Cita,
    clientes: List<Cliente>,
    servicios: List<Servicio>,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val cliente = clientes.find { it.id == cita.clienteId }
    val servicio = servicios.find { it.id == cita.servicioId }
    
    val estadoColor = when (cita.estado) {
        "CONFIRMADA" -> MaterialTheme.colorScheme.primary
        "PENDIENTE" -> MaterialTheme.colorScheme.secondary
        "CANCELADA" -> MaterialTheme.colorScheme.error
        "COMPLETADA" -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.onSurface
    }

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
                    text = cliente?.nombre ?: "Cliente no encontrado",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = servicio?.nombre ?: "Servicio no encontrado",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${cita.fecha} a las ${cita.hora}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Estado: ${cita.estado}",
                    style = MaterialTheme.typography.bodySmall,
                    color = estadoColor
                )
                if (!cita.notas.isNullOrBlank()) {
                    Text(
                        text = "Notas: ${cita.notas}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitaDialog(
    viewModel: CitasViewModel,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    var expandedClientes by remember { mutableStateOf(false) }
    var expandedServicios by remember { mutableStateOf(false) }
    var expandedEstados by remember { mutableStateOf(false) }
    
    val estados = listOf("PENDIENTE", "CONFIRMADA", "COMPLETADA", "CANCELADA")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(if (viewModel.isEditing) "Editar Cita" else "Nueva Cita")
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Selector de Cliente
                ExposedDropdownMenuBox(
                    expanded = expandedClientes,
                    onExpandedChange = { expandedClientes = !expandedClientes && !viewModel.isLoading }
                ) {
                    OutlinedTextField(
                        value = viewModel.clientes.find { it.id == viewModel.selectedClienteId }?.nombre ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Cliente *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedClientes) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        isError = viewModel.clienteError != null,
                        supportingText = {
                            viewModel.clienteError?.let { error ->
                                Text(text = error, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        enabled = !viewModel.isLoading
                    )
                    ExposedDropdownMenu(
                        expanded = expandedClientes,
                        onDismissRequest = { expandedClientes = false }
                    ) {
                        viewModel.clientes.forEach { cliente ->
                            DropdownMenuItem(
                                text = { Text(cliente.nombre) },
                                onClick = {
                                    viewModel.onClienteSelected(cliente.id)
                                    expandedClientes = false
                                }
                            )
                        }
                    }
                }

                // Selector de Servicio
                ExposedDropdownMenuBox(
                    expanded = expandedServicios,
                    onExpandedChange = { expandedServicios = !expandedServicios && !viewModel.isLoading }
                ) {
                    OutlinedTextField(
                        value = viewModel.servicios.find { it.id == viewModel.selectedServicioId }?.nombre ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Servicio *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedServicios) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        isError = viewModel.servicioError != null,
                        supportingText = {
                            viewModel.servicioError?.let { error ->
                                Text(text = error, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        enabled = !viewModel.isLoading
                    )
                    ExposedDropdownMenu(
                        expanded = expandedServicios,
                        onDismissRequest = { expandedServicios = false }
                    ) {
                        viewModel.servicios.forEach { servicio ->
                            DropdownMenuItem(
                                text = { Text(servicio.nombre) },
                                onClick = {
                                    viewModel.onServicioSelected(servicio.id)
                                    expandedServicios = false
                                }
                            )
                        }
                    }
                }

                // Campo de Fecha
                OutlinedTextField(
                    value = viewModel.fecha,
                    onValueChange = { viewModel.onFechaChange(it) },
                    label = { Text("Fecha (YYYY-MM-DD) *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = viewModel.fechaError != null,
                    supportingText = {
                        viewModel.fechaError?.let { error ->
                            Text(text = error, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    placeholder = { Text("2025-10-25") },
                    enabled = !viewModel.isLoading
                )

                // Campo de Hora
                OutlinedTextField(
                    value = viewModel.hora,
                    onValueChange = { viewModel.onHoraChange(it) },
                    label = { Text("Hora (HH:MM) *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = viewModel.horaError != null,
                    supportingText = {
                        viewModel.horaError?.let { error ->
                            Text(text = error, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    placeholder = { Text("14:30") },
                    enabled = !viewModel.isLoading
                )

                // Selector de Estado
                ExposedDropdownMenuBox(
                    expanded = expandedEstados,
                    onExpandedChange = { expandedEstados = !expandedEstados && !viewModel.isLoading }
                ) {
                    OutlinedTextField(
                        value = viewModel.estado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Estado") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEstados) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        enabled = !viewModel.isLoading
                    )
                    ExposedDropdownMenu(
                        expanded = expandedEstados,
                        onDismissRequest = { expandedEstados = false }
                    ) {
                        estados.forEach { estado ->
                            DropdownMenuItem(
                                text = { Text(estado) },
                                onClick = {
                                    viewModel.onEstadoChange(estado)
                                    expandedEstados = false
                                }
                            )
                        }
                    }
                }

                // Campo de Notas
                OutlinedTextField(
                    value = viewModel.notas,
                    onValueChange = { viewModel.onNotasChange(it) },
                    label = { Text("Notas") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
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
