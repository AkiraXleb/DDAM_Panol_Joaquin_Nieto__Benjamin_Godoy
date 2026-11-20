package com.example.panol.ui.model

import com.example.panol.data.Constantes
import com.example.panol.data.local.entity.CarritoItemEntity
import com.example.panol.data.local.entity.EquipoEntity

/*
 * Estado de la pantalla Carrito: items y equipos para conocer el stock.
 *
 * - totalEquipos: Cantidad de equipos distintos en el carrito.
 * - totalUnidades: Suma de todas las unidades del carrito.
 * - maximoDe: Máximo de un item: el menor entre 3 y el stock disponible.
 */
data class CarritoModel(
    val items: List<CarritoItemEntity> = emptyList(),
    val equipos: List<EquipoEntity> = emptyList()
) {
    val totalEquipos: Int
        get() = items.size

    val totalUnidades: Int
        get() = items.sumOf { it.cantidad }

    fun maximoDe(item: CarritoItemEntity): Int {
        val stock = equipos.firstOrNull { it.id == item.equipoId }?.stockDisponible ?: 0
        return minOf(Constantes.MAX_UNIDADES_POR_EQUIPO, stock)
    }
}
