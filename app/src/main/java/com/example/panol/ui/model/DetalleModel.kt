package com.example.panol.ui.model

import com.example.panol.data.Constantes
import com.example.panol.data.local.entity.EquipoEntity

// Estado de la pantalla Detalle: equipo, cantidad y error
data class DetalleModel(
    val equipo: EquipoEntity? = null,
    val cantidad: Int = 1,
    val errorCantidad: String = ""
) {
    // Máximo que se puede pedir: el menor entre 3 y el stock disponible
    val maximo: Int
        get() = minOf(Constantes.MAX_UNIDADES_POR_EQUIPO, equipo?.stockDisponible ?: 0)
}
