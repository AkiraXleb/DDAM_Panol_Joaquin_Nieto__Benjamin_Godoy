package com.example.panol.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panol.data.Sesion
import com.example.panol.data.database.AppDatabase
import com.example.panol.ui.model.ConfirmacionModel
import kotlinx.coroutines.launch

// ViewModel de la Confirmación: lee la solicitud recién creada
class ConfirmacionViewModel : ViewModel() {

    var uiState by mutableStateOf(ConfirmacionModel())
        private set

    // Lee la solicitud, sus equipos y el usuario de la sesión
    fun cargarSolicitud(context: Context) {
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)
            val id = Sesion.solicitudCreadaId
            uiState = ConfirmacionModel(
                solicitud = db.solicitudDao().obtenerPorId(id),
                detalles = db.solicitudDao().obtenerDetalles(id),
                usuario = Sesion.usuario
            )
        }
    }
}
