package com.example.panol.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.panol.data.local.entity.UsuarioEntity

/*
 * Consultas de la tabla usuarios (guarda el usuario con sesión iniciada).
 *
 * - insertarUsuario: Registra un usuario en la base de datos.
 * - obtenerPorEmail: Busca un usuario por correo (login).
 * - contarUsuarios: Cuenta los usuarios.
 * - obtenerSesion: Devuelve el usuario con sesión iniciada (la tabla guarda solo uno), o null si no
 *   hay.
 * - borrarTodos: Borra la sesión guardada (cerrar sesión o antes de guardar un login nuevo).
 */
@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarUsuario(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun obtenerPorEmail(email: String): UsuarioEntity?

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contarUsuarios(): Int

    @Query("SELECT * FROM usuarios LIMIT 1")
    suspend fun obtenerSesion(): UsuarioEntity?

    @Query("DELETE FROM usuarios")
    suspend fun borrarTodos()
}
