package com.example.panol.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla del usuario con sesión iniciada: los usuarios reales viven en el servidor (usuarios-service)
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val email: String,
    val password: String,
    val cargo: String,
    val departamento: String
)
