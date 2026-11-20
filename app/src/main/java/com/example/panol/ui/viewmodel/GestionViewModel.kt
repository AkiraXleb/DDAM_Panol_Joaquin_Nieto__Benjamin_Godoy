package com.example.panol.ui.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panol.data.EstadoSolicitud
import com.example.panol.data.local.entity.SolicitudEntity
import com.example.panol.data.repository.PanolRepository
import com.example.panol.ui.model.GestionModel
import com.example.panol.ui.screen.sonarAprobacion
import com.example.panol.ui.screen.vibrar
import kotlinx.coroutines.launch

// ViewModel de Gestión (Encargado TI): aceptar, rechazar con motivo, entregar y recibir
class GestionViewModel : ViewModel() {

    var uiState by mutableStateOf(GestionModel())
        private set

    // Lee todas las solicitudes del microservicio. Si hay pendientes, avisa con vibración.
    fun cargar(context: Context, avisarPendientes: Boolean) {
        viewModelScope.launch {
            uiState = uiState.copy(cargando = true)
            try {
                val resultado = PanolRepository.todasLasSolicitudes()
                uiState = uiState.copy(
                    solicitudes = resultado.map { it.solicitud },
                    detalles = resultado.flatMap { it.detalles },
                    nombres = resultado.associate { it.solicitud.id to it.usuarioNombre },
                    cargando = false
                )
                val pendientes = uiState.pendientes
                if (avisarPendientes && pendientes > 0) {
                    Toast.makeText(context, "Tienes $pendientes solicitudes pendientes", Toast.LENGTH_SHORT).show()
                    vibrar(context, 200)
                }
            } catch (e: Exception) {
                uiState = uiState.copy(cargando = false)
                Toast.makeText(context, PanolRepository.mensajeDeError(e), Toast.LENGTH_LONG).show()
            }
        }
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

    // Limpia el error del motivo mientras el encargado escribe
    fun onMotivoChanged() {
        uiState = uiState.copy(errorMotivo = "")
    }

    // Acepta la solicitud (el servidor descuenta el stock)
    fun onAceptarClick(context: Context, solicitud: SolicitudEntity) {
        cambiar(context, solicitud.id, EstadoSolicitud.ACEPTADA, null, "SOL-${solicitud.id} aceptada")
    }

    // Valida el motivo y rechaza la solicitud
    fun onRechazarClick(context: Context, solicitud: SolicitudEntity, motivo: String, onSuccess: () -> Unit) {
        val texto = motivo.trim()
        val error = when {
            texto.isEmpty() -> "Escribe el motivo del rechazo"
            texto.length > 200 -> "Máximo 200 caracteres"
            else -> ""
        }
        uiState = uiState.copy(errorMotivo = error)
        if (error.isNotEmpty()) return
        cambiar(context, solicitud.id, EstadoSolicitud.RECHAZADA, texto, "SOL-${solicitud.id} rechazada", onSuccess)
    }

    // Registra la entrega de los equipos
    fun onEntregarClick(context: Context, solicitud: SolicitudEntity) {
        cambiar(context, solicitud.id, EstadoSolicitud.ENTREGADA, null, "SOL-${solicitud.id} entregada")
    }

    // Registra la devolución (el servidor repone el stock)
    fun onRecibirClick(context: Context, solicitud: SolicitudEntity) {
        cambiar(context, solicitud.id, EstadoSolicitud.DEVUELTA, null, "SOL-${solicitud.id} recibida")
    }

    // Envía el cambio de estado al servidor, vibra y recarga la lista
    private fun cambiar(
        context: Context,
        id: Int,
        estado: String,
        motivo: String?,
        mensaje: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                PanolRepository.cambiarEstado(id, estado, motivo)
                vibrar(context, 150)
                Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                onSuccess()
                cargar(context, false)
                // Al aceptar una solicitud también suena el aviso de aprobación
                if (estado == EstadoSolicitud.ACEPTADA) sonarAprobacion(context)
            } catch (e: Exception) {
                Toast.makeText(context, PanolRepository.mensajeDeError(e), Toast.LENGTH_LONG).show()
            }
        }
    }
}
