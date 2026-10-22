package com.example.panol.ui.model

import com.example.panol.data.Constantes
import com.example.panol.data.local.entity.CarritoItemEntity
import com.example.panol.data.local.entity.EquipoEntity

// Estado de la pantalla Carrito
data class CarritoModel(
    val items: List<CarritoItemEntity> = emptyList(),
    val equipos: List<EquipoEntity> = emptyList()    // para conocer el stock de cada item
) {
    // cantidad de equipos distintos en el carrito
    val totalEquipos: Int
        get() = items.size

    // suma de todas las unidades
    val totalUnidades: Int
        get() = items.sumOf { it.cantidad }

    // máximo de un item: mín(3, stock disponible del equipo)
    fun maximoDe(item: CarritoItemEntity): Int {
        val stock = equipos.firstOrNull { it.id == item.equipoId }?.stockDisponible ?: 0
        return minOf(Constantes.MAX_UNIDADES_POR_EQUIPO, stock)
    }
}
