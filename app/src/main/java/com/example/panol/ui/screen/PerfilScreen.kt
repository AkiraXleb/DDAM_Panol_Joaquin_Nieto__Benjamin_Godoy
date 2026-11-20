package com.example.panol.ui.screen

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.graphics.asImageBitmap
import com.example.panol.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
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
import com.example.panol.data.Sesion
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
    var mostrarOpcionesFoto by remember { mutableStateOf(false) }

    // Selector de fotos de la galería
    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) viewModel.onFotoElegida(context, uri)
    }
    // Cámara: deja la foto en un archivo temporal y avisa si se tomó
    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { tomada: Boolean ->
        if (tomada) viewModel.onFotoTomada(context)
    }

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

            // Foto de perfil (o iniciales si el usuario no tiene foto)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AvatarPerfil(nombre = state.nombre, correo = state.correo, fotoVersion = state.fotoVersion)
                    TextButton(onClick = { mostrarOpcionesFoto = true }) {
                        Text("Cambiar foto")
                    }
                }
            }

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

    // Opciones para cambiar la foto de perfil
    if (mostrarOpcionesFoto) {
        AlertDialog(
            onDismissRequest = { mostrarOpcionesFoto = false },
            title = { Text("Foto de perfil") },
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
                        Text("Quitar foto")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarOpcionesFoto = false }) { Text("Cancelar") }
            }
        )
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
                    viewModel.onCerrarSesionConfirmado(context, onCerrarSesion)
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

// Foto del usuario: la que eligió, si no la de su correo y si no, un círculo con sus iniciales
@Composable
private fun AvatarPerfil(nombre: String, correo: String, fotoVersion: Int) {
    val context = LocalContext.current
    val usuarioId = Sesion.usuario?.id ?: 0
    // Se vuelve a leer el archivo cada vez que cambia fotoVersion
    val propia = remember(fotoVersion, usuarioId) {
        val archivo = archivoFoto(context, usuarioId)
        if (archivo.exists()) BitmapFactory.decodeFile(archivo.path)?.asImageBitmap() else null
    }
    if (propia != null) {
        Image(
            bitmap = propia,
            contentDescription = "Foto de perfil",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )
        return
    }
    val foto = when (correo) {
        "prof.patricia@escuelamarcelapaz.cl" -> R.drawable.perfil_docente
        "ti.roberto@escuelamarcelapaz.cl" -> R.drawable.perfil_ti
        else -> null
    }
    if (foto != null) {
        Image(
            painter = painterResource(foto),
            contentDescription = "Foto de perfil",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )
    } else {
        val iniciales = nombre.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = iniciales,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
