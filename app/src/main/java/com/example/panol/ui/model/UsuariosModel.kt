package com.example.panol.ui.model

import com.example.panol.data.local.entity.UsuarioEntity

// Estado de la pantalla Usuarios (Super Admin): lista, formulario de nuevo usuario y un error por campo
data class UsuariosModel(
    val usuarios: List<UsuarioEntity> = emptyList(),
    val cargando: Boolean = false,
    val mostrarFormulario: Boolean = false,
    val nombre: String = "",
    val correo: String = "",
    val password: String = "",
    val departamento: String = "",
    val errorNombre: String = "",
    val errorCorreo: String = "",
    val errorPassword: String = "",
    val errorDepartamento: String = "",
    val errorGeneral: String = ""
)
