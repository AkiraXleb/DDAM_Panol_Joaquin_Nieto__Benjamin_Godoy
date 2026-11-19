package com.example.panol.data


// Estados posibles de un equipo del inventario
object EstadoEquipo {
    const val DISPONIBLE = "Disponible"
    const val RESERVADO = "Reservado"
    const val PRESTADO = "Prestado"
    const val MANTENIMIENTO = "En Mantenimiento"
    const val FUERA_SERVICIO = "Fuera de Servicio"
}

// Estados posibles de una solicitud de préstamo
object EstadoSolicitud {
    const val PENDIENTE = "Pendiente"
    const val ACEPTADA = "Aceptada"
    const val RECHAZADA = "Rechazada"
    const val ENTREGADA = "Entregada"
    const val DEVUELTA = "Devuelta"
    const val CANCELADA = "Cancelada"
}

// Valores fijos del caso: categorías, salas, dominio de correo y reglas del préstamo
object Constantes {
    val CATEGORIAS = listOf(
        "Notebook", "Computador", "Proyector", "Parlante",
        "Alargador", "Robótica", "Equipamiento de red"
    )

    val SALAS = listOf(
        "Sala 1° Básico A", "Sala 4° Básico B", "Sala 6° Básico B", "Sala 7° Básico A",
        "Laboratorio de Ciencias 2", "Laboratorio de Computación",
        "Taller de Tecnología", "Biblioteca CRA", "Gimnasio Techado", "Sala de Reuniones"
    )

    const val DOMINIO_CORREO = "@escuelamarcelapaz.cl"
    const val MAX_UNIDADES_POR_EQUIPO = 3
    const val HORAS_ANTICIPACION = 3
    const val CARGO_TI = "Encargado TI"
}
