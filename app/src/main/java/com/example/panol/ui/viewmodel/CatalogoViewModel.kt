package com.example.panol.ui.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panol.data.Constantes
import com.example.panol.data.Sesion
import com.example.panol.data.database.AppDatabase
import com.example.panol.data.local.entity.CarritoItemEntity
import com.example.panol.data.local.entity.EquipoEntity
import com.example.panol.data.repository.PanolRepository
import com.example.panol.ui.model.CatalogoModel
import kotlinx.coroutines.launch

// ViewModel del Catálogo: búsqueda, filtros y agregar al carrito
class CatalogoViewModel : ViewModel() {

    var uiState by mutableStateOf(CatalogoModel())
        private set

    // Lee los equipos desde el microservicio y el contador del carrito desde Room
    // manual = true cuando el usuario arrastra hacia abajo para recargar
    fun cargarDatos(context: Context, manual: Boolean = false) {
        viewModelScope.launch {
            uiState = uiState.copy(cargando = true)
            if (manual) uiState = uiState.copy(actualizando = true)
            val db = AppDatabase.getDatabase(context)
            val usuarioId = Sesion.usuario?.id ?: 0
            val unidades = db.carritoDao().contarUnidades(usuarioId)
            try {
                val equipos = PanolRepository.equipos()
                uiState = uiState.copy(equipos = equipos, unidadesCarrito = unidades, error = "")
            } catch (e: Exception) {
                val mensaje = PanolRepository.mensajeDeError(e)
                uiState = uiState.copy(unidadesCarrito = unidades, error = mensaje)
                // Si ya hay equipos en pantalla solo avisa, si no se muestra la tarjeta con "Reintentar"
                if (uiState.equipos.isNotEmpty()) {
                    Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
                }
            } finally {
                uiState = uiState.copy(actualizando = false, cargando = false)
            }
        }
    }

    // Actualiza el texto del buscador
    fun onBusquedaChanged(valor: String) {
        uiState = uiState.copy(busqueda = valor)
    }

    // Quita el filtro de categoría
    fun onTodosClick() {
        uiState = uiState.copy(categoriaSeleccionada = "")
    }

    // Filtra por categoría, o quita el filtro si ya estaba elegida
    fun onCategoriaClick(categoria: String) {
        val nueva = if (uiState.categoriaSeleccionada == categoria) "" else categoria
        uiState = uiState.copy(categoriaSeleccionada = nueva)
    }

    // Agrega 1 unidad al carrito respetando el máximo
    fun onAgregarClick(context: Context, equipo: EquipoEntity) {
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)
            val usuarioId = Sesion.usuario?.id ?: return@launch
            val maximo = minOf(Constantes.MAX_UNIDADES_POR_EQUIPO, equipo.stockDisponible)
            val existente = db.carritoDao().buscarItem(usuarioId, equipo.id)

            if (existente == null) {
                db.carritoDao().insertarItem(
                    CarritoItemEntity(
                        usuarioId = usuarioId,
                        equipoId = equipo.id,
                        nombreEquipo = equipo.nombre,
                        codigoEquipo = equipo.codigo,
                        categoria = equipo.categoria,
                        cantidad = 1
                    )
                )
                Toast.makeText(context, "${equipo.nombre} agregado al carrito", Toast.LENGTH_SHORT).show()
            } else if (existente.cantidad < maximo) {
                db.carritoDao().actualizarCantidad(existente.id, existente.cantidad + 1)
                Toast.makeText(context, "${equipo.nombre} agregado al carrito", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Ya alcanzaste el máximo de $maximo unidades", Toast.LENGTH_SHORT).show()
            }
            uiState = uiState.copy(unidadesCarrito = db.carritoDao().contarUnidades(usuarioId))
        }
    }
}
