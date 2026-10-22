package com.example.panol.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla de equipos del pañol (inventario)
@Entity(tableName = "equipos")
data class EquipoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val codigo: String,             // código interno, ej: NOT-2024-08
    val nombre: String,
    val marca: String,
    val modelo: String,
    val categoria: String,          // Notebook, Proyector, Parlante, Robótica, etc.
    val descripcion: String,
    val stockTotal: Int,
    val stockDisponible: Int,
    val estado: String,             // Disponible, Reservado, Prestado, En Mantenimiento, Fuera de Servicio
    val ubicacion: String           // ubicación física en el pañol o ubicación técnica (switches/routers)
)
