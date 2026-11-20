package com.example.panol.ui.model

import com.example.panol.data.EstadoEquipo
import com.example.panol.data.local.entity.EquipoEntity

/*
 * Estado de la pantalla Catálogo: equipos, búsqueda y categoría elegida.
 *
 * - equiposFiltrados: Equipos que cumplen la búsqueda y la categoría.
 * - cantidadDisponibles: Cantidad de equipos disponibles entre los que se ven.
 */
data class CatalogoModel(
    val equipos: List<EquipoEntity> = emptyList(),
    val busqueda: String = "",
    val categoriaSeleccionada: String = "",
    val unidadesCarrito: Int = 0,
    val actualizando: Boolean = false,
    val cargando: Boolean = true,
    val error: String = ""
) {
    val equiposFiltrados: List<EquipoEntity>
        get() = equipos.filter { equipo ->
            val coincideBusqueda = equipo.nombre.contains(busqueda.trim(), ignoreCase = true) ||
                equipo.descripcion.contains(busqueda.trim(), ignoreCase = true)
            val coincideCategoria = categoriaSeleccionada.isEmpty() ||
                equipo.categoria == categoriaSeleccionada
            coincideBusqueda && coincideCategoria
        }

    val cantidadDisponibles: Int
        get() = equiposFiltrados.count {
            it.estado == EstadoEquipo.DISPONIBLE && it.stockDisponible > 0
        }
}
