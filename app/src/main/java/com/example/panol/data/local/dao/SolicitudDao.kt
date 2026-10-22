package com.example.panol.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.panol.data.local.entity.DetalleSolicitudEntity
import com.example.panol.data.local.entity.SolicitudEntity

// Consultas de las tablas solicitudes y detalle_solicitud
@Dao
interface SolicitudDao {

    // crea la solicitud y devuelve el id generado (para asociar el detalle)
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarSolicitud(solicitud: SolicitudEntity): Long

    // guarda los equipos de la solicitud
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarDetalles(detalles: List<DetalleSolicitudEntity>)

    // solicitudes del usuario, las más recientes primero
    @Query("SELECT * FROM solicitudes WHERE usuarioId = :usuarioId ORDER BY fechaCreacion DESC")
    suspend fun obtenerPorUsuario(usuarioId: Int): List<SolicitudEntity>

    // una solicitud por id (pantalla confirmación)
    @Query("SELECT * FROM solicitudes WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): SolicitudEntity?

    // equipos de una solicitud
    @Query("SELECT * FROM detalle_solicitud WHERE solicitudId = :solicitudId")
    suspend fun obtenerDetalles(solicitudId: Int): List<DetalleSolicitudEntity>

    // el solicitante puede cancelar una solicitud pendiente
    @Query("UPDATE solicitudes SET estado = :estado WHERE id = :id")
    suspend fun actualizarEstado(id: Int, estado: String)

    // solicitudes aceptadas o rechazadas que aún no se le avisan al usuario
    @Query("SELECT * FROM solicitudes WHERE usuarioId = :usuarioId AND notificada = 0 AND (estado = 'Aceptada' OR estado = 'Rechazada')")
    suspend fun obtenerSinNotificar(usuarioId: Int): List<SolicitudEntity>

    // marca la solicitud como notificada (log de notificación)
    @Query("UPDATE solicitudes SET notificada = 1 WHERE id = :id")
    suspend fun marcarNotificada(id: Int)
}
