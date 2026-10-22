package com.example.panol.ui.model

// Estado de la pantalla Perfil
data class PerfilModel(
    val nombre: String = "",
    val cargo: String = "",
    val departamento: String = "",
    val correo: String = "",
    val unidadesCarrito: Int = 0
)
