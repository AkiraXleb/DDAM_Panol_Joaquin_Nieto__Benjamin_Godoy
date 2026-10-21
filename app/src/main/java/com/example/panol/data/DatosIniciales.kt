package com.example.panol.data

import com.example.panol.data.database.AppDatabase
import com.example.panol.data.local.entity.DetalleSolicitudEntity
import com.example.panol.data.local.entity.EquipoEntity
import com.example.panol.data.local.entity.SolicitudEntity
import com.example.panol.data.local.entity.UsuarioEntity

// Datos ficticios para probar la app (el caso prohíbe usar datos reales - Ley 19.628 / 21.719)
// Se cargan solo la primera vez, cuando la base de datos está vacía
object DatosIniciales {

    private const val HORA = 60 * 60 * 1000L
    private const val DIA = 24 * HORA

    suspend fun cargar(db: AppDatabase) {
        if (db.usuarioDao().contarUsuarios() > 0) return   // ya hay datos, no se cargan de nuevo

        // ---------- USUARIOS ----------
        db.usuarioDao().insertarUsuario(
            UsuarioEntity(
                nombre = "Patricia González",
                email = "profesora.patricia@escuelamarcelapaz.cl",
                password = "docente123",
                cargo = "Docente",
                departamento = "Departamento de Ciencias"
            )
        )
        db.usuarioDao().insertarUsuario(
            UsuarioEntity(
                nombre = "Marcelo Rojas",
                email = "inspector.marcelo@escuelamarcelapaz.cl",
                password = "inspector123",
                cargo = "Inspector",
                departamento = "Inspectoría General"
            )
        )
        db.usuarioDao().insertarUsuario(
            UsuarioEntity(
                nombre = "Roberto Díaz",
                email = "ti.roberto@escuelamarcelapaz.cl",
                password = "admin123",
                cargo = "Encargado TI",
                departamento = "Pañol Central"
            )
        )

        // ---------- EQUIPOS ----------
        db.equipoDao().insertarEquipos(
            listOf(
                EquipoEntity(
                    codigo = "NOT-2024-08", nombre = "Notebook Lenovo ThinkPad E14",
                    marca = "Lenovo", modelo = "E14 Gen 4", categoria = "Notebook",
                    descripcion = "Intel Core i5, 16 GB RAM. Ideal para clases y presentaciones. Batería de 6 horas.",
                    stockTotal = 8, stockDisponible = 8, estado = EstadoEquipo.DISPONIBLE,
                    ubicacion = "Pañol Central - Estante A1"
                ),
                EquipoEntity(
                    codigo = "PRO-2024-03", nombre = "Proyector Láser Epson PowerLite",
                    marca = "Epson", modelo = "PowerLite L210", categoria = "Proyector",
                    descripcion = "Full HD 1080p, 4000 lúmenes, entrada HDMI. Incluye control remoto.",
                    stockTotal = 3, stockDisponible = 2, estado = EstadoEquipo.DISPONIBLE,
                    ubicacion = "Pañol Central - Estante B2"
                ),
                EquipoEntity(
                    codigo = "PAR-2024-01", nombre = "Parlante Portátil Bluetooth",
                    marca = "JBL", modelo = "PartyBox 110", categoria = "Parlante",
                    descripcion = "Potencia 40W con micrófono inalámbrico.",
                    stockTotal = 1, stockDisponible = 0, estado = EstadoEquipo.PRESTADO,
                    ubicacion = "Pañol Central - Estante C1"
                ),
                EquipoEntity(
                    codigo = "ROB-2024-05", nombre = "Kit Robótica Educativa Arduino",
                    marca = "Arduino", modelo = "Starter Kit", categoria = "Robótica",
                    descripcion = "Set con placa Arduino UNO, sensores, servomotores y protoboard.",
                    stockTotal = 3, stockDisponible = 1, estado = EstadoEquipo.RESERVADO,
                    ubicacion = "Pañol Central - Estante D1"
                ),
                EquipoEntity(
                    codigo = "ROB-2024-09", nombre = "Kit LEGO WeDo 2.0",
                    marca = "LEGO", modelo = "WeDo 2.0", categoria = "Robótica",
                    descripcion = "Kit de robótica para primer ciclo con motor, sensores y bloques.",
                    stockTotal = 6, stockDisponible = 4, estado = EstadoEquipo.DISPONIBLE,
                    ubicacion = "Pañol Central - Estante D2"
                ),
                EquipoEntity(
                    codigo = "ROB-2024-12", nombre = "Placa de desarrollo ESP32",
                    marca = "Espressif", modelo = "ESP32 DevKit V1", categoria = "Robótica",
                    descripcion = "Microcontrolador con WiFi y Bluetooth para proyectos de tecnología.",
                    stockTotal = 15, stockDisponible = 10, estado = EstadoEquipo.DISPONIBLE,
                    ubicacion = "Pañol Central - Cajón D3"
                ),
                EquipoEntity(
                    codigo = "COM-2024-02", nombre = "Carro de 30 Tablets Galaxy Tab",
                    marca = "Samsung", modelo = "Galaxy Tab A9", categoria = "Computador",
                    descripcion = "Estación móvil de carga con candado de seguridad.",
                    stockTotal = 1, stockDisponible = 0, estado = EstadoEquipo.MANTENIMIENTO,
                    ubicacion = "Sala de Mantención TI"
                ),
                EquipoEntity(
                    codigo = "COM-2024-06", nombre = "Computador de escritorio HP",
                    marca = "HP", modelo = "ProDesk 400 G7", categoria = "Computador",
                    descripcion = "Intel Core i3, 8 GB RAM, monitor 22 pulgadas.",
                    stockTotal = 5, stockDisponible = 3, estado = EstadoEquipo.DISPONIBLE,
                    ubicacion = "Pañol Central - Zona E"
                ),
                EquipoEntity(
                    codigo = "ALA-2024-04", nombre = "Alargador Robusto 10 Metros",
                    marca = "Genérico", modelo = "AL-10M", categoria = "Alargador",
                    descripcion = "Interruptor de encendido y protección contra sobrecarga.",
                    stockTotal = 12, stockDisponible = 12, estado = EstadoEquipo.DISPONIBLE,
                    ubicacion = "Pañol Central - Estante F1"
                ),
                EquipoEntity(
                    codigo = "RED-2024-01", nombre = "Switch Cisco 24 puertos",
                    marca = "Cisco", modelo = "CBS250-24T", categoria = "Equipamiento de red",
                    descripcion = "Switch administrable de 24 puertos Gigabit.",
                    stockTotal = 1, stockDisponible = 0, estado = EstadoEquipo.FUERA_SERVICIO,
                    ubicacion = "Rack Sala de Servidores (ubicación técnica ficticia)"
                ),
                EquipoEntity(
                    codigo = "RED-2024-02", nombre = "Router TP-Link Archer",
                    marca = "TP-Link", modelo = "Archer C6", categoria = "Equipamiento de red",
                    descripcion = "Router WiFi doble banda para actividades de laboratorio.",
                    stockTotal = 2, stockDisponible = 2, estado = EstadoEquipo.DISPONIBLE,
                    ubicacion = "Pañol Central - Estante G1"
                )
            )
        )

        // ---------- SOLICITUDES DE EJEMPLO (de la profesora Patricia) ----------
        // Como la consola del encargado TI se hace en la siguiente etapa, se precargan
        // solicitudes en distintos estados para poder mostrar "Mis Solicitudes"
        val patricia = db.usuarioDao().obtenerPorEmail("profesora.patricia@escuelamarcelapaz.cl") ?: return
        val ahora = System.currentTimeMillis()

        crearSolicitud(
            db,
            SolicitudEntity(
                usuarioId = patricia.id, sala = "Sala 6° Básico B",
                fechaInicio = ahora + DIA, fechaDevolucion = ahora + DIA + 2 * HORA,
                asignatura = "Matemática - 6° Básico B",
                estado = EstadoSolicitud.RECHAZADA,
                comentarioGestion = "Estimada colega: el carro de tablets está en mantención preventiva " +
                        "hasta el viernes. Le sugerimos reservar notebooks individuales.",
                fechaCreacion = ahora - 2 * DIA
            ),
            listOf(DetalleSolicitudEntity(solicitudId = 0, equipoId = 7, nombreEquipo = "Carro de 30 Tablets Galaxy Tab", categoria = "Computador", cantidad = 1))
        )
        crearSolicitud(
            db,
            SolicitudEntity(
                usuarioId = patricia.id, sala = "Taller de Tecnología",
                fechaInicio = ahora + DIA + 4 * HORA, fechaDevolucion = ahora + DIA + 6 * HORA,
                asignatura = "Tecnología - 7° Básico A",
                estado = EstadoSolicitud.ACEPTADA,
                comentarioGestion = "Listo para retiro en el mesón del Pañol.",
                fechaCreacion = ahora - DIA
            ),
            listOf(DetalleSolicitudEntity(solicitudId = 0, equipoId = 4, nombreEquipo = "Kit Robótica Educativa Arduino", categoria = "Robótica", cantidad = 2))
        )
        crearSolicitud(
            db,
            SolicitudEntity(
                usuarioId = patricia.id, sala = "Gimnasio Techado",
                fechaInicio = ahora - HORA, fechaDevolucion = ahora + 2 * HORA,
                asignatura = "Educación Física - 4° Básico B",
                estado = EstadoSolicitud.ENTREGADA,
                fechaCreacion = ahora - 3 * DIA,
                fechaEntrega = ahora - HORA,
                notificada = true
            ),
            listOf(DetalleSolicitudEntity(solicitudId = 0, equipoId = 3, nombreEquipo = "Parlante Portátil Bluetooth", categoria = "Parlante", cantidad = 1))
        )
        crearSolicitud(
            db,
            SolicitudEntity(
                usuarioId = patricia.id, sala = "Biblioteca CRA",
                fechaInicio = ahora - 3 * DIA, fechaDevolucion = ahora - 3 * DIA + 2 * HORA,
                asignatura = "Lenguaje - 1° Básico A",
                estado = EstadoSolicitud.DEVUELTA,
                comentarioGestion = "Revisión técnica OK. Recibido conforme.",
                fechaCreacion = ahora - 5 * DIA,
                fechaEntrega = ahora - 3 * DIA,
                fechaRecepcion = ahora - 3 * DIA + 2 * HORA,
                notificada = true
            ),
            listOf(
                DetalleSolicitudEntity(solicitudId = 0, equipoId = 1, nombreEquipo = "Notebook Lenovo ThinkPad E14", categoria = "Notebook", cantidad = 1),
                DetalleSolicitudEntity(solicitudId = 0, equipoId = 9, nombreEquipo = "Alargador Robusto 10 Metros", categoria = "Alargador", cantidad = 1)
            )
        )
    }

    // inserta la solicitud y luego sus equipos con el id generado
    // (se usa copy() del data class para asignar el solicitudId a cada detalle)
    private suspend fun crearSolicitud(
        db: AppDatabase,
        solicitud: SolicitudEntity,
        detalles: List<DetalleSolicitudEntity>
    ) {
        val idSolicitud = db.solicitudDao().insertarSolicitud(solicitud).toInt()
        val detallesConId = detalles.map { it.copy(solicitudId = idSolicitud) }
        db.solicitudDao().insertarDetalles(detallesConId)
    }
}
