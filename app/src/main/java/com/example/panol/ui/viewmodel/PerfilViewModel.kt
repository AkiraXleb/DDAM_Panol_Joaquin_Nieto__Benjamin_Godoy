package com.example.panol.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panol.data.Sesion
import com.example.panol.data.database.AppDatabase
import com.example.panol.ui.model.PerfilModel
import com.example.panol.ui.screen.archivoCamara
import com.example.panol.ui.screen.archivoFoto
import com.example.panol.ui.screen.copiarDeGaleria
import com.example.panol.ui.screen.guardarFoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*
 * ViewModel del Perfil: datos del usuario y cierre de sesión.
 *
 * - cargarDatos: Lee los datos del usuario y el contador del carrito.
 * - onFotoElegida: Guarda la foto elegida en la galería.
 * - onFotoTomada: Guarda la foto que acaba de tomar la cámara.
 * - onQuitarFoto: Borra la foto y vuelve a la imagen por defecto.
 * - avisarResultado: Refresca la foto en pantalla y avisa si se guardó o no.
 * - onCerrarSesionConfirmado: Borra la sesión (memoria y Room) y avisa a la pantalla.
 */
class PerfilViewModel : ViewModel() {

    var uiState by mutableStateOf(PerfilModel())
        private set

    fun cargarDatos(context: Context) {
        val usuario = Sesion.usuario ?: return
        uiState = PerfilModel(
            nombre = usuario.nombre,
            cargo = usuario.cargo,
            departamento = usuario.departamento,
            correo = usuario.email
        )
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)
            uiState = uiState.copy(unidadesCarrito = db.carritoDao().contarUnidades(usuario.id))
        }
    }

    fun onFotoElegida(context: Context, uri: Uri) {
        val usuario = Sesion.usuario ?: return
        viewModelScope.launch {
            val guardada = withContext(Dispatchers.IO) {
                val temporal = copiarDeGaleria(context, uri)
                temporal != null && guardarFoto(context, usuario.id, temporal)
            }
            avisarResultado(context, guardada)
        }
    }

    fun onFotoTomada(context: Context) {
        val usuario = Sesion.usuario ?: return
        viewModelScope.launch {
            val guardada = withContext(Dispatchers.IO) {
                guardarFoto(context, usuario.id, archivoCamara(context))
            }
            avisarResultado(context, guardada)
        }
    }

    fun onQuitarFoto(context: Context) {
        val usuario = Sesion.usuario ?: return
        archivoFoto(context, usuario.id).delete()
        uiState = uiState.copy(fotoVersion = uiState.fotoVersion + 1)
        Toast.makeText(context, "Foto eliminada", Toast.LENGTH_SHORT).show()
    }

    private fun avisarResultado(context: Context, guardada: Boolean) {
        if (guardada) {
            uiState = uiState.copy(fotoVersion = uiState.fotoVersion + 1)
            Toast.makeText(context, "Foto actualizada", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "No se pudo guardar la foto", Toast.LENGTH_LONG).show()
        }
    }

    fun onCerrarSesionConfirmado(context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)
            db.usuarioDao().borrarTodos()
            Sesion.cerrarSesion()
            onSuccess()
        }
    }
}
