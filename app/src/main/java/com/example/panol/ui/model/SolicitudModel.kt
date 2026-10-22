package com.example.panol.ui.model

import java.util.Calendar
import java.util.TimeZone

// Estado del Formulario: campos, un error por campo, advertencia de 3 horas y contador del temblor
data class SolicitudModel(
    val sala: String = "",
    val fechaMillis: Long = 0L,
    val horaInicio: String = "",
    val horaDevolucion: String = "",
    val asignatura: String = "",
    val observaciones: String = "",
    val errorSala: String = "",
    val errorFecha: String = "",
    val errorHoraInicio: String = "",
    val errorHoraDevolucion: String = "",
    val errorObservaciones: String = "",
    val advertenciaAnticipacion: Boolean = false,
    val mostrarConfirmacion: Boolean = false,
    val errorTrigger: Int = 0
) {
    // Fecha elegida como texto dd/MM/yyyy
    val fechaTexto: String
        get() {
            if (fechaMillis == 0L) return ""
            val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            cal.timeInMillis = fechaMillis
            return String.format(
                "%02d/%02d/%04d",
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.YEAR)
            )
        }
}
