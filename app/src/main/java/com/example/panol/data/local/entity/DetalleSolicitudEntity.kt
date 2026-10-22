package com.example.panol.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla con los equipos de cada solicitud (una solicitud puede tener varios equipos)
@Entity(tableName = "detalle_solicitud")
data class DetalleSolicitudEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val solicitudId: Int,
    val equipoId: Int,
    val nombreEquipo: String,
    val categoria: String,
    val cantidad: Int
)
