package com.example.panol.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.panol.data.Constantes
import com.example.panol.ui.components.Header
import com.example.panol.ui.components.MensajeError
import com.example.panol.ui.theme.coloresBotonAccion
import com.example.panol.ui.viewmodel.SolicitudViewModel

// Pantalla del formulario de solicitud con validación por campo
@Composable
fun FormularioScreen(
    onVolver: () -> Unit,
    onEnviado: () -> Unit,
    viewModel: SolicitudViewModel = viewModel()
) {
    val context = LocalContext.current
    val state = viewModel.uiState

    var dialogoAbierto by remember { mutableStateOf("") }
    var menuSalaAbierto by remember { mutableStateOf(false) }

    // Temblor del formulario cuando hay errores
    val desplazamiento = remember { Animatable(0f) }
    LaunchedEffect(state.errorTrigger) {
        if (state.errorTrigger > 0) {
            for (valor in listOf(-12f, 12f, -8f, 8f, 0f)) {
                desplazamiento.animateTo(valor, tween(50))
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { Header(onVolver = onVolver) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .offset(x = desplazamiento.value.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "Formulario de solicitud",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            item {
                // Advertencia de la regla de 3 horas
                AnimatedVisibility(visible = state.advertenciaAnticipacion) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "La hora de inicio debe ser al menos ${Constantes.HORAS_ANTICIPACION} horas después de ahora. Elige otra hora o fecha.",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            item {
                Box {
                    CampoSelector(
                        etiqueta = "Sala",
                        valor = state.sala,
                        error = state.errorSala,
                        onClick = { menuSalaAbierto = true }
                    )
                    DropdownMenu(
                        expanded = menuSalaAbierto,
                        onDismissRequest = { menuSalaAbierto = false }
                    ) {
                        Constantes.SALAS.forEach { sala ->
                            DropdownMenuItem(
                                text = { Text(sala) },
                                onClick = {
                                    viewModel.onSalaChanged(sala)
                                    menuSalaAbierto = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                CampoSelector(
                    etiqueta = "Fecha",
                    valor = state.fechaTexto,
                    error = state.errorFecha,
                    onClick = { dialogoAbierto = "fecha" }
                )
            }
            item {
                CampoSelector(
                    etiqueta = "Hora de inicio",
                    valor = state.horaInicio,
                    error = state.errorHoraInicio,
                    onClick = { dialogoAbierto = "inicio" }
                )
            }
            item {
                CampoSelector(
                    etiqueta = "Hora estimada de devolución",
                    valor = state.horaDevolucion,
                    error = state.errorHoraDevolucion,
                    onClick = { dialogoAbierto = "devolucion" }
                )
            }

            item {
                OutlinedTextField(
                    value = state.asignatura,
                    onValueChange = { viewModel.onAsignaturaChanged(it) },
                    label = { Text("Asignatura (opcional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = state.observaciones,
                    onValueChange = { viewModel.onObservacionesChanged(it) },
                    label = { Text("Observaciones (${state.observaciones.length}/200)") },
                    isError = state.errorObservaciones.isNotEmpty(),
                    supportingText = { MensajeError(state.errorObservaciones) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Button(
                    onClick = { viewModel.onEnviarClick(context) },
                    enabled = !state.advertenciaAnticipacion,
                    colors = coloresBotonAccion(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("Enviar solicitud")
                }
            }
        }
    }

    // Diálogos de fecha y horas
    if (dialogoAbierto == "fecha") {
        DialogoFecha(
            fechaInicial = state.fechaMillis,
            onConfirmar = {
                viewModel.onFechaChanged(context, it)
                dialogoAbierto = ""
            },
            onCancelar = { dialogoAbierto = "" }
        )
    }
    if (dialogoAbierto == "inicio") {
        DialogoHora(
            titulo = "Hora de inicio",
            horaInicial = state.horaInicio,
            onConfirmar = {
                viewModel.onHoraInicioChanged(context, it)
                dialogoAbierto = ""
            },
            onCancelar = { dialogoAbierto = "" }
        )
    }
    if (dialogoAbierto == "devolucion") {
        DialogoHora(
            titulo = "Hora estimada de devolución",
            horaInicial = state.horaDevolucion,
            onConfirmar = {
                viewModel.onHoraDevolucionChanged(it)
                dialogoAbierto = ""
            },
            onCancelar = { dialogoAbierto = "" }
        )
    }

    // Diálogo para confirmar el envío
    if (state.mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { viewModel.onCancelarConfirmacion() },
            title = { Text("Enviar solicitud") },
            text = { Text("¿Quieres enviar la solicitud para ${state.sala} el ${state.fechaTexto}?") },
            confirmButton = {
                Button(
                    onClick = { viewModel.onConfirmarEnvio(context, onEnviado) },
                    colors = coloresBotonAccion()
                ) {
                    Text("Enviar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onCancelarConfirmacion() }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// Campo de solo lectura que abre un menú o diálogo al tocarlo
@Composable
private fun CampoSelector(
    etiqueta: String,
    valor: String,
    error: String,
    onClick: () -> Unit
) {
    Box {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            isError = error.isNotEmpty(),
            supportingText = { MensajeError(error) },
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { onClick() }
        )
    }
}

// Diálogo con calendario para elegir la fecha
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogoFecha(
    fechaInicial: Long,
    onConfirmar: (Long) -> Unit,
    onCancelar: () -> Unit
) {
    val pickerState = rememberDatePickerState(
        initialSelectedDateMillis = if (fechaInicial == 0L) null else fechaInicial
    )
    DatePickerDialog(
        onDismissRequest = onCancelar,
        confirmButton = {
            TextButton(onClick = {
                val elegida = pickerState.selectedDateMillis
                if (elegida != null) onConfirmar(elegida) else onCancelar()
            }) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    ) {
        DatePicker(state = pickerState)
    }
}

// Diálogo con reloj para elegir una hora
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogoHora(
    titulo: String,
    horaInicial: String,
    onConfirmar: (String) -> Unit,
    onCancelar: () -> Unit
) {
    val partes = horaInicial.split(":")
    val pickerState = rememberTimePickerState(
        initialHour = partes.getOrNull(0)?.toIntOrNull() ?: 8,
        initialMinute = partes.getOrNull(1)?.toIntOrNull() ?: 0,
        is24Hour = true
    )
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(titulo) },
        text = { TimePicker(state = pickerState) },
        confirmButton = {
            TextButton(onClick = {
                onConfirmar(String.format("%02d:%02d", pickerState.hour, pickerState.minute))
            }) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}
