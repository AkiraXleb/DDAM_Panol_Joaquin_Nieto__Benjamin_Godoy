package com.example.panol.data.remote

// Modelos que viajan por la red (JSON). Los nombres coinciden con los del backend.

data class EquipoDto(
    val id: Long = 0,
    val codigo: String? = null,
    val nombre: String? = null,
    val marca: String? = null,
    val modelo: String? = null,
    val categoria: String? = null,
    val descripcion: String? = null,
    val stockTotal: Int? = null,
    val stockDisponible: Int? = null,
    val estado: String? = null,
    val ubicacion: String? = null
)

data class DetalleDto(
    val id: Long? = null,
    val equipoId: Long = 0,
    val equipoNombre: String? = null,
    val categoria: String? = null,
    val cantidad: Int = 0
)

data class SolicitudDto(
    val id: Long? = null,
    val usuarioId: Long = 0,
    val usuarioNombre: String? = null,
    val sala: String? = null,
    val fechaInicio: Long = 0,
    val fechaDevolucion: Long = 0,
    val asignatura: String? = null,
    val observaciones: String? = null,
    val estado: String? = null,
    val comentarioGestion: String? = null,
    val fechaCreacion: Long? = null,
    val fechaEntrega: Long? = null,
    val fechaRecepcion: Long? = null,
    val notificada: Boolean? = null,
    val detalles: List<DetalleDto>? = null
)

// Cuerpo de PATCH /api/solicitudes/{id}/estado
data class CambioEstadoDto(
    val estado: String,
    val motivo: String? = null
)
