package com.example.panol.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.panol.data.local.entity.UsuarioEntity

// Consultas de la tabla usuarios (guarda el usuario con sesión iniciada)
@Dao
interface UsuarioDao {

    // registra un usuario en la base de datos
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarUsuario(usuario: UsuarioEntity)

    // busca un usuario por correo (login)
    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun obtenerPorEmail(email: String): UsuarioEntity?

    // cuenta los usuarios
    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contarUsuarios(): Int

    // devuelve el usuario con sesión iniciada (la tabla guarda solo uno), o null si no hay
    @Query("SELECT * FROM usuarios LIMIT 1")
    suspend fun obtenerSesion(): UsuarioEntity?

    // borra la sesión guardada (cerrar sesión o antes de guardar un login nuevo)
    @Query("DELETE FROM usuarios")
    suspend fun borrarTodos()
}
