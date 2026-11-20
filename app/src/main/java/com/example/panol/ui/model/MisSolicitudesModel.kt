package com.example.panol.ui.model

import com.example.panol.data.local.entity.DetalleSolicitudEntity
import com.example.panol.data.local.entity.SolicitudEntity

/*
 * Estado de Mis Solicitudes: solicitudes, sus equipos y el filtro de estado.
 *
 * - solicitudesFiltradas: Solicitudes que cumplen el filtro de estado.
 * - detallesDe: Equipos de una solicitud.
 */
data class MisSolicitudesModel(
    val solicitudes: List<SolicitudEntity> = emptyList(),
    val detalles: List<DetalleSolicitudEntity> = emptyList(),
    val estadoSeleccionado: String = "",
    val unidadesCarrito: Int = 0
) {
    val solicitudesFiltradas: List<SolicitudEntity>
        get() = solicitudes.filter { estadoSeleccionado.isEmpty() || it.estado == estadoSeleccionado }

    fun detallesDe(solicitudId: Int): List<DetalleSolicitudEntity> {
        return detalles.filter { it.solicitudId == solicitudId }
    }
}
