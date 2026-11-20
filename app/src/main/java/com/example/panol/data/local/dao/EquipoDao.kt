package com.example.panol.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.panol.data.local.entity.EquipoEntity

/*
 * Consultas de la tabla equipos.
 *
 * - insertarEquipos: Inserta varios equipos (datos iniciales).
 * - obtenerTodos: Lista completa del catálogo.
 * - obtenerPorId: Un equipo por su id (pantalla detalle).
 */
@Dao
interface EquipoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarEquipos(equipos: List<EquipoEntity>)

    @Query("SELECT * FROM equipos ORDER BY nombre")
    suspend fun obtenerTodos(): List<EquipoEntity>

    @Query("SELECT * FROM equipos WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): EquipoEntity?
}
