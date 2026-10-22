package com.example.panol.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panol.data.EstadoSolicitud
import com.example.panol.data.Sesion
import com.example.panol.data.database.AppDatabase
import com.example.panol.data.local.entity.SolicitudEntity
import com.example.panol.ui.model.MisSolicitudesModel
import kotlinx.coroutines.launch

class MisSolicitudesViewModel : ViewModel() {

    var uiState by mutableStateOf(MisSolicitudesModel())
        private set

    // Lee las solicitudes del usuario, sus equipos y el contador del carrito
    fun cargarSolicitudes(context: Context) {
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)
            val usuarioId = Sesion.usuario?.id ?: return@launch
            val solicitudes = db.solicitudDao().obtenerPorUsuario(usuarioId)
            uiState = uiState.copy(
                solicitudes = solicitudes,
                detalles = solicitudes.flatMap { db.solicitudDao().obtenerDetalles(it.id) },
                unidadesCarrito = db.carritoDao().contarUnidades(usuarioId)
            )
        }
    }

    // Busca las solicitudes aceptadas/rechazadas sin avisar, las marca como notificadas y las devuelve
    suspend fun obtenerYMarcarNotificadas(context: Context): List<SolicitudEntity> {
        val db = AppDatabase.getDatabase(context)
        val usuarioId = Sesion.usuario?.id ?: return emptyList()
        val nuevas = db.solicitudDao().obtenerSinNotificar(usuarioId)
        nuevas.forEach { db.solicitudDao().marcarNotificada(it.id) }
        return nuevas
    }

    fun onTodasClick() {
        uiState = uiState.copy(estadoSeleccionado = "")
    }

    // Si se toca el estado ya seleccionado, se quita el filtro
    fun onEstadoClick(estado: String) {
        val nuevo = if (uiState.estadoSeleccionado == estado) "" else estado
        uiState = uiState.copy(estadoSeleccionado = nuevo)
    }

    // Cancela una solicitud pendiente después de confirmar en el AlertDialog
    fun onCancelarConfirmado(context: Context, solicitud: SolicitudEntity) {
        if (solicitud.estado != EstadoSolicitud.PENDIENTE) return
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)
            db.solicitudDao().actualizarEstado(solicitud.id, EstadoSolicitud.CANCELADA)
            cargarSolicitudes(context)
        }
    }
}
