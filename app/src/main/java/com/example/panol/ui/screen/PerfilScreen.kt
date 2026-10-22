package com.example.panol.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.panol.ui.components.BarraInferior
import com.example.panol.ui.components.Header
import com.example.panol.ui.navigation.Routes
import com.example.panol.ui.viewmodel.PerfilViewModel

// Pantalla del perfil con los datos del usuario y cierre de sesión
@Composable
fun PerfilScreen(
    onNavegar: (String) -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: PerfilViewModel = viewModel()
) {
    val context = LocalContext.current
    val state = viewModel.uiState
    var mostrarDialogo by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.cargarDatos(context)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { Header() },
        bottomBar = {
            BarraInferior(
                rutaActual = Routes.PERFIL,
                unidadesCarrito = state.unidadesCarrito,
                onNavegar = onNavegar
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("Mi perfil", style = MaterialTheme.typography.titleLarge)

            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DatoPerfil("Nombre", state.nombre)
                    DatoPerfil("Cargo", state.cargo)
                    DatoPerfil("Departamento", state.departamento)
                    DatoPerfil("Correo", state.correo)
                }
            }

            OutlinedButton(
                onClick = { mostrarDialogo = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Cerrar sesión")
            }
        }
    }

    // Diálogo para confirmar el cierre de sesión
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Seguro que quieres cerrar sesión?") },
            confirmButton = {
                Button(onClick = {
                    mostrarDialogo = false
                    viewModel.onCerrarSesionConfirmado(onCerrarSesion)
                }) {
                    Text("Cerrar sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// Fila con título y valor
@Composable
private fun DatoPerfil(titulo: String, valor: String) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)) {
        Text(titulo, style = MaterialTheme.typography.labelLarge, modifier = Modifier.width(120.dp))
        Text(valor, style = MaterialTheme.typography.bodyMedium)
    }
}
