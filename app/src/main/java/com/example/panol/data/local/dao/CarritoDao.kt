package com.example.panol.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.panol.data.local.entity.CarritoItemEntity

// Consultas de la tabla carrito
@Dao
interface CarritoDao {

    // agrega un equipo al carrito
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarItem(item: CarritoItemEntity)

    // items del carrito del usuario conectado
    @Query("SELECT * FROM carrito WHERE usuarioId = :usuarioId")
    suspend fun obtenerPorUsuario(usuarioId: Int): List<CarritoItemEntity>

    // busca si el equipo ya está en el carrito (para sumar cantidad en vez de duplicar)
    @Query("SELECT * FROM carrito WHERE usuarioId = :usuarioId AND equipoId = :equipoId LIMIT 1")
    suspend fun buscarItem(usuarioId: Int, equipoId: Int): CarritoItemEntity?

    // cambia la cantidad de un item
    @Query("UPDATE carrito SET cantidad = :cantidad WHERE id = :id")
    suspend fun actualizarCantidad(id: Int, cantidad: Int)

    // elimina un item del carrito
    @Query("DELETE FROM carrito WHERE id = :id")
    suspend fun eliminarItem(id: Int)

    // vacía el carrito después de enviar la solicitud
    @Query("DELETE FROM carrito WHERE usuarioId = :usuarioId")
    suspend fun vaciarCarrito(usuarioId: Int)

    // total de unidades en el carrito (para el contador de la barra inferior)
    @Query("SELECT COALESCE(SUM(cantidad), 0) FROM carrito WHERE usuarioId = :usuarioId")
    suspend fun contarUnidades(usuarioId: Int): Int
}
