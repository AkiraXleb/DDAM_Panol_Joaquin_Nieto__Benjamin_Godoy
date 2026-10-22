package com.example.panol.ui.model

import java.util.Calendar
import java.util.TimeZone

// Estado del Formulario de solicitud: valores de los campos y un error por campo
data class SolicitudModel(
    val sala: String = "",
    val fechaMillis: Long = 0L,             // fecha elegida en el DatePicker (0 = sin elegir)
    val horaInicio: String = "",            // formato HH:mm
    val horaDevolucion: String = "",        // formato HH:mm
    val asignatura: String = "",
    val observaciones: String = "",
    val errorSala: String = "",
    val errorFecha: String = "",
    val errorHoraInicio: String = "",
    val errorHoraDevolucion: String = "",
    val errorObservaciones: String = "",
    val advertenciaAnticipacion: Boolean = false,   // inicio antes de ahora + 3 horas
    val mostrarConfirmacion: Boolean = false,
    val errorTrigger: Int = 0               // sube cada vez que hay errores (dispara el temblor)
) {
    // fecha elegida como texto dd/MM/yyyy (el DatePicker entrega la fecha en UTC)
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
