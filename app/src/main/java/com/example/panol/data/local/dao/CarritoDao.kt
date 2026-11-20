package com.example.panol.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.panol.data.local.entity.CarritoItemEntity

/*
 * Consultas de la tabla carrito.
 *
 * - insertarItem: Agrega un equipo al carrito.
 * - obtenerPorUsuario: Items del carrito del usuario conectado.
 * - buscarItem: Busca si el equipo ya está en el carrito (para sumar cantidad en vez de duplicar).
 * - actualizarCantidad: Cambia la cantidad de un item.
 * - eliminarItem: Elimina un item del carrito.
 * - vaciarCarrito: Vacía el carrito después de enviar la solicitud.
 * - contarUnidades: Total de unidades en el carrito (para el contador de la barra inferior).
 */
@Dao
interface CarritoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarItem(item: CarritoItemEntity)

    @Query("SELECT * FROM carrito WHERE usuarioId = :usuarioId")
    suspend fun obtenerPorUsuario(usuarioId: Int): List<CarritoItemEntity>

    @Query("SELECT * FROM carrito WHERE usuarioId = :usuarioId AND equipoId = :equipoId LIMIT 1")
    suspend fun buscarItem(usuarioId: Int, equipoId: Int): CarritoItemEntity?

    @Query("UPDATE carrito SET cantidad = :cantidad WHERE id = :id")
    suspend fun actualizarCantidad(id: Int, cantidad: Int)

    @Query("DELETE FROM carrito WHERE id = :id")
    suspend fun eliminarItem(id: Int)

    @Query("DELETE FROM carrito WHERE usuarioId = :usuarioId")
    suspend fun vaciarCarrito(usuarioId: Int)

    @Query("SELECT COALESCE(SUM(cantidad), 0) FROM carrito WHERE usuarioId = :usuarioId")
    suspend fun contarUnidades(usuarioId: Int): Int
}
