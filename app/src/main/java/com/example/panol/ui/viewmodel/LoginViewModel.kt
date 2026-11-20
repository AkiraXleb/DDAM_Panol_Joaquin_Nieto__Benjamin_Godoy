package com.example.panol.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panol.data.Constantes
import com.example.panol.data.Sesion
import com.example.panol.data.database.AppDatabase
import com.example.panol.data.repository.PanolRepository
import com.example.panol.ui.model.LoginModel
import kotlinx.coroutines.launch
import retrofit2.HttpException

// ViewModel del Login: validación de campos, login contra el servidor y sesión guardada en Room
class LoginViewModel : ViewModel() {

    var uiState by mutableStateOf(LoginModel())
        private set

    // Actualiza el correo y limpia sus errores
    fun onCorreoChanged(valor: String) {
        uiState = uiState.copy(correo = valor, errorCorreo = "", errorGeneral = "")
    }

    // Actualiza la contraseña y limpia sus errores
    fun onPasswordChanged(valor: String) {
        uiState = uiState.copy(password = valor, errorPassword = "", errorGeneral = "")
    }

    // Si quedó una sesión guardada en Room entra directo; si no, muestra el formulario
    fun restaurarSesion(context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)
            val guardado = db.usuarioDao().obtenerSesion()
            if (guardado != null) {
                Sesion.usuario = guardado
                onSuccess()
            } else {
                uiState = uiState.copy(restaurando = false)
            }
        }
    }

    // Valida cada campo y devuelve true si no hay errores
    private fun validar(): Boolean {
        val correo = uiState.correo.trim().lowercase()
        val password = uiState.password

        val errorCorreo = when {
            correo.isEmpty() -> "Ingresa tu correo"
            correo.count { it == '@' } != 1 || correo.startsWith("@") -> "El formato del correo no es válido"
            !correo.endsWith(Constantes.DOMINIO_CORREO) -> "Debe terminar en ${Constantes.DOMINIO_CORREO}"
            !Constantes.correoValido(correo) -> "Usa prefijo.nombre${Constantes.DOMINIO_CORREO} (prof, insp, ti o admin)"
            else -> ""
        }
        val errorPassword = when {
            password.isEmpty() -> "Ingresa tu contraseña"
            password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            else -> ""
        }

        val hayErrores = errorCorreo.isNotEmpty() || errorPassword.isNotEmpty()
        uiState = uiState.copy(
            errorCorreo = errorCorreo,
            errorPassword = errorPassword,
            errorTrigger = if (hayErrores) uiState.errorTrigger + 1 else uiState.errorTrigger
        )
        return !hayErrores
    }

    // Valida, inicia sesión en el servidor y guarda la sesión en Room
    fun onLoginClick(context: Context, onSuccess: () -> Unit) {
        if (!validar()) return

        uiState = uiState.copy(cargando = true, errorGeneral = "")
        viewModelScope.launch {
            try {
                val usuario = PanolRepository.login(uiState.correo.trim().lowercase(), uiState.password)
                // La tabla guarda un solo usuario: se borra el anterior y se guarda el nuevo
                val db = AppDatabase.getDatabase(context)
                db.usuarioDao().borrarTodos()
                db.usuarioDao().insertarUsuario(usuario)
                Sesion.usuario = usuario
                uiState = uiState.copy(cargando = false)
                onSuccess()
            } catch (e: Exception) {
                // 401 = correo o contraseña incorrectos; cualquier otro error viene del servidor o de la red
                val mensaje = if (e is HttpException && e.code() == 401) {
                    "La contraseña o correo son incorrectos"
                } else {
                    PanolRepository.mensajeDeError(e)
                }
                uiState = uiState.copy(
                    cargando = false,
                    errorGeneral = mensaje,
                    errorTrigger = uiState.errorTrigger + 1
                )
            }
        }
    }
}
