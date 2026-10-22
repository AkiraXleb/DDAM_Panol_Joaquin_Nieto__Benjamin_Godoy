package com.example.panol.ui.model

import com.example.panol.data.Constantes
import com.example.panol.data.local.entity.EquipoEntity

// Estado de la pantalla Detalle
data class DetalleModel(
    val equipo: EquipoEntity? = null,
    val cantidad: Int = 1,
    val errorCantidad: String = ""
) {
    // máximo que se puede pedir: mín(3, stock disponible)
    val maximo: Int
        get() = minOf(Constantes.MAX_UNIDADES_POR_EQUIPO, equipo?.stockDisponible ?: 0)
}
