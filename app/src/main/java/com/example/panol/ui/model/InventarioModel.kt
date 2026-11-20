package com.example.panol.ui.model

import com.example.panol.data.local.entity.EquipoEntity

// Estado de la pantalla Inventario (Encargado TI): lista de equipos, formulario de nuevo equipo y un error por campo
data class InventarioModel(
    val equipos: List<EquipoEntity> = emptyList(),
    val cargando: Boolean = false,
    val mostrarFormulario: Boolean = false,
    val codigo: String = "",
    val nombre: String = "",
    val marca: String = "",
    val modelo: String = "",
    val categoria: String = "",
    val descripcion: String = "",
    val ubicacion: String = "",
    val stock: String = "",
    val errorCodigo: String = "",
    val errorNombre: String = "",
    val errorCategoria: String = "",
    val errorUbicacion: String = "",
    val errorStock: String = "",
    val errorGeneral: String = "",
    // Imagen del equipo nuevo (ruta del archivo ya reducido, "" si no eligió) y a quién va la foto que se está eligiendo:
    // -1 = equipo nuevo, un id = ese equipo, 0 = nadie
    val fotoNueva: String = "",
    val destinoFoto: Int = 0,
    // Sube cada vez que cambia una imagen, para que las tarjetas la vuelvan a pedir
    val fotoVersion: Int = 0,
    // Diálogo de mantenimiento: equipo elegido (null = cerrado), cantidad escrita y su error
    val equipoMantenimiento: EquipoEntity? = null,
    val cantidadMantenimiento: String = "",
    val errorMantenimiento: String = ""
)
