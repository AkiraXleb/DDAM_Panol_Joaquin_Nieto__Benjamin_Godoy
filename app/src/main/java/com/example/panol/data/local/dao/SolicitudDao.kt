package com.example.panol.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.panol.data.local.entity.DetalleSolicitudEntity
import com.example.panol.data.local.entity.SolicitudEntity

/*
 * Consultas de las tablas solicitudes y detalle_solicitud.
 *
 * - insertarSolicitud: Crea la solicitud y devuelve el id generado (para asociar el detalle).
 * - insertarDetalles: Guarda los equipos de la solicitud.
 * - obtenerPorUsuario: Solicitudes del usuario, las más recientes primero.
 * - obtenerPorId: Una solicitud por id (pantalla confirmación).
 * - obtenerDetalles: Equipos de una solicitud.
 * - actualizarEstado: El solicitante puede cancelar una solicitud pendiente.
 * - obtenerSinNotificar: Solicitudes aceptadas o rechazadas que aún no se le avisan al usuario.
 * - marcarNotificada: Marca la solicitud como notificada (log de notificación).
 */
@Dao
interface SolicitudDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarSolicitud(solicitud: SolicitudEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarDetalles(detalles: List<DetalleSolicitudEntity>)

    @Query("SELECT * FROM solicitudes WHERE usuarioId = :usuarioId ORDER BY fechaCreacion DESC")
    suspend fun obtenerPorUsuario(usuarioId: Int): List<SolicitudEntity>

    @Query("SELECT * FROM solicitudes WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): SolicitudEntity?

    @Query("SELECT * FROM detalle_solicitud WHERE solicitudId = :solicitudId")
    suspend fun obtenerDetalles(solicitudId: Int): List<DetalleSolicitudEntity>

    @Query("UPDATE solicitudes SET estado = :estado WHERE id = :id")
    suspend fun actualizarEstado(id: Int, estado: String)

    @Query("SELECT * FROM solicitudes WHERE usuarioId = :usuarioId AND notificada = 0 AND (estado = 'Aceptada' OR estado = 'Rechazada')")
    suspend fun obtenerSinNotificar(usuarioId: Int): List<SolicitudEntity>

    @Query("UPDATE solicitudes SET notificada = 1 WHERE id = :id")
    suspend fun marcarNotificada(id: Int)
}
