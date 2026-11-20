package com.example.panol.ui.model

import com.example.panol.data.EstadoSolicitud
import com.example.panol.data.local.entity.DetalleSolicitudEntity
import com.example.panol.data.local.entity.SolicitudEntity

/*
 * Estado de la pantalla Gestión (Encargado TI): todas las solicitudes, filtro y errores.
 *
 * - solicitudesFiltradas: Solicitudes que cumplen el filtro de estado.
 * - pendientes: Cantidad de solicitudes esperando respuesta.
 * - detallesDe: Equipos de una solicitud.
 * - solicitanteDe: Nombre del docente que hizo la solicitud.
 */
data class GestionModel(
    val solicitudes: List<SolicitudEntity> = emptyList(),
    val detalles: List<DetalleSolicitudEntity> = emptyList(),
    val nombres: Map<Int, String> = emptyMap(),
    val estadoSeleccionado: String = EstadoSolicitud.PENDIENTE,
    val cargando: Boolean = false,
    val errorMotivo: String = ""
) {
    val solicitudesFiltradas: List<SolicitudEntity>
        get() = solicitudes.filter { estadoSeleccionado.isEmpty() || it.estado == estadoSeleccionado }

    val pendientes: Int
        get() = solicitudes.count { it.estado == EstadoSolicitud.PENDIENTE }

    fun detallesDe(solicitudId: Int): List<DetalleSolicitudEntity> {
        return detalles.filter { it.solicitudId == solicitudId }
    }

    fun solicitanteDe(solicitudId: Int): String {
        return nombres[solicitudId].orEmpty().ifEmpty { "Sin nombre" }
    }
}
