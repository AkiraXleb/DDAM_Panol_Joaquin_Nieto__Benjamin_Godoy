package com.example.panol.ui.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.panol.R
import com.example.panol.ui.components.ChipEstadoSolicitud
import com.example.panol.ui.components.Header
import com.example.panol.ui.navigation.Routes
import com.example.panol.ui.theme.coloresBotonAccion
import com.example.panol.ui.viewmodel.ConfirmacionViewModel

// Pantalla de confirmación con el resumen de la solicitud enviada
@Composable
fun ConfirmacionScreen(
    onNavegar: (String) -> Unit,
    viewModel: ConfirmacionViewModel = viewModel()
) {
    val context = LocalContext.current
    val state = viewModel.uiState
    val solicitud = state.solicitud
    val usuario = state.usuario

    LaunchedEffect(Unit) {
        viewModel.cargarSolicitud(context)
    }

    // Pulso del check
    val escala = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        repeat(2) {
            escala.animateTo(1.1f, tween(500))
            escala.animateTo(1f, tween(500))
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { Header() }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = "Solicitud enviada",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .size((96 * escala.value).dp)
                )
            }
            item {
                Text(
                    text = "¡Solicitud enviada!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            if (solicitud != null) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("N° SOL-${solicitud.id}", style = MaterialTheme.typography.titleMedium)
                        ChipEstadoSolicitud(solicitud.estado)
                    }
                }

                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Solicitante", style = MaterialTheme.typography.titleMedium)
                            if (usuario != null) {
                                DatoConfirmacion("Nombre", usuario.nombre)
                                DatoConfirmacion("Cargo", usuario.cargo)
                                DatoConfirmacion("Correo", usuario.email)
                            }
                        }
                    }
                }

                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Uso del equipamiento", style = MaterialTheme.typography.titleMedium)
                            DatoConfirmacion("Sala", solicitud.sala)
                            DatoConfirmacion("Inicio", state.inicioTexto)
                            DatoConfirmacion("Devolución", state.devolucionTexto)
                        }
                    }
                }

                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Equipos solicitados", style = MaterialTheme.typography.titleMedium)
                            state.detalles.forEach { detalle ->
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)) {
                                    Text(
                                        detalle.nombreEquipo,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text("x${detalle.cantidad}", style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = { onNavegar(Routes.MIS_SOLICITUDES) },
                    colors = coloresBotonAccion(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text("Ir a Mis Solicitudes")
                }
            }
            item {
                OutlinedButton(
                    onClick = { onNavegar(Routes.CATALOGO) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text("Volver al catálogo")
                }
            }
        }
    }
}

// Fila con título y valor
@Composable
private fun DatoConfirmacion(titulo: String, valor: String) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 2.dp)) {
        Text(titulo, style = MaterialTheme.typography.labelLarge, modifier = Modifier.width(100.dp))
        Text(valor, style = MaterialTheme.typography.bodyMedium)
    }
}
