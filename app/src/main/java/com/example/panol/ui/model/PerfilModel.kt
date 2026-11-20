package com.example.panol.ui.model

// Estado de la pantalla Perfil: datos del usuario, contador del carrito y versión de la foto
data class PerfilModel(
    val nombre: String = "",
    val cargo: String = "",
    val departamento: String = "",
    val correo: String = "",
    val unidadesCarrito: Int = 0,
    // Sube cada vez que cambia la foto, para que la pantalla la vuelva a leer
    val fotoVersion: Int = 0
)
