package com.example.panol.ui.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Convierte milisegundos a texto dd/MM/yyyy HH:mm
fun formatearFechaHora(millis: Long): String {
    return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(millis))
}
