package com.example.panol.data

import com.example.panol.data.local.entity.UsuarioEntity

/*
 * Guarda el usuario conectado y los ids que se pasan entre pantallas.
 *
 * - cerrarSesion: Borra el usuario de la sesión.
 */
object Sesion {
    var usuario: UsuarioEntity? = null

    var equipoSeleccionadoId: Int = 0
    var solicitudCreadaId: Int = 0

    fun cerrarSesion() {
        usuario = null
    }
}
