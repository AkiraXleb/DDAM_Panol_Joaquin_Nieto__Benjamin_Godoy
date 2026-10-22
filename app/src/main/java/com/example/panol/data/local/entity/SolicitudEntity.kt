package com.example.panol.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla de solicitudes: sala, fechas en milisegundos, estado y comentario del encargado
@Entity(tableName = "solicitudes")
data class SolicitudEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val usuarioId: Int,
    val sala: String,
    val fechaInicio: Long,
    val fechaDevolucion: Long,
    val asignatura: String = "",
    val observaciones: String = "",
    val estado: String,
    val comentarioGestion: String = "",
    val fechaCreacion: Long,
    val fechaEntrega: Long? = null,
    val fechaRecepcion: Long? = null,
    val notificada: Boolean = false
)
