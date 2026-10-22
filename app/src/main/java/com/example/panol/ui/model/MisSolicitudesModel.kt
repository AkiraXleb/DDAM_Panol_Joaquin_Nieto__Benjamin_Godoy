package com.example.panol.ui.model

import com.example.panol.data.local.entity.DetalleSolicitudEntity
import com.example.panol.data.local.entity.SolicitudEntity

// Estado de la pantalla Mis Solicitudes
data class MisSolicitudesModel(
    val solicitudes: List<SolicitudEntity> = emptyList(),
    val detalles: List<DetalleSolicitudEntity> = emptyList(),   // equipos de todas las solicitudes
    val estadoSeleccionado: String = "",                        // vacío = todas
    val unidadesCarrito: Int = 0
) {
    // solicitudes que cumplen el filtro de estado
    val solicitudesFiltradas: List<SolicitudEntity>
        get() = solicitudes.filter { estadoSeleccionado.isEmpty() || it.estado == estadoSeleccionado }

    // equipos de una solicitud
    fun detallesDe(solicitudId: Int): List<DetalleSolicitudEntity> {
        return detalles.filter { it.solicitudId == solicitudId }
    }
}
