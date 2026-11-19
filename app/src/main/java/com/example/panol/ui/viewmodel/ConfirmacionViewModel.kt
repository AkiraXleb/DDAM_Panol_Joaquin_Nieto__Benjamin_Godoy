package com.example.panol.ui.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panol.data.Sesion
import com.example.panol.data.repository.PanolRepository
import com.example.panol.ui.model.ConfirmacionModel
import kotlinx.coroutines.launch

// ViewModel de la Confirmación: lee la solicitud recién creada desde el microservicio
class ConfirmacionViewModel : ViewModel() {

    var uiState by mutableStateOf(ConfirmacionModel())
        private set

    // Lee la solicitud, sus equipos y el usuario de la sesión
    fun cargarSolicitud(context: Context) {
        viewModelScope.launch {
            try {
                val resultado = PanolRepository.solicitud(Sesion.solicitudCreadaId)
                uiState = ConfirmacionModel(
                    solicitud = resultado.solicitud,
                    detalles = resultado.detalles,
                    usuario = Sesion.usuario
                )
            } catch (e: Exception) {
                Toast.makeText(context, PanolRepository.mensajeDeError(e), Toast.LENGTH_LONG).show()
            }
        }
    }
}
