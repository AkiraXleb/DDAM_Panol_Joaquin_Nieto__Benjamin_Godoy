package com.example.panol.data

import com.example.panol.data.local.entity.UsuarioEntity

// Guarda el usuario que inició sesión para usarlo en todas las pantallas
object Sesion {
    var usuario: UsuarioEntity? = null

    // ids que se pasan entre pantallas (en vez de argumentos de navegación)
    var equipoSeleccionadoId: Int = 0
    var solicitudCreadaId: Int = 0

    fun cerrarSesion() {
        usuario = null
    }
}
