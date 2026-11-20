package com.example.panol.ui.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panol.data.EstadoSolicitud
import com.example.panol.data.Sesion
import com.example.panol.data.database.AppDatabase
import com.example.panol.data.local.entity.SolicitudEntity
import com.example.panol.data.repository.PanolRepository
import com.example.panol.ui.model.MisSolicitudesModel
import kotlinx.coroutines.launch

/*
 * ViewModel de Mis Solicitudes: filtro, cancelación y avisos.
 *
 * - cargarSolicitudes: Lee las solicitudes del docente desde el microservicio y el carrito desde Room.
 * - obtenerYMarcarNotificadas: Busca las solicitudes sin avisar, las marca como notificadas en el
 *   servidor y las devuelve.
 * - onTodasClick: Quita el filtro de estado.
 * - onEstadoClick: Filtra por estado, o quita el filtro si ya estaba elegido.
 * - onCancelarConfirmado: Cambia una solicitud pendiente a Cancelada.
 */
class MisSolicitudesViewModel : ViewModel() {

    var uiState by mutableStateOf(MisSolicitudesModel())
        private set

    fun cargarSolicitudes(context: Context) {
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)
            val usuarioId = Sesion.usuario?.id ?: return@launch
            val unidades = db.carritoDao().contarUnidades(usuarioId)
            try {
                val resultado = PanolRepository.solicitudesDe(usuarioId)
                uiState = uiState.copy(
                    solicitudes = resultado.map { it.solicitud },
                    detalles = resultado.flatMap { it.detalles },
                    unidadesCarrito = unidades
                )
            } catch (e: Exception) {
                uiState = uiState.copy(unidadesCarrito = unidades)
                Toast.makeText(context, PanolRepository.mensajeDeError(e), Toast.LENGTH_LONG).show()
            }
        }
    }

    suspend fun obtenerYMarcarNotificadas(context: Context): List<SolicitudEntity> {
        val usuarioId = Sesion.usuario?.id ?: return emptyList()
        return try {
            val nuevas = PanolRepository.sinNotificar(usuarioId)
            nuevas.forEach { PanolRepository.marcarNotificada(it.id) }
            nuevas
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun onTodasClick() {
        uiState = uiState.copy(estadoSeleccionado = "")
    }

    fun onEstadoClick(estado: String) {
        val nuevo = if (uiState.estadoSeleccionado == estado) "" else estado
        uiState = uiState.copy(estadoSeleccionado = nuevo)
    }

    fun onCancelarConfirmado(context: Context, solicitud: SolicitudEntity) {
        if (solicitud.estado != EstadoSolicitud.PENDIENTE) return
        viewModelScope.launch {
            try {
                PanolRepository.cancelar(solicitud.id)
            } catch (e: Exception) {
                Toast.makeText(context, PanolRepository.mensajeDeError(e), Toast.LENGTH_LONG).show()
            }
            cargarSolicitudes(context)
        }
    }
}
