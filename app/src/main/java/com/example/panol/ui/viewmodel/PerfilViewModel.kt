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

class PerfilViewModel : ViewModel() {

    var uiState by mutableStateOf(PerfilModel())
        private set

    // Lee los datos del usuario conectado y el contador del carrito
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

    // Borra el usuario de la sesión y avisa a la pantalla para ir al Login
    fun onCerrarSesionConfirmado(onSuccess: () -> Unit) {
        Sesion.cerrarSesion()
        onSuccess()
    }
}
