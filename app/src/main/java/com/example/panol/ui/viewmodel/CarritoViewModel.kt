package com.example.panol.ui.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panol.data.Sesion
import com.example.panol.data.database.AppDatabase
import com.example.panol.data.local.entity.CarritoItemEntity
import com.example.panol.data.repository.PanolRepository
import com.example.panol.ui.model.CarritoModel
import kotlinx.coroutines.launch

// ViewModel del Carrito: cantidades y eliminación de items
class CarritoViewModel : ViewModel() {

    var uiState by mutableStateOf(CarritoModel())
        private set

    // Lee los items del carrito desde Room y el stock actual desde el microservicio
    fun cargarCarrito(context: Context) {
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)
            val usuarioId = Sesion.usuario?.id ?: return@launch
            val items = db.carritoDao().obtenerPorUsuario(usuarioId)
            try {
                uiState = uiState.copy(items = items, equipos = PanolRepository.equipos())
            } catch (e: Exception) {
                uiState = uiState.copy(items = items)
                Toast.makeText(context, PanolRepository.mensajeDeError(e), Toast.LENGTH_LONG).show()
            }
        }
    }

    // Suma 1 unidad sin pasar el máximo
    fun onMasClick(context: Context, item: CarritoItemEntity) {
        val maximo = uiState.maximoDe(item)
        if (item.cantidad >= maximo) {
            Toast.makeText(context, "El máximo es $maximo unidades", Toast.LENGTH_SHORT).show()
            return
        }
        cambiarCantidad(context, item, item.cantidad + 1)
    }

    // Resta 1 unidad, con mínimo 1
    fun onMenosClick(context: Context, item: CarritoItemEntity) {
        if (item.cantidad <= 1) {
            Toast.makeText(context, "La cantidad mínima es 1", Toast.LENGTH_SHORT).show()
            return
        }
        cambiarCantidad(context, item, item.cantidad - 1)
    }

    // Guarda la nueva cantidad y recarga el carrito
    private fun cambiarCantidad(context: Context, item: CarritoItemEntity, cantidad: Int) {
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)
            db.carritoDao().actualizarCantidad(item.id, cantidad)
            cargarCarrito(context)
        }
    }

    // Elimina el item después de confirmar
    fun onEliminarConfirmado(context: Context, item: CarritoItemEntity) {
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)
            db.carritoDao().eliminarItem(item.id)
            cargarCarrito(context)
        }
    }
}
