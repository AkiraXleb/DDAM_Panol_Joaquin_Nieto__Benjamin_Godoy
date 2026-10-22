package com.example.panol.ui.model

// Estado del Login: campos, un error por campo y contador que dispara el temblor
data class LoginModel(
    val correo: String = "",
    val password: String = "",
    val errorCorreo: String = "",
    val errorPassword: String = "",
    val errorGeneral: String = "",
    val errorTrigger: Int = 0
)
