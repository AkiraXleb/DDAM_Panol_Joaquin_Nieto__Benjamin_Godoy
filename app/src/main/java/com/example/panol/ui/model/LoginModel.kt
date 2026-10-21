package com.example.panol.ui.model

// Estado de la pantalla de Login: valores de los campos y un error por campo
data class LoginModel(
    val correo: String = "",
    val password: String = "",
    val errorCorreo: String = "",
    val errorPassword: String = "",
    val errorGeneral: String = "",
    val errorTrigger: Int = 0   // sube cada vez que hay errores (dispara el temblor)
)
