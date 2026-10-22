package com.example.panol.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.panol.data.local.entity.EquipoEntity

// Consultas de la tabla equipos
@Dao
interface EquipoDao {

    // inserta varios equipos (datos iniciales)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarEquipos(equipos: List<EquipoEntity>)

    // lista completa del catálogo
    @Query("SELECT * FROM equipos ORDER BY nombre")
    suspend fun obtenerTodos(): List<EquipoEntity>

    // un equipo por su id (pantalla detalle)
    @Query("SELECT * FROM equipos WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): EquipoEntity?
}
