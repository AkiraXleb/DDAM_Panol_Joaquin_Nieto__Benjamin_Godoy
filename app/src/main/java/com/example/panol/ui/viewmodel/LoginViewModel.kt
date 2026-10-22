package com.example.panol.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panol.data.Constantes
import com.example.panol.data.DatosIniciales
import com.example.panol.data.Sesion
import com.example.panol.data.database.AppDatabase
import com.example.panol.ui.model.LoginModel
import kotlinx.coroutines.launch

// ViewModel del Login: validación de campos y credenciales
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

    // Carga los datos ficticios si la base de datos está vacía
    fun cargarDatosIniciales(context: Context) {
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)
            DatosIniciales.cargar(db)
        }
    }

    // Valida cada campo y devuelve true si no hay errores
    private fun validar(): Boolean {
        val correo = uiState.correo.trim()
        val password = uiState.password

        val errorCorreo = when {
            correo.isEmpty() -> "Ingresa tu correo"
            !correo.contains("@") || correo.startsWith("@") -> "El formato del correo no es válido"
            !correo.endsWith(Constantes.DOMINIO_CORREO) -> "Debe terminar en ${Constantes.DOMINIO_CORREO}"
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

    // Valida, busca el usuario en Room y guarda la sesión
    fun onLoginClick(context: Context, onSuccess: () -> Unit) {
        if (!validar()) return

        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)
            val usuario = db.usuarioDao().obtenerPorEmail(uiState.correo.trim())
            if (usuario != null && usuario.password == uiState.password) {
                Sesion.usuario = usuario
                onSuccess()
            } else {
                uiState = uiState.copy(
                    errorGeneral = "Correo o contraseña incorrectos",
                    errorTrigger = uiState.errorTrigger + 1
                )
            }
        }
    }
}
