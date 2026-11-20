package com.example.panol.ui.model

/*
 * Estado de la pantalla Perfil: datos del usuario, contador del carrito y versión de la foto.
 *
 * - fotoVersion: Sube cada vez que cambia la foto, para que la pantalla la vuelva a leer.
 */
data class PerfilModel(
    val nombre: String = "",
    val cargo: String = "",
    val departamento: String = "",
    val correo: String = "",
    val unidadesCarrito: Int = 0,
    val fotoVersion: Int = 0
)
