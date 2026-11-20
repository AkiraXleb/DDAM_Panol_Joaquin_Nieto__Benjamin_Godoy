package com.example.panol.ui.screen

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.panol.data.Constantes
import com.example.panol.data.local.entity.EquipoEntity
import com.example.panol.ui.components.BarraInferior
import com.example.panol.ui.components.ChipEstado
import com.example.panol.ui.components.Header
import com.example.panol.ui.components.ImagenEquipo
import com.example.panol.ui.components.MensajeError
import com.example.panol.ui.navigation.Routes
import com.example.panol.ui.theme.coloresBotonAccion
import com.example.panol.ui.viewmodel.InventarioViewModel

// Pantalla del Encargado TI: lista del inventario, agregar equipos y eliminarlos
@Composable
fun InventarioScreen(
    onNavegar: (String) -> Unit,
    viewModel: InventarioViewModel = viewModel()
) {
    val context = LocalContext.current
    val state = viewModel.uiState
    var equipoAEliminar by remember { mutableStateOf<EquipoEntity?>(null) }
    var menuCategoriaAbierto by remember { mutableStateOf(false) }
    var mostrarOpcionesFoto by remember { mutableStateOf(false) }

    // Selector de la galería y cámara (la foto va al equipo nuevo o a uno existente, según el destino elegido)
    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) viewModel.onFotoElegida(context, uri)
    }
    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { tomada: Boolean ->
        if (tomada) viewModel.onFotoTomada(context)
    }

    LaunchedEffect(Unit) {
        viewModel.cargar(context)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { Header() },
        bottomBar = {
            BarraInferior(
                rutaActual = Routes.INVENTARIO,
                unidadesCarrito = 0,
                onNavegar = onNavegar
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text("Inventario del pañol", style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = if (state.cargando) "Cargando..." else "${state.equipos.size} equipos registrados",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(
                        onClick = { viewModel.onNuevoClick() },
                        colors = coloresBotonAccion(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        Text("Agregar equipo")
                    }
                }
            }

            items(state.equipos, key = { it.id }) { equipo ->
                TarjetaEquipo(
                    equipo = equipo,
                    fotoVersion = state.fotoVersion,
                    onMantenimiento = { viewModel.onMantenimientoClick(equipo) },
                    onFoto = {
                        viewModel.onElegirFoto(equipo.id)
                        mostrarOpcionesFoto = true
                    },
                    onEliminar = { equipoAEliminar = equipo }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    // Formulario para agregar un equipo
    if (state.mostrarFormulario) {
        AlertDialog(
            onDismissRequest = { viewModel.onCancelarNuevo() },
            title = { Text("Nuevo equipo") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    // Imagen del equipo (opcional)
                    Row(
                        modifier = Modifier.padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val vistaPrevia = remember(state.fotoNueva, state.fotoVersion) {
                            if (state.fotoNueva.isEmpty()) null
                            else BitmapFactory.decodeFile(state.fotoNueva)?.asImageBitmap()
                        }
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            if (vistaPrevia != null) {
                                Image(
                                    bitmap = vistaPrevia,
                                    contentDescription = "Imagen del equipo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Text("Sin imagen", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        OutlinedButton(onClick = {
                            viewModel.onElegirFoto(-1)
                            mostrarOpcionesFoto = true
                        }) {
                            Text(if (state.fotoNueva.isEmpty()) "Agregar imagen" else "Cambiar imagen")
                        }
                    }
                    OutlinedTextField(
                        value = state.codigo,
                        onValueChange = { viewModel.onCodigoChanged(it) },
                        label = { Text("Código (ej: NOT-2026-01)") },
                        singleLine = true,
                        isError = state.errorCodigo.isNotEmpty(),
                        supportingText = { MensajeError(state.errorCodigo) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = state.nombre,
                        onValueChange = { viewModel.onNombreChanged(it) },
                        label = { Text("Nombre") },
                        singleLine = true,
                        isError = state.errorNombre.isNotEmpty(),
                        supportingText = { MensajeError(state.errorNombre) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = state.marca,
                        onValueChange = { viewModel.onMarcaChanged(it) },
                        label = { Text("Marca (opcional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = state.modelo,
                        onValueChange = { viewModel.onModeloChanged(it) },
                        label = { Text("Modelo (opcional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Categoría con menú desplegable
                    OutlinedButton(
                        onClick = { menuCategoriaAbierto = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text(if (state.categoria.isEmpty()) "Elegir categoría" else state.categoria)
                    }
                    DropdownMenu(
                        expanded = menuCategoriaAbierto,
                        onDismissRequest = { menuCategoriaAbierto = false }
                    ) {
                        Constantes.CATEGORIAS.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria) },
                                onClick = {
                                    viewModel.onCategoriaChanged(categoria)
                                    menuCategoriaAbierto = false
                                }
                            )
                        }
                    }
                    MensajeError(state.errorCategoria)

                    OutlinedTextField(
                        value = state.descripcion,
                        onValueChange = { viewModel.onDescripcionChanged(it) },
                        label = { Text("Descripción (opcional)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                    OutlinedTextField(
                        value = state.ubicacion,
                        onValueChange = { viewModel.onUbicacionChanged(it) },
                        label = { Text("Ubicación (ej: Pañol Central - Estante A1)") },
                        isError = state.errorUbicacion.isNotEmpty(),
                        supportingText = { MensajeError(state.errorUbicacion) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = state.stock,
                        onValueChange = { viewModel.onStockChanged(it) },
                        label = { Text("Stock total") },
                        singleLine = true,
                        isError = state.errorStock.isNotEmpty(),
                        supportingText = { MensajeError(state.errorStock) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    MensajeError(state.errorGeneral)
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.onGuardarClick(context) }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onCancelarNuevo() }) { Text("Cancelar") }
            }
        )
    }

    // Mantenimiento: enviar unidades disponibles o devolver las que ya están en mantenimiento
    val enMantenimiento = state.equipoMantenimiento
    if (enMantenimiento != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onCancelarMantenimiento() },
            title = { Text("Mantenimiento") },
            text = {
                Column {
                    Text(enMantenimiento.nombre, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Disponibles: ${enMantenimiento.stockDisponible} · En mantenimiento: ${enMantenimiento.stockMantenimiento}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = state.cantidadMantenimiento,
                        onValueChange = { viewModel.onCantidadMantenimientoChanged(it) },
                        label = { Text("Cantidad de unidades") },
                        singleLine = true,
                        isError = state.errorMantenimiento.isNotEmpty(),
                        supportingText = { MensajeError(state.errorMantenimiento) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = { viewModel.onEnviarMantenimiento(context) },
                        enabled = enMantenimiento.stockDisponible > 0,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Enviar a mantenimiento")
                    }
                    OutlinedButton(
                        onClick = { viewModel.onDevolverMantenimiento(context) },
                        enabled = enMantenimiento.stockMantenimiento > 0,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text("Devolver al stock")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onCancelarMantenimiento() }) { Text("Cerrar") }
            }
        )
    }

    // Opciones para la imagen de un equipo
    if (mostrarOpcionesFoto) {
        AlertDialog(
            onDismissRequest = { mostrarOpcionesFoto = false },
            title = { Text("Imagen del equipo") },
            text = {
                Column {
                    TextButton(onClick = {
                        mostrarOpcionesFoto = false
                        try {
                            camara.launch(uriCamara(context))
                        } catch (e: Exception) {
                            Toast.makeText(context, "No se pudo abrir la cámara", Toast.LENGTH_LONG).show()
                        }
                    }) {
                        Text("Tomar foto")
                    }
                    TextButton(onClick = {
                        mostrarOpcionesFoto = false
                        galeria.launch("image/*")
                    }) {
                        Text("Elegir de la galería")
                    }
                    TextButton(onClick = {
                        mostrarOpcionesFoto = false
                        viewModel.onQuitarFoto(context)
                    }) {
                        Text("Quitar imagen")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarOpcionesFoto = false }) { Text("Cancelar") }
            }
        )
    }

    // Confirmación antes de eliminar
    val aEliminar = equipoAEliminar
    if (aEliminar != null) {
        AlertDialog(
            onDismissRequest = { equipoAEliminar = null },
            title = { Text("Eliminar equipo") },
            text = { Text("¿Seguro que quieres eliminar ${aEliminar.nombre} (${aEliminar.codigo}) del inventario?") },
            confirmButton = {
                Button(onClick = {
                    equipoAEliminar = null
                    viewModel.onEliminarClick(context, aEliminar)
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { equipoAEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}

// Tarjeta con los datos de un equipo y su botón para eliminar (deshabilitado si hay unidades en préstamo)
@Composable
private fun TarjetaEquipo(
    equipo: EquipoEntity,
    fotoVersion: Int,
    onMantenimiento: () -> Unit,
    onFoto: () -> Unit,
    onEliminar: () -> Unit
) {
    // En préstamo = unidades fuera que no están en mantenimiento
    val enPrestamo = equipo.stockDisponible + equipo.stockMantenimiento < equipo.stockTotal
    val tieneUnidadesFuera = equipo.stockDisponible < equipo.stockTotal
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ImagenEquipo(
                equipoId = equipo.id,
                categoria = equipo.categoria,
                modifier = Modifier.size(72.dp),
                version = fotoVersion
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(equipo.nombre, style = MaterialTheme.typography.titleMedium)
                Text("${equipo.codigo} · ${equipo.categoria}", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "Stock: ${equipo.stockDisponible} de ${equipo.stockTotal} disponibles",
                    style = MaterialTheme.typography.bodySmall
                )
                if (equipo.stockMantenimiento > 0) {
                    Text(
                        "En mantenimiento: ${equipo.stockMantenimiento}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                if (enPrestamo) {
                    Text(
                        "Tiene unidades en préstamo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                ChipEstado(equipo.estado)
            }
            Column(horizontalAlignment = Alignment.End) {
                TextButton(onClick = onMantenimiento) { Text("Mantenimiento") }
                TextButton(onClick = onFoto) { Text("Imagen") }
                OutlinedButton(onClick = onEliminar, enabled = !tieneUnidadesFuera) {
                    Text("Eliminar")
                }
            }
        }
    }
}
