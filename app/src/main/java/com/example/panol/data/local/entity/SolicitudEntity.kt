package com.example.panol.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla de solicitudes (ticket de préstamo)
// Las fechas se guardan en milisegundos (System.currentTimeMillis())
@Entity(tableName = "solicitudes")
data class SolicitudEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val usuarioId: Int,
    val sala: String,
    val fechaInicio: Long,              // fecha + hora de inicio de uso
    val fechaDevolucion: Long,          // fecha + hora estimada de devolución
    val asignatura: String = "",
    val observaciones: String = "",
    val estado: String,                 // Pendiente, Aceptada, Rechazada, Entregada, Devuelta
    val comentarioGestion: String = "", // justificación del encargado TI (obligatoria si rechaza)
    val fechaCreacion: Long,            // trazabilidad: creación del ticket
    val fechaEntrega: Long? = null,     // trazabilidad: entrega física
    val fechaRecepcion: Long? = null,   // trazabilidad: recepción de la devolución
    val notificada: Boolean = false     // log de notificación: si ya se avisó al solicitante
)
