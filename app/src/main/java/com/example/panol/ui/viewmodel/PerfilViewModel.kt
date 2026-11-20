package com.example.panol.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panol.data.Sesion
import com.example.panol.data.database.AppDatabase
import com.example.panol.ui.model.PerfilModel
import kotlinx.coroutines.launch

// ViewModel del Perfil: datos del usuario y cierre de sesión
class PerfilViewModel : ViewModel() {

    var uiState by mutableStateOf(PerfilModel())
        private set

    // Lee los datos del usuario y el contador del carrito
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

    // Borra la sesión (memoria y Room) y avisa a la pantalla
    fun onCerrarSesionConfirmado(context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)
            db.usuarioDao().borrarTodos()
            Sesion.cerrarSesion()
            onSuccess()
        }
    }
}
