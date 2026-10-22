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

// ViewModel de Mis Solicitudes: filtro, cancelación y avisos
class MisSolicitudesViewModel : ViewModel() {

    var uiState by mutableStateOf(MisSolicitudesModel())
        private set

    // Lee las solicitudes, sus equipos y el contador del carrito
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

    // Busca las solicitudes sin avisar, las marca como notificadas y las devuelve
    suspend fun obtenerYMarcarNotificadas(context: Context): List<SolicitudEntity> {
        val db = AppDatabase.getDatabase(context)
        val usuarioId = Sesion.usuario?.id ?: return emptyList()
        val nuevas = db.solicitudDao().obtenerSinNotificar(usuarioId)
        nuevas.forEach { db.solicitudDao().marcarNotificada(it.id) }
        return nuevas
    }

    // Quita el filtro de estado
    fun onTodasClick() {
        uiState = uiState.copy(estadoSeleccionado = "")
    }

    // Filtra por estado, o quita el filtro si ya estaba elegido
    fun onEstadoClick(estado: String) {
        val nuevo = if (uiState.estadoSeleccionado == estado) "" else estado
        uiState = uiState.copy(estadoSeleccionado = nuevo)
    }

    // Cambia una solicitud pendiente a Cancelada
    fun onCancelarConfirmado(context: Context, solicitud: SolicitudEntity) {
        if (solicitud.estado != EstadoSolicitud.PENDIENTE) return
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)
            db.solicitudDao().actualizarEstado(solicitud.id, EstadoSolicitud.CANCELADA)
            cargarSolicitudes(context)
        }
    }
}
