package com.example.panol.ui.model

// Estado del Login: campos, un error por campo, contador que dispara el temblor y banderas de carga
data class LoginModel(
    val correo: String = "",
    val password: String = "",
    val errorCorreo: String = "",
    val errorPassword: String = "",
    val errorGeneral: String = "",
    val errorTrigger: Int = 0,
    // true mientras se llama al servidor
    val cargando: Boolean = false,
    // true mientras se revisa si quedó una sesión guardada (evita mostrar el formulario un instante)
    val restaurando: Boolean = true
)
