package com.example.panol.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.panol.data.local.entity.CarritoItemEntity
import com.example.panol.ui.components.BarraInferior
import com.example.panol.ui.components.Header
import com.example.panol.ui.components.iconoCategoria
import com.example.panol.ui.navigation.Routes
import com.example.panol.ui.theme.coloresBotonAccion
import com.example.panol.ui.viewmodel.CarritoViewModel

// Pantalla del carrito con resumen y acceso al formulario
@Composable
fun CarritoScreen(
    onContinuar: () -> Unit,
    onNavegar: (String) -> Unit,
    viewModel: CarritoViewModel = viewModel()
) {
    val context = LocalContext.current
    val state = viewModel.uiState
    var itemAEliminar by remember { mutableStateOf<CarritoItemEntity?>(null) }

    LaunchedEffect(Unit) {
        viewModel.cargarCarrito(context)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { Header() },
        bottomBar = {
            BarraInferior(
                rutaActual = Routes.CARRITO,
                unidadesCarrito = state.totalUnidades,
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
                text = "Mi carrito",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            if (state.items.isEmpty()) {
                Text(
                    text = "Tu carrito está vacío. Agrega equipos desde el catálogo.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.items) { item ->
                        TarjetaCarrito(
                            item = item,
                            onMenos = { viewModel.onMenosClick(context, item) },
                            onMas = { viewModel.onMasClick(context, item) },
                            onEliminar = { itemAEliminar = item }
                        )
                    }
                }
            }

            // Resumen del carrito
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Resumen", style = MaterialTheme.typography.titleMedium)
                    Text("Equipos: ${state.totalEquipos}", style = MaterialTheme.typography.bodyMedium)
                    Text("Unidades: ${state.totalUnidades}", style = MaterialTheme.typography.bodyMedium)
                }
            }

            OutlinedButton(
                onClick = { onNavegar(Routes.CATALOGO) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("Agregar otro equipo")
            }
            Button(
                onClick = onContinuar,
                enabled = state.items.isNotEmpty(),
                colors = coloresBotonAccion(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Continuar a Formulario")
            }
        }
    }

    val item = itemAEliminar
    // Diálogo para confirmar la eliminación
    if (item != null) {
        AlertDialog(
            onDismissRequest = { itemAEliminar = null },
            title = { Text("Eliminar equipo") },
            text = { Text("¿Quieres quitar ${item.nombreEquipo} del carrito?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.onEliminarConfirmado(context, item)
                    itemAEliminar = null
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// Tarjeta de un equipo del carrito con cantidad y eliminar
@Composable
private fun TarjetaCarrito(
    item: CarritoItemEntity,
    onMenos: () -> Unit,
    onMas: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(iconoCategoria(item.categoria)),
                    contentDescription = item.categoria,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.nombreEquipo, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${item.categoria} · ${item.codigoEquipo}",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(onClick = onMenos) { Text("−") }
                Text(item.cantidad.toString(), style = MaterialTheme.typography.titleMedium)
                OutlinedButton(onClick = onMas) { Text("+") }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = onEliminar) { Text("Eliminar") }
            }
        }
    }
}
