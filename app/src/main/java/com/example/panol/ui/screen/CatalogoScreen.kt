package com.example.panol.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import com.example.panol.R
import com.example.panol.data.Constantes
import com.example.panol.data.EstadoEquipo
import com.example.panol.data.Sesion
import com.example.panol.data.local.entity.EquipoEntity
import com.example.panol.ui.components.BarraInferior
import com.example.panol.ui.components.ChipEstado
import com.example.panol.ui.components.Header
import com.example.panol.ui.components.ImagenEquipo
import com.example.panol.ui.navigation.Routes
import com.example.panol.ui.theme.coloresBotonAccion
import com.example.panol.ui.viewmodel.CatalogoViewModel

// Pantalla del catálogo con buscador, filtros y lista de equipos
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    onVerDetalle: () -> Unit,
    onNavegar: (String) -> Unit,
    viewModel: CatalogoViewModel = viewModel()
) {
    val context = LocalContext.current
    val state = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.cargarDatos(context)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { Header() },
        bottomBar = {
            BarraInferior(
                rutaActual = Routes.CATALOGO,
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
            // Saludo con el primer nombre del usuario
            Text(
                text = "Hola, ${Sesion.usuario?.nombre.orEmpty().substringBefore(" ")}. ¿Qué deseas solicitar hoy?",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp)
            )

            OutlinedTextField(
                value = state.busqueda,
                onValueChange = { viewModel.onBusquedaChanged(it) },
                label = { Text("Buscar equipo") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            val coloresChip = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = state.categoriaSeleccionada.isEmpty(),
                        onClick = { viewModel.onTodosClick() },
                        label = { Text("Todos") },
                        colors = coloresChip
                    )
                }
                items(Constantes.CATEGORIAS) { categoria ->
                    FilterChip(
                        selected = state.categoriaSeleccionada == categoria,
                        onClick = { viewModel.onCategoriaClick(categoria) },
                        label = { Text(categoria) },
                        colors = coloresChip
                    )
                }
            }

            Text(
                text = "${state.cantidadDisponibles} equipos disponibles",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (state.cargando && state.equipos.isEmpty()) {
                // Mientras llegan los datos desde AWS
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.error.isNotEmpty() && state.equipos.isEmpty()) {
                // Sin conexión con el servidor: tarjeta con botón para reintentar
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("No se pudo conectar con el servidor", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = state.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Button(
                            onClick = { viewModel.cargarDatos(context, manual = true) },
                            colors = coloresBotonAccion(),
                            modifier = Modifier.padding(top = 12.dp)
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            } else if (state.equiposFiltrados.isEmpty()) {
                Text("No se encontraron equipos", style = MaterialTheme.typography.bodyMedium)
            }

            // Solo se recarga si el usuario arrastra el dedo hacia abajo
            PullToRefreshBox(
                isRefreshing = state.actualizando,
                onRefresh = { viewModel.cargarDatos(context, manual = true) },
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.equiposFiltrados) { equipo ->
                        TarjetaEquipo(
                            equipo = equipo,
                            onVerDetalle = {
                                Sesion.equipoSeleccionadoId = equipo.id
                                onVerDetalle()
                            },
                            onAgregar = { viewModel.onAgregarClick(context, equipo) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }
            }
        }
    }
}

// Tarjeta de un equipo con estado, stock y botones
@Composable
private fun TarjetaEquipo(
    equipo: EquipoEntity,
    onVerDetalle: () -> Unit,
    onAgregar: () -> Unit
) {
    val puedeAgregar = equipo.estado == EstadoEquipo.DISPONIBLE && equipo.stockDisponible > 0

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ImagenEquipo(
                    equipoId = equipo.id,
                    categoria = equipo.categoria,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(equipo.nombre, style = MaterialTheme.typography.titleMedium)
                    Text(equipo.categoria, style = MaterialTheme.typography.labelMedium)
                }
                ChipEstado(equipo.estado)
            }

            Text(
                text = equipo.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "Stock disponible: ${equipo.stockDisponible} de ${equipo.stockTotal}",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 4.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = onVerDetalle, modifier = Modifier.weight(1f)) {
                    Text("Ver detalle")
                }
                Button(
                    onClick = onAgregar,
                    enabled = puedeAgregar,
                    colors = coloresBotonAccion()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_agregar),
                        contentDescription = "Agregar al carrito"
                    )
                }
            }
        }
    }
}
