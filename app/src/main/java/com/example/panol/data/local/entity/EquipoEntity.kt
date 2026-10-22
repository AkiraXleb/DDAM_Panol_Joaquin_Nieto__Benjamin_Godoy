package com.example.panol.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla de equipos del pañol: código, categoría, stock, estado y ubicación
@Entity(tableName = "equipos")
data class EquipoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val codigo: String,
    val nombre: String,
    val marca: String,
    val modelo: String,
    val categoria: String,
    val descripcion: String,
    val stockTotal: Int,
    val stockDisponible: Int,
    val estado: String,
    val ubicacion: String
)
