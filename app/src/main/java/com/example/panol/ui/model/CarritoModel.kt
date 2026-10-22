package com.example.panol.ui.model

import com.example.panol.data.Constantes
import com.example.panol.data.local.entity.CarritoItemEntity
import com.example.panol.data.local.entity.EquipoEntity

// Estado de la pantalla Carrito: items y equipos para conocer el stock
data class CarritoModel(
    val items: List<CarritoItemEntity> = emptyList(),
    val equipos: List<EquipoEntity> = emptyList()
) {
    // Cantidad de equipos distintos en el carrito
    val totalEquipos: Int
        get() = items.size

    // Suma de todas las unidades del carrito
    val totalUnidades: Int
        get() = items.sumOf { it.cantidad }

    // Máximo de un item: el menor entre 3 y el stock disponible
    fun maximoDe(item: CarritoItemEntity): Int {
        val stock = equipos.firstOrNull { it.id == item.equipoId }?.stockDisponible ?: 0
        return minOf(Constantes.MAX_UNIDADES_POR_EQUIPO, stock)
    }
}
