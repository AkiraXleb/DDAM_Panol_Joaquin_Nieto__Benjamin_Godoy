package com.example.panol.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla del carrito: equipo, nombre, código, categoría y cantidad antes de enviar la solicitud
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
