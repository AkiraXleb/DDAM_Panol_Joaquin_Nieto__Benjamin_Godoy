package com.example.panol.ui.model

import com.example.panol.data.EstadoEquipo
import com.example.panol.data.local.entity.EquipoEntity

// Estado de la pantalla Catálogo
data class CatalogoModel(
    val equipos: List<EquipoEntity> = emptyList(),   // todos los equipos de la BD
    val busqueda: String = "",
    val categoriaSeleccionada: String = "",          // vacío = todas las categorías
    val unidadesCarrito: Int = 0
) {
    // equipos que cumplen el buscador y el filtro de categoría
    val equiposFiltrados: List<EquipoEntity>
        get() = equipos.filter { equipo ->
            val coincideBusqueda = equipo.nombre.contains(busqueda.trim(), ignoreCase = true) ||
                equipo.descripcion.contains(busqueda.trim(), ignoreCase = true)
            val coincideCategoria = categoriaSeleccionada.isEmpty() ||
                equipo.categoria == categoriaSeleccionada
            coincideBusqueda && coincideCategoria
        }

    // cantidad de equipos que se pueden solicitar entre los que se ven
    val cantidadDisponibles: Int
        get() = equiposFiltrados.count {
            it.estado == EstadoEquipo.DISPONIBLE && it.stockDisponible > 0
        }
}
