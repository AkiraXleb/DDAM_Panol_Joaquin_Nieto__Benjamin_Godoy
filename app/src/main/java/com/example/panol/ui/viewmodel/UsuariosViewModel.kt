package com.example.panol.ui.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panol.data.Constantes
import com.example.panol.data.local.entity.UsuarioEntity
import com.example.panol.data.repository.PanolRepository
import com.example.panol.ui.model.UsuariosModel
import com.example.panol.ui.screen.vibrar
import kotlinx.coroutines.launch

// ViewModel de Usuarios (Super Admin): lista, agrega y elimina usuarios en el servidor
class UsuariosViewModel : ViewModel() {

    var uiState by mutableStateOf(UsuariosModel())
        private set

    // Lee todos los usuarios del microservicio
    fun cargar(context: Context) {
        viewModelScope.launch {
            uiState = uiState.copy(cargando = true)
            try {
                uiState = uiState.copy(usuarios = PanolRepository.usuarios(), cargando = false)
            } catch (e: Exception) {
                uiState = uiState.copy(cargando = false)
                Toast.makeText(context, PanolRepository.mensajeDeError(e), Toast.LENGTH_LONG).show()
            }
        }
    }

    // Abre el formulario con los campos vacíos
    fun onNuevoClick() {
        uiState = uiState.copy(
            mostrarFormulario = true,
            nombre = "", correo = "", password = "", departamento = "",
            errorNombre = "", errorCorreo = "", errorPassword = "", errorDepartamento = "", errorGeneral = ""
        )
    }

    // Cierra el formulario
    fun onCancelarNuevo() {
        uiState = uiState.copy(mostrarFormulario = false)
    }

    fun onNombreChanged(valor: String) {
        uiState = uiState.copy(nombre = valor, errorNombre = "", errorGeneral = "")
    }

    fun onCorreoChanged(valor: String) {
        uiState = uiState.copy(correo = valor, errorCorreo = "", errorGeneral = "")
    }

    fun onPasswordChanged(valor: String) {
        uiState = uiState.copy(password = valor, errorPassword = "", errorGeneral = "")
    }

    fun onDepartamentoChanged(valor: String) {
        uiState = uiState.copy(departamento = valor, errorDepartamento = "", errorGeneral = "")
    }

    // Valida cada campo y devuelve true si no hay errores
    private fun validar(): Boolean {
        val state = uiState
        val correo = state.correo.trim().lowercase()

        val errorNombre = if (state.nombre.isBlank()) "Ingresa el nombre" else ""
        val errorCorreo = when {
            correo.isEmpty() -> "Ingresa el correo"
            correo.count { it == '@' } != 1 || correo.startsWith("@") -> "El formato del correo no es válido"
            !correo.endsWith(Constantes.DOMINIO_CORREO) -> "Debe terminar en ${Constantes.DOMINIO_CORREO}"
            !Constantes.correoValido(correo) -> "Usa prefijo.nombre${Constantes.DOMINIO_CORREO} (prof, insp, ti o admin)"
            else -> ""
        }
        val errorPassword = if (state.password.length < 6) "La contraseña debe tener al menos 6 caracteres" else ""
        val errorDepartamento = if (state.departamento.isBlank()) "Ingresa el departamento" else ""

        uiState = state.copy(
            errorNombre = errorNombre,
            errorCorreo = errorCorreo,
            errorPassword = errorPassword,
            errorDepartamento = errorDepartamento
        )
        return errorNombre.isEmpty() && errorCorreo.isEmpty() &&
            errorPassword.isEmpty() && errorDepartamento.isEmpty()
    }

    // Valida y crea el usuario. El cargo lo define el servidor según el prefijo del correo
    fun onGuardarClick(context: Context) {
        if (!validar()) return
        val state = uiState
        viewModelScope.launch {
            try {
                val creado = PanolRepository.crearUsuario(
                    state.nombre.trim(),
                    state.correo.trim().lowercase(),
                    state.password,
                    state.departamento.trim()
                )
                uiState = uiState.copy(mostrarFormulario = false)
                vibrar(context, 150)
                Toast.makeText(context, "${creado.nombre} agregado como ${creado.cargo}", Toast.LENGTH_SHORT).show()
                cargar(context)
            } catch (e: Exception) {
                uiState = uiState.copy(errorGeneral = PanolRepository.mensajeDeError(e))
            }
        }
    }

    // Elimina un usuario y recarga la lista
    fun onEliminarClick(context: Context, usuario: UsuarioEntity) {
        viewModelScope.launch {
            try {
                PanolRepository.eliminarUsuario(usuario.id)
                vibrar(context, 150)
                Toast.makeText(context, "${usuario.nombre} eliminado", Toast.LENGTH_SHORT).show()
                cargar(context)
            } catch (e: Exception) {
                Toast.makeText(context, PanolRepository.mensajeDeError(e), Toast.LENGTH_LONG).show()
            }
        }
    }
}
