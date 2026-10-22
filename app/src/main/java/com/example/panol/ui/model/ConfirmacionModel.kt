package com.example.panol.ui.model

import com.example.panol.data.local.entity.DetalleSolicitudEntity
import com.example.panol.data.local.entity.SolicitudEntity
import com.example.panol.data.local.entity.UsuarioEntity

// Estado de la pantalla Confirmación
data class ConfirmacionModel(
    val solicitud: SolicitudEntity? = null,
    val detalles: List<DetalleSolicitudEntity> = emptyList(),
    val usuario: UsuarioEntity? = null
) {
    // fechas en formato dd/MM/yyyy HH:mm
    val inicioTexto: String
        get() = if (solicitud == null) "" else formatearFechaHora(solicitud.fechaInicio)

    val devolucionTexto: String
        get() = if (solicitud == null) "" else formatearFechaHora(solicitud.fechaDevolucion)
}
