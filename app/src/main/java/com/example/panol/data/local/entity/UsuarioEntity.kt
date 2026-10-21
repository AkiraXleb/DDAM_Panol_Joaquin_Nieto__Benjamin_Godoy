package com.example.panol.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla de usuarios: solicitantes (docentes, inspectoría, UTP, dirección) y encargado TI
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val email: String,
    val password: String,
    val cargo: String,          // Docente, Inspector, UTP, Directivo, Encargado TI
    val departamento: String
)
