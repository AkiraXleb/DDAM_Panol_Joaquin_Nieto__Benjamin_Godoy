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
import com.example.panol.ui.model.DetalleModel
import kotlinx.coroutines.launch

// ViewModel del Detalle: cantidad y agregar al carrito
class DetalleViewModel : ViewModel() {

    var uiState by mutableStateOf(DetalleModel())
        private set

    // Lee el equipo elegido en el Catálogo
    fun cargarEquipo(context: Context) {
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)
            uiState = DetalleModel(equipo = db.equipoDao().obtenerPorId(Sesion.equipoSeleccionadoId))
        }
    }

    // Resta 1 a la cantidad, con mínimo 1
    fun onMenosClick() {
        if (uiState.cantidad > 1) {
            uiState = uiState.copy(cantidad = uiState.cantidad - 1, errorCantidad = "")
        } else {
            uiState = uiState.copy(errorCantidad = "La cantidad mínima es 1")
        }
    }

    // Suma 1 a la cantidad sin pasar el máximo
    fun onMasClick() {
        if (uiState.cantidad < uiState.maximo) {
            uiState = uiState.copy(cantidad = uiState.cantidad + 1, errorCantidad = "")
        } else {
            uiState = uiState.copy(errorCantidad = "La cantidad máxima es ${uiState.maximo}")
        }
    }

    // Valida la cantidad y la agrega al carrito
    fun onAgregarClick(context: Context, onSuccess: () -> Unit) {
        val equipo = uiState.equipo ?: return
        val maximo = uiState.maximo

        if (maximo < 1) {
            uiState = uiState.copy(errorCantidad = "No hay stock disponible")
            return
        }
        if (uiState.cantidad < 1 || uiState.cantidad > maximo) {
            uiState = uiState.copy(errorCantidad = "La cantidad debe estar entre 1 y $maximo")
            return
        }

        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)
            val usuarioId = Sesion.usuario?.id ?: return@launch
            val existente = db.carritoDao().buscarItem(usuarioId, equipo.id)

            if (existente == null) {
                db.carritoDao().insertarItem(
                    CarritoItemEntity(
                        usuarioId = usuarioId,
                        equipoId = equipo.id,
                        nombreEquipo = equipo.nombre,
                        codigoEquipo = equipo.codigo,
                        categoria = equipo.categoria,
                        cantidad = uiState.cantidad
                    )
                )
            } else if (existente.cantidad + uiState.cantidad <= maximo) {
                db.carritoDao().actualizarCantidad(existente.id, existente.cantidad + uiState.cantidad)
            } else {
                uiState = uiState.copy(
                    errorCantidad = "Ya tienes ${existente.cantidad} en el carrito. El máximo es $maximo"
                )
                return@launch
            }
            Toast.makeText(context, "${equipo.nombre} agregado al carrito", Toast.LENGTH_SHORT).show()
            onSuccess()
        }
    }
}
