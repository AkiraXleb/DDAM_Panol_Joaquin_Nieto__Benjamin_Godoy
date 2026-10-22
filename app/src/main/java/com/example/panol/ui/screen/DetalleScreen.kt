package com.example.panol.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.panol.data.EstadoEquipo
import com.example.panol.ui.components.ChipEstado
import com.example.panol.ui.components.Header
import com.example.panol.ui.components.MensajeError
import com.example.panol.ui.components.iconoCategoria
import com.example.panol.ui.theme.coloresBotonAccion
import com.example.panol.ui.viewmodel.DetalleViewModel

@Composable
fun DetalleScreen(
    onVolver: () -> Unit,
    onAgregado: () -> Unit,
    viewModel: DetalleViewModel = viewModel()
) {
    val context = LocalContext.current
    val state = viewModel.uiState
    val equipo = state.equipo

    LaunchedEffect(Unit) {
        viewModel.cargarEquipo(context)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { Header(onVolver = onVolver) }
    ) { innerPadding ->
        if (equipo == null) {
            Text("Cargando equipo...", modifier = Modifier.padding(innerPadding).padding(16.dp))
            return@Scaffold
        }

        val puedeAgregar = equipo.estado == EstadoEquipo.DISPONIBLE && equipo.stockDisponible > 0

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(iconoCategoria(equipo.categoria)),
                                contentDescription = equipo.categoria,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(equipo.nombre, style = MaterialTheme.typography.titleLarge)
                                Text(equipo.categoria, style = MaterialTheme.typography.labelMedium)
                            }
                            ChipEstado(equipo.estado)
                        }

                        Text(
                            text = equipo.descripcion,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        DatoFila("Código", equipo.codigo)
                        DatoFila("Marca", equipo.marca)
                        DatoFila("Modelo", equipo.modelo)
                        DatoFila("Ubicación", equipo.ubicacion)
                        DatoFila("Stock disponible", "${equipo.stockDisponible} de ${equipo.stockTotal}")
                    }
                }
            }

            item {
                Text(
                    text = "Cantidad",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    OutlinedButton(onClick = { viewModel.onMenosClick() }, enabled = puedeAgregar) {
                        Text("−")
                    }
                    Text(state.cantidad.toString(), style = MaterialTheme.typography.headlineSmall)
                    OutlinedButton(onClick = { viewModel.onMasClick() }, enabled = puedeAgregar) {
                        Text("+")
                    }
                }
                MensajeError(state.errorCantidad, modifier = Modifier.padding(top = 4.dp))
                Text(
                    text = "Máximo por solicitud: ${state.maximo} unidades",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            item {
                Button(
                    onClick = { viewModel.onAgregarClick(context, onAgregado) },
                    enabled = puedeAgregar,
                    colors = coloresBotonAccion(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Text("Agregar al carrito")
                }
            }
        }
    }
}

@Composable
private fun DatoFila(titulo: String, valor: String) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 2.dp)) {
        Text(titulo, style = MaterialTheme.typography.labelLarge, modifier = Modifier.width(120.dp))
        Text(valor, style = MaterialTheme.typography.bodyMedium)
    }
}
