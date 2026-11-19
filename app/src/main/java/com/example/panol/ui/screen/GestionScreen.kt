package com.example.panol.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.panol.data.EstadoSolicitud
import com.example.panol.data.Sesion
import com.example.panol.data.local.entity.DetalleSolicitudEntity
import com.example.panol.data.local.entity.SolicitudEntity
import com.example.panol.ui.components.BarraInferior
import com.example.panol.ui.components.ChipEstadoSolicitud
import com.example.panol.ui.components.Header
import com.example.panol.ui.components.MensajeError
import com.example.panol.ui.model.formatearFechaHora
import com.example.panol.ui.navigation.Routes
import com.example.panol.ui.theme.MantenimientoFondo
import com.example.panol.ui.theme.MantenimientoTexto
import com.example.panol.ui.theme.ReservadoFondo
import com.example.panol.ui.theme.ReservadoTexto
import com.example.panol.ui.viewmodel.GestionViewModel

// Estados que se pueden usar como filtro
private val ESTADOS = listOf(
    EstadoSolicitud.PENDIENTE,
    EstadoSolicitud.ACEPTADA,
    EstadoSolicitud.RECHAZADA,
    EstadoSolicitud.ENTREGADA,
    EstadoSolicitud.DEVUELTA,
    EstadoSolicitud.CANCELADA
)

// Pantalla del Encargado TI: gestiona las solicitudes de todos los docentes
@Composable
fun GestionScreen(
    onNavegar: (String) -> Unit,
    viewModel: GestionViewModel = viewModel()
) {
    val context = LocalContext.current
    val state = viewModel.uiState
    var solicitudARechazar by remember { mutableStateOf<SolicitudEntity?>(null) }
    var motivo by remember { mutableStateOf("") }

    // Al entrar carga las solicitudes y avisa con vibración si hay pendientes
    LaunchedEffect(Unit) {
        viewModel.cargar(context, true)
    }

    val coloresChip = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.primary,
        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { Header() },
        bottomBar = {
            BarraInferior(
                rutaActual = Routes.GESTION,
                unidadesCarrito = 0,
                onNavegar = onNavegar
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Gestión de solicitudes",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                OutlinedButton(
                    onClick = { viewModel.cargar(context, true) },
                    enabled = !state.cargando
                ) {
                    Text(if (state.cargando) "Cargando..." else "Actualizar")
                }
            }

            Text(
                text = "Hola, ${Sesion.usuario?.nombre.orEmpty().substringBefore(" ")}. ¿Qué deseas gestionar hoy?",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Aviso de solicitudes pendientes
            if (state.pendientes > 0) {
                Surface(
                    color = ReservadoFondo,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "Hay ${state.pendientes} solicitudes pendientes de revisión",
                        color = ReservadoTexto,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = state.estadoSeleccionado.isEmpty(),
                        onClick = { viewModel.onTodasClick() },
                        label = { Text("Todas") },
                        colors = coloresChip
                    )
                }
                items(ESTADOS) { estado ->
                    FilterChip(
                        selected = state.estadoSeleccionado == estado,
                        onClick = { viewModel.onEstadoClick(estado) },
                        label = { Text(estado) },
                        colors = coloresChip
                    )
                }
            }

            if (state.solicitudesFiltradas.isEmpty()) {
                Text(
                    text = if (state.solicitudes.isEmpty()) "No hay solicitudes registradas"
                    else "No hay solicitudes con este estado",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.solicitudesFiltradas) { solicitud ->
                    TarjetaGestion(
                        solicitud = solicitud,
                        solicitante = state.solicitanteDe(solicitud.id),
                        detalles = state.detallesDe(solicitud.id),
                        onAceptar = { viewModel.onAceptarClick(context, solicitud) },
                        onRechazar = {
                            motivo = ""
                            viewModel.onMotivoChanged()
                            solicitudARechazar = solicitud
                        },
                        onEntregar = { viewModel.onEntregarClick(context, solicitud) },
                        onRecibir = { viewModel.onRecibirClick(context, solicitud) }
                    )
                }
            }
        }
    }

    val rechazando = solicitudARechazar
    // Diálogo para escribir el motivo del rechazo (obligatorio, máximo 200 caracteres)
    if (rechazando != null) {
        AlertDialog(
            onDismissRequest = { solicitudARechazar = null },
            title = { Text("Rechazar SOL-${rechazando.id}") },
            text = {
                Column {
                    OutlinedTextField(
                        value = motivo,
                        onValueChange = {
                            motivo = it
                            viewModel.onMotivoChanged()
                        },
                        label = { Text("Motivo del rechazo") },
                        isError = state.errorMotivo.isNotEmpty(),
                        supportingText = { Text("${motivo.length}/200") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    MensajeError(state.errorMotivo)
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.onRechazarClick(context, rechazando, motivo) {
                        solicitudARechazar = null
                        motivo = ""
                    }
                }) {
                    Text("Rechazar")
                }
            },
            dismissButton = {
                TextButton(onClick = { solicitudARechazar = null }) {
                    Text("Volver")
                }
            }
        )
    }
}

// Tarjeta de una solicitud con los botones que corresponden a su estado
@Composable
private fun TarjetaGestion(
    solicitud: SolicitudEntity,
    solicitante: String,
    detalles: List<DetalleSolicitudEntity>,
    onAceptar: () -> Unit,
    onRechazar: () -> Unit,
    onEntregar: () -> Unit,
    onRecibir: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "SOL-${solicitud.id}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                ChipEstadoSolicitud(solicitud.estado)
            }

            Text(
                text = "Solicitante: $solicitante",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(text = solicitud.sala, style = MaterialTheme.typography.bodyMedium)
            if (solicitud.asignatura.isNotEmpty()) {
                Text(text = solicitud.asignatura, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = "Inicio: ${formatearFechaHora(solicitud.fechaInicio)}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Devolución: ${formatearFechaHora(solicitud.fechaDevolucion)}",
                style = MaterialTheme.typography.bodySmall
            )

            detalles.forEach { detalle ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    Text(
                        detalle.nombreEquipo,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text("x${detalle.cantidad}", style = MaterialTheme.typography.labelLarge)
                }
            }

            if (solicitud.observaciones.isNotEmpty()) {
                Text(
                    text = "Observaciones: ${solicitud.observaciones}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Motivo del rechazo guardado por el encargado
            if (solicitud.estado == EstadoSolicitud.RECHAZADA) {
                Surface(
                    color = MantenimientoFondo,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = "Motivo del rechazo: ${solicitud.comentarioGestion.ifEmpty { "Sin comentario" }}",
                        color = MantenimientoTexto,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            // Acciones según el estado
            when (solicitud.estado) {
                EstadoSolicitud.PENDIENTE -> Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = onAceptar, modifier = Modifier.weight(1f)) { Text("Aceptar") }
                    OutlinedButton(onClick = onRechazar, modifier = Modifier.weight(1f)) { Text("Rechazar") }
                }
                EstadoSolicitud.ACEPTADA -> Button(
                    onClick = onEntregar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) { Text("Registrar entrega") }
                EstadoSolicitud.ENTREGADA -> Button(
                    onClick = onRecibir,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) { Text("Registrar devolución") }
                else -> Unit
            }
        }
    }
}
