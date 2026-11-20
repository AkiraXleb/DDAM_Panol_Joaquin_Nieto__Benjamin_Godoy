package com.example.panol.ui.screen

import androidx.compose.foundation.layout.Arrangement
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
import com.example.panol.data.local.entity.UsuarioEntity
import com.example.panol.ui.components.BarraInferior
import com.example.panol.ui.components.Header
import com.example.panol.ui.components.MensajeError
import com.example.panol.ui.navigation.Routes
import com.example.panol.ui.theme.coloresBotonAccion
import com.example.panol.ui.viewmodel.UsuariosViewModel

// Pantalla del Super Admin: lista de usuarios, agregar con correo por prefijo y eliminar
@Composable
fun UsuariosScreen(
    onNavegar: (String) -> Unit,
    viewModel: UsuariosViewModel = viewModel()
) {
    val context = LocalContext.current
    val state = viewModel.uiState
    var usuarioAEliminar by remember { mutableStateOf<UsuarioEntity?>(null) }

    LaunchedEffect(Unit) {
        viewModel.cargar(context)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { Header() },
        bottomBar = {
            BarraInferior(
                rutaActual = Routes.USUARIOS,
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
                    Text("Usuarios del sistema", style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = if (state.cargando) "Cargando..." else "${state.usuarios.size} usuarios registrados",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(
                        onClick = { viewModel.onNuevoClick() },
                        colors = coloresBotonAccion(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        Text("Agregar usuario")
                    }
                }
            }

            items(state.usuarios, key = { it.id }) { usuario ->
                TarjetaUsuario(usuario = usuario, onEliminar = { usuarioAEliminar = usuario })
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    // Formulario para agregar un usuario
    if (state.mostrarFormulario) {
        AlertDialog(
            onDismissRequest = { viewModel.onCancelarNuevo() },
            title = { Text("Nuevo usuario") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(
                        value = state.nombre,
                        onValueChange = { viewModel.onNombreChanged(it) },
                        label = { Text("Nombre completo") },
                        singleLine = true,
                        isError = state.errorNombre.isNotEmpty(),
                        supportingText = { MensajeError(state.errorNombre) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = state.correo,
                        onValueChange = { viewModel.onCorreoChanged(it) },
                        label = { Text("Correo (ej: prof.ana${Constantes.DOMINIO_CORREO})") },
                        singleLine = true,
                        isError = state.errorCorreo.isNotEmpty(),
                        supportingText = { MensajeError(state.errorCorreo) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = { viewModel.onPasswordChanged(it) },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        isError = state.errorPassword.isNotEmpty(),
                        supportingText = { MensajeError(state.errorPassword) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = state.departamento,
                        onValueChange = { viewModel.onDepartamentoChanged(it) },
                        label = { Text("Departamento") },
                        singleLine = true,
                        isError = state.errorDepartamento.isNotEmpty(),
                        supportingText = { MensajeError(state.errorDepartamento) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "El cargo depende del prefijo: prof. = Docente, insp. = Inspector, " +
                            "ti. = Encargado TI, admin. = Super Admin",
                        style = MaterialTheme.typography.bodySmall
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

    // Confirmación antes de eliminar
    val aEliminar = usuarioAEliminar
    if (aEliminar != null) {
        AlertDialog(
            onDismissRequest = { usuarioAEliminar = null },
            title = { Text("Eliminar usuario") },
            text = { Text("¿Seguro que quieres eliminar a ${aEliminar.nombre} (${aEliminar.email})?") },
            confirmButton = {
                Button(onClick = {
                    usuarioAEliminar = null
                    viewModel.onEliminarClick(context, aEliminar)
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { usuarioAEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}

// Tarjeta con los datos de un usuario y su botón para eliminar (los Super Admin no se pueden eliminar)
@Composable
private fun TarjetaUsuario(usuario: UsuarioEntity, onEliminar: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(usuario.nombre, style = MaterialTheme.typography.titleMedium)
                Text(usuario.cargo, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Text(usuario.email, style = MaterialTheme.typography.bodyMedium)
                Text(usuario.departamento, style = MaterialTheme.typography.bodySmall)
            }
            OutlinedButton(
                onClick = onEliminar,
                enabled = usuario.cargo != Constantes.CARGO_SUPER_ADMIN
            ) {
                Text("Eliminar")
            }
        }
    }
}
