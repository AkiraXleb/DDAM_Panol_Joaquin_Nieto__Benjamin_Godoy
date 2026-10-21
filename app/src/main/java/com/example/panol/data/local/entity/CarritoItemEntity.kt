package com.example.panol.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla del carrito de solicitud (borrador antes de enviar la solicitud)
// Se guardan nombre, código y categoría del equipo para mostrarlos sin hacer otra consulta
@Entity(tableName = "carrito")
data class CarritoItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val usuarioId: Int,
    val equipoId: Int,
    val nombreEquipo: String,
    val codigoEquipo: String,
    val categoria: String,
    val cantidad: Int
)
