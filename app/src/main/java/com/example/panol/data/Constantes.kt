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

/*
 * Valores fijos del caso: categorías, salas, dominio de correo y reglas del préstamo.
 *
 * - PREFIJOS_CORREO: Prefijos permitidos en el correo: prof. (Docente), insp. (Inspector), ti.
 *   (Encargado TI), admin. (Super Admin).
 * - correoValido: Revisa que el correo sea prefijo.nombre@escuelamarcelapaz.cl, con prefijo válido y
 *   nombre en minúsculas o números.
 */
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
    const val CARGO_SUPER_ADMIN = "Super Admin"

    val PREFIJOS_CORREO = listOf("prof", "insp", "ti", "admin")

    fun correoValido(correo: String): Boolean {
        if (!correo.endsWith(DOMINIO_CORREO)) return false
        val partes = correo.removeSuffix(DOMINIO_CORREO).split(".")
        if (partes.size != 2) return false
        val nombre = partes[1]
        return partes[0] in PREFIJOS_CORREO &&
            nombre.isNotEmpty() &&
            nombre.all { it in 'a'..'z' || it in '0'..'9' }
    }
}
