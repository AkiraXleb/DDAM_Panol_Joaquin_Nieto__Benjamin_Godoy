package com.example.panol.ui.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panol.data.Constantes
import com.example.panol.data.EstadoSolicitud
import com.example.panol.data.Sesion
import com.example.panol.data.database.AppDatabase
import com.example.panol.data.local.entity.DetalleSolicitudEntity
import com.example.panol.data.local.entity.SolicitudEntity
import com.example.panol.data.repository.PanolRepository
import com.example.panol.ui.model.SolicitudModel
import com.example.panol.ui.screen.vibrar
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

// ViewModel del Formulario: validación por campo y envío de la solicitud
class SolicitudViewModel : ViewModel() {

    var uiState by mutableStateOf(SolicitudModel())
        private set

    // Guarda la sala elegida
    fun onSalaChanged(valor: String) {
        uiState = uiState.copy(sala = valor, errorSala = "")
    }

    // Guarda la fecha y revisa la regla de 3 horas
    fun onFechaChanged(context: Context, valor: Long) {
        uiState = uiState.copy(fechaMillis = valor, errorFecha = "")
        actualizarAdvertencia(context)
    }

    // Guarda la hora de inicio y revisa la regla de 3 horas
    fun onHoraInicioChanged(context: Context, valor: String) {
        uiState = uiState.copy(horaInicio = valor, errorHoraInicio = "")
        actualizarAdvertencia(context)
    }

    // Guarda la hora de devolución
    fun onHoraDevolucionChanged(valor: String) {
        uiState = uiState.copy(horaDevolucion = valor, errorHoraDevolucion = "")
    }

    // Guarda la asignatura
    fun onAsignaturaChanged(valor: String) {
        uiState = uiState.copy(asignatura = valor)
    }

    // Guarda las observaciones y valida el máximo de 200 caracteres
    fun onObservacionesChanged(valor: String) {
        val error = if (valor.length > 200) "Máximo 200 caracteres" else ""
        uiState = uiState.copy(observaciones = valor, errorObservaciones = error)
    }

    // Cierra el diálogo de confirmación
    fun onCancelarConfirmacion() {
        uiState = uiState.copy(mostrarConfirmacion = false)
    }

    // Convierte la fecha elegida y una hora HH:mm a milisegundos
    private fun armarMillis(hora: String): Long {
        val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        utc.timeInMillis = uiState.fechaMillis
        val partes = hora.split(":")
        val cal = Calendar.getInstance()
        cal.clear()
        cal.set(
            utc.get(Calendar.YEAR),
            utc.get(Calendar.MONTH),
            utc.get(Calendar.DAY_OF_MONTH),
            partes[0].toInt(),
            partes[1].toInt()
        )
        return cal.timeInMillis
    }

    // Revisa si el inicio es antes de ahora + 3 horas
    private fun inicioMuyCercano(): Boolean {
        if (uiState.fechaMillis == 0L || uiState.horaInicio.isEmpty()) return false
        val minimo = System.currentTimeMillis() + Constantes.HORAS_ANTICIPACION * 60 * 60 * 1000L
        return armarMillis(uiState.horaInicio) < minimo
    }

    // Muestra la advertencia y vibra corto si se rompe la regla
    private fun actualizarAdvertencia(context: Context) {
        val rompeRegla = inicioMuyCercano()
        if (rompeRegla) vibrar(context, 150)
        uiState = uiState.copy(advertenciaAnticipacion = rompeRegla)
    }

    // Valida cada campo y devuelve true si no hay errores
    private fun validar(): Boolean {
        val state = uiState

        val errorSala = if (state.sala.isEmpty()) "Selecciona una sala" else ""

        val errorFecha = when {
            state.fechaMillis == 0L -> "Selecciona una fecha"
            armarMillis("00:00") < inicioDelDia() -> "La fecha no puede ser pasada"
            else -> ""
        }

        val errorHoraInicio = if (state.horaInicio.isEmpty()) "Selecciona la hora de inicio" else ""

        val errorHoraDevolucion = when {
            state.horaDevolucion.isEmpty() -> "Selecciona la hora de devolución"
            state.fechaMillis != 0L && state.horaInicio.isNotEmpty() &&
                armarMillis(state.horaDevolucion) <= armarMillis(state.horaInicio) ->
                "Debe ser posterior a la hora de inicio"
            else -> ""
        }

        val errorObservaciones = if (state.observaciones.length > 200) "Máximo 200 caracteres" else ""

        val advertencia = inicioMuyCercano()
        val hayErrores = errorSala.isNotEmpty() || errorFecha.isNotEmpty() ||
            errorHoraInicio.isNotEmpty() || errorHoraDevolucion.isNotEmpty() ||
            errorObservaciones.isNotEmpty() || advertencia

        uiState = state.copy(
            errorSala = errorSala,
            errorFecha = errorFecha,
            errorHoraInicio = errorHoraInicio,
            errorHoraDevolucion = errorHoraDevolucion,
            errorObservaciones = errorObservaciones,
            advertenciaAnticipacion = advertencia,
            errorTrigger = if (hayErrores) state.errorTrigger + 1 else state.errorTrigger
        )
        return !hayErrores
    }

    // Milisegundos de hoy a las 00:00
    private fun inicioDelDia(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    // Valida y pide confirmación antes de enviar
    fun onEnviarClick(context: Context) {
        if (inicioMuyCercano()) vibrar(context, 150)
        if (validar()) {
            uiState = uiState.copy(mostrarConfirmacion = true)
        }
    }

    // Envía la solicitud al microservicio, vacía el carrito y avisa a la pantalla
    fun onConfirmarEnvio(context: Context, onSuccess: () -> Unit) {
        uiState = uiState.copy(mostrarConfirmacion = false)
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)
            val usuarioId = Sesion.usuario?.id ?: return@launch
            val items = db.carritoDao().obtenerPorUsuario(usuarioId)
            if (items.isEmpty()) {
                Toast.makeText(context, "Tu carrito está vacío", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val solicitud = SolicitudEntity(
                usuarioId = usuarioId,
                sala = uiState.sala,
                fechaInicio = armarMillis(uiState.horaInicio),
                fechaDevolucion = armarMillis(uiState.horaDevolucion),
                asignatura = uiState.asignatura.trim(),
                observaciones = uiState.observaciones.trim(),
                estado = EstadoSolicitud.PENDIENTE,
                fechaCreacion = System.currentTimeMillis()
            )
            val detalles = items.map { item ->
                DetalleSolicitudEntity(
                    solicitudId = 0,
                    equipoId = item.equipoId,
                    nombreEquipo = item.nombreEquipo,
                    categoria = item.categoria,
                    cantidad = item.cantidad
                )
            }

            try {
                val solicitudId = PanolRepository.crearSolicitud(
                    solicitud,
                    Sesion.usuario?.nombre ?: "",
                    detalles
                )
                db.carritoDao().vaciarCarrito(usuarioId)
                Sesion.solicitudCreadaId = solicitudId
                onSuccess()
            } catch (e: Exception) {
                // Por ejemplo: la regla de 3 horas que valida también el servidor
                Toast.makeText(context, PanolRepository.mensajeDeError(e), Toast.LENGTH_LONG).show()
            }
        }
    }
}
