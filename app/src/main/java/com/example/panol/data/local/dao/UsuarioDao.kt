package com.example.panol.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.panol.data.local.entity.UsuarioEntity

// Consultas de la tabla usuarios
@Dao
interface UsuarioDao {

    // registra un usuario en la base de datos
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarUsuario(usuario: UsuarioEntity)

    // busca un usuario por correo (login)
    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun obtenerPorEmail(email: String): UsuarioEntity?

    // cuenta los usuarios (para saber si hay que cargar datos iniciales)
    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contarUsuarios(): Int
}
