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
import com.example.panol.ui.model.CatalogoModel
import kotlinx.coroutines.launch

class CatalogoViewModel : ViewModel() {

    var uiState by mutableStateOf(CatalogoModel())
        private set

    // Lee los equipos y el contador del carrito desde Room
    fun cargarDatos(context: Context) {
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)
            val usuarioId = Sesion.usuario?.id ?: 0
            uiState = uiState.copy(
                equipos = db.equipoDao().obtenerTodos(),
                unidadesCarrito = db.carritoDao().contarUnidades(usuarioId)
            )
        }
    }

    fun onBusquedaChanged(valor: String) {
        uiState = uiState.copy(busqueda = valor)
    }

    fun onTodosClick() {
        uiState = uiState.copy(categoriaSeleccionada = "")
    }

    // Si se toca la categoría ya seleccionada, se quita el filtro
    fun onCategoriaClick(categoria: String) {
        val nueva = if (uiState.categoriaSeleccionada == categoria) "" else categoria
        uiState = uiState.copy(categoriaSeleccionada = nueva)
    }

    // Agrega 1 unidad al carrito (o suma 1 si ya existe), respetando el máximo
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
