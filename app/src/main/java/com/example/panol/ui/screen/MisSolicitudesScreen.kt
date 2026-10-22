package com.example.panol.ui.screen

import android.widget.Toast
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
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
import com.example.panol.data.local.entity.DetalleSolicitudEntity
import com.example.panol.data.local.entity.SolicitudEntity
import com.example.panol.ui.components.BarraInferior
import com.example.panol.ui.components.ChipEstadoSolicitud
import com.example.panol.ui.components.Header
import com.example.panol.ui.model.formatearFechaHora
import com.example.panol.ui.navigation.Routes
import com.example.panol.ui.theme.MantenimientoFondo
import com.example.panol.ui.theme.MantenimientoTexto
import com.example.panol.ui.viewmodel.MisSolicitudesViewModel
import kotlinx.coroutines.delay

// Estados que se pueden usar como filtro
private val ESTADOS = listOf(
    EstadoSolicitud.PENDIENTE,
    EstadoSolicitud.ACEPTADA,
    EstadoSolicitud.RECHAZADA,
    EstadoSolicitud.ENTREGADA,
    EstadoSolicitud.DEVUELTA,
    EstadoSolicitud.CANCELADA
)

// Pantalla con las solicitudes del usuario, filtros y avisos nativos
@Composable
fun MisSolicitudesScreen(
    onNavegar: (String) -> Unit,
    viewModel: MisSolicitudesViewModel = viewModel()
) {
    val context = LocalContext.current
    val state = viewModel.uiState
    var solicitudACancelar by remember { mutableStateOf<SolicitudEntity?>(null) }

    var idsTemblando by remember { mutableStateOf(listOf<Int>()) }

    LaunchedEffect(Unit) {
        viewModel.cargarSolicitudes(context)

        // Aviso con vibración y linterna por cada solicitud revisada
        val nuevas = viewModel.obtenerYMarcarNotificadas(context)
        idsTemblando = nuevas.map { it.id }
        nuevas.forEach { solicitud ->
            val aprobada = solicitud.estado == EstadoSolicitud.ACEPTADA
            val texto = if (aprobada) "aceptada" else "rechazada"
            Toast.makeText(context, "Tu solicitud SOL-${solicitud.id} fue $texto", Toast.LENGTH_SHORT).show()
            senalSolicitud(context, aprobada)
        }
        if (nuevas.isNotEmpty()) {
            delay(2000)
            idsTemblando = emptyList()
        }
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
                rutaActual = Routes.MIS_SOLICITUDES,
                unidadesCarrito = state.unidadesCarrito,
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
            Text(
                text = "Mis solicitudes",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

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
                    text = if (state.solicitudes.isEmpty()) "Aún no tienes solicitudes"
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
                    TarjetaSolicitud(
                        solicitud = solicitud,
                        detalles = state.detallesDe(solicitud.id),
                        temblando = solicitud.id in idsTemblando,
                        onCancelar = { solicitudACancelar = solicitud }
                    )
                }
            }
        }
    }

    val pendiente = solicitudACancelar
    // Diálogo para confirmar la cancelación
    if (pendiente != null) {
        AlertDialog(
            onDismissRequest = { solicitudACancelar = null },
            title = { Text("Cancelar solicitud") },
            text = { Text("¿Quieres cancelar la solicitud SOL-${pendiente.id}?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.onCancelarConfirmado(context, pendiente)
                    solicitudACancelar = null
                }) {
                    Text("Cancelar solicitud")
                }
            },
            dismissButton = {
                TextButton(onClick = { solicitudACancelar = null }) {
                    Text("Volver")
                }
            }
        )
    }
}

// Tarjeta de una solicitud, tiembla si fue revisada recién
@Composable
private fun TarjetaSolicitud(
    solicitud: SolicitudEntity,
    detalles: List<DetalleSolicitudEntity>,
    temblando: Boolean,
    onCancelar: () -> Unit
) {
    // Temblor de la tarjeta
    val transicion = rememberInfiniteTransition(label = "temblor")
    val desplazamiento by transicion.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(60), RepeatMode.Reverse),
        label = "desplazamiento"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset(x = if (temblando) desplazamiento.dp else 0.dp)
    ) {
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
                text = solicitud.sala,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "Inicio: ${formatearFechaHora(solicitud.fechaInicio)}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Devolución: ${formatearFechaHora(solicitud.fechaDevolucion)}",
                style = MaterialTheme.typography.bodySmall
            )

            detalles.forEach { detalle ->
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)) {
                    Text(
                        detalle.nombreEquipo,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text("x${detalle.cantidad}", style = MaterialTheme.typography.labelLarge)
                }
            }

            // Comentario del encargado cuando se rechazó
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

            if (solicitud.estado == EstadoSolicitud.PENDIENTE) {
                OutlinedButton(
                    onClick = onCancelar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}
