package com.example.panol.data.repository

import com.example.panol.data.EstadoSolicitud
import com.example.panol.data.local.entity.DetalleSolicitudEntity
import com.example.panol.data.local.entity.EquipoEntity
import com.example.panol.data.local.entity.SolicitudEntity
import com.example.panol.data.local.entity.UsuarioEntity
import com.example.panol.data.remote.AjusteDto
import com.example.panol.data.remote.CambioEstadoDto
import com.example.panol.data.remote.DetalleDto
import com.example.panol.data.remote.EquipoDto
import com.example.panol.data.remote.LoginDto
import com.example.panol.data.remote.NuevoUsuarioDto
import com.example.panol.data.remote.RetrofitClient
import com.example.panol.data.remote.SolicitudDto
import com.example.panol.data.remote.UsuarioDto
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import com.google.gson.JsonObject
import retrofit2.HttpException
import java.io.IOException

// Una solicitud junto a sus equipos y el nombre de quien la pidió
data class SolicitudConDetalles(
    val solicitud: SolicitudEntity,
    val detalles: List<DetalleSolicitudEntity>,
    val usuarioNombre: String
)

// Repository: único lugar que habla con los microservicios (Retrofit).
// Los ViewModels piden datos aquí y reciben las mismas entidades que usan las pantallas.
object PanolRepository {

    private val equipoApi get() = RetrofitClient.equipoApi
    private val imagenApi get() = RetrofitClient.imagenApi
    private val solicitudApi get() = RetrofitClient.solicitudApi
    private val usuarioApi get() = RetrofitClient.usuarioApi

    // ---------- Equipos (equipos-service) ----------

    suspend fun equipos(): List<EquipoEntity> {
        return equipoApi.listar().map { it.aEntidad() }
    }

    suspend fun equipo(id: Int): EquipoEntity {
        return equipoApi.obtener(id.toLong()).aEntidad()
    }

    // Agrega un equipo al inventario. El servidor lo deja Disponible con todo su stock
    suspend fun crearEquipo(
        codigo: String,
        nombre: String,
        marca: String,
        modelo: String,
        categoria: String,
        descripcion: String,
        ubicacion: String,
        stockTotal: Int
    ): EquipoEntity {
        val dto = EquipoDto(
            codigo = codigo,
            nombre = nombre,
            marca = marca,
            modelo = modelo,
            categoria = categoria,
            descripcion = descripcion,
            ubicacion = ubicacion,
            stockTotal = stockTotal
        )
        return equipoApi.crear(dto).aEntidad()
    }

    // Envía unidades a mantenimiento (cantidad positiva) o las devuelve al stock (negativa)
    suspend fun ajustarMantenimiento(id: Int, cantidad: Int): EquipoEntity {
        return equipoApi.mantenimiento(id.toLong(), AjusteDto(cantidad)).aEntidad()
    }

    // Quita un equipo del inventario (el servidor no deja si hay unidades prestadas)
    suspend fun eliminarEquipo(id: Int) {
        equipoApi.eliminar(id.toLong())
    }

    // ---------- Imágenes de equipos (img-service) ----------

    // Descarga la imagen de un equipo. Devuelve null si el equipo no tiene imagen
    suspend fun imagenEquipo(id: Int): ByteArray? {
        val respuesta = imagenApi.obtener(id.toLong())
        return when {
            respuesta.isSuccessful -> withContext(Dispatchers.IO) { respuesta.body()?.bytes() }
            respuesta.code() == 404 -> null
            else -> throw HttpException(respuesta)
        }
    }

    // Sube (o reemplaza) la imagen de un equipo
    suspend fun subirImagenEquipo(id: Int, archivo: File) {
        val cuerpo = archivo.asRequestBody("image/jpeg".toMediaType())
        val parte = MultipartBody.Part.createFormData("archivo", "equipo_$id.jpg", cuerpo)
        imagenApi.subir(id.toLong(), parte)
    }

    // Borra la imagen de un equipo
    suspend fun eliminarImagenEquipo(id: Int) {
        imagenApi.eliminar(id.toLong())
    }

    // ---------- Solicitudes (solicitudes-service) ----------

    // Crea la solicitud en el servidor y devuelve su id
    suspend fun crearSolicitud(
        solicitud: SolicitudEntity,
        usuarioNombre: String,
        detalles: List<DetalleSolicitudEntity>
    ): Int {
        val dto = SolicitudDto(
            usuarioId = solicitud.usuarioId.toLong(),
            usuarioNombre = usuarioNombre,
            sala = solicitud.sala,
            fechaInicio = solicitud.fechaInicio,
            fechaDevolucion = solicitud.fechaDevolucion,
            asignatura = solicitud.asignatura,
            observaciones = solicitud.observaciones,
            detalles = detalles.map {
                DetalleDto(
                    equipoId = it.equipoId.toLong(),
                    equipoNombre = it.nombreEquipo,
                    categoria = it.categoria,
                    cantidad = it.cantidad
                )
            }
        )
        return (solicitudApi.crear(dto).id ?: 0L).toInt()
    }

    // Solicitudes de un docente
    suspend fun solicitudesDe(usuarioId: Int): List<SolicitudConDetalles> {
        return solicitudApi.listar(usuarioId.toLong(), null).map { it.aSolicitudConDetalles() }
    }

    // Todas las solicitudes (Encargado TI)
    suspend fun todasLasSolicitudes(): List<SolicitudConDetalles> {
        return solicitudApi.listar(null, null).map { it.aSolicitudConDetalles() }
    }

    suspend fun solicitud(id: Int): SolicitudConDetalles {
        return solicitudApi.obtener(id.toLong()).aSolicitudConDetalles()
    }

    // Cambia el estado. El servidor valida el flujo, el motivo y mueve el stock.
    suspend fun cambiarEstado(id: Int, estado: String, motivo: String? = null): SolicitudConDetalles {
        return solicitudApi.cambiarEstado(id.toLong(), CambioEstadoDto(estado, motivo))
            .aSolicitudConDetalles()
    }

    suspend fun cancelar(id: Int) {
        cambiarEstado(id, EstadoSolicitud.CANCELADA)
    }

    // Solicitudes aceptadas o rechazadas que aún no se le avisan al docente
    suspend fun sinNotificar(usuarioId: Int): List<SolicitudEntity> {
        return solicitudesDe(usuarioId)
            .map { it.solicitud }
            .filter {
                !it.notificada &&
                    (it.estado == EstadoSolicitud.ACEPTADA || it.estado == EstadoSolicitud.RECHAZADA)
            }
    }

    suspend fun marcarNotificada(id: Int) {
        solicitudApi.marcarNotificada(id.toLong())
    }

    // ---------- Usuarios (usuarios-service) ----------

    // Inicia sesión en el servidor. Si los datos son incorrectos lanza HttpException 401
    suspend fun login(email: String, password: String): UsuarioEntity {
        return usuarioApi.login(LoginDto(email, password)).aEntidad()
    }

    // Todos los usuarios registrados (Super Admin)
    suspend fun usuarios(): List<UsuarioEntity> {
        return usuarioApi.listar().map { it.aEntidad() }
    }

    // Crea un usuario. El servidor valida el correo y define el cargo por su prefijo
    suspend fun crearUsuario(nombre: String, email: String, password: String, departamento: String): UsuarioEntity {
        return usuarioApi.crear(NuevoUsuarioDto(nombre, email, password, departamento)).aEntidad()
    }

    suspend fun eliminarUsuario(id: Int) {
        usuarioApi.eliminar(id.toLong())
    }

    // ---------- Mensajes de error ----------

    // Convierte un error de red o del servidor en un texto para el usuario
    fun mensajeDeError(e: Throwable): String {
        return when (e) {
            is HttpException -> {
                try {
                    val cuerpo = e.response()?.errorBody()?.string()
                    val json = Gson().fromJson(cuerpo, JsonObject::class.java)
                    json?.get("message")?.asString?.takeIf { it.isNotBlank() }
                        ?: "El servidor respondió con error ${e.code()}"
                } catch (ex: Exception) {
                    "El servidor respondió con error ${e.code()}"
                }
            }
            is IOException -> "No se pudo conectar con el servidor. Revisa tu conexión."
            else -> e.message ?: "Ocurrió un error inesperado"
        }
    }

    // ---------- Conversión de modelos de red a entidades de las pantallas ----------

    private fun EquipoDto.aEntidad() = EquipoEntity(
        id = id.toInt(),
        codigo = codigo ?: "",
        nombre = nombre ?: "",
        marca = marca ?: "",
        modelo = modelo ?: "",
        categoria = categoria ?: "",
        descripcion = descripcion ?: "",
        stockTotal = stockTotal ?: 0,
        stockDisponible = stockDisponible ?: 0,
        stockMantenimiento = stockMantenimiento ?: 0,
        estado = estado ?: "",
        ubicacion = ubicacion ?: ""
    )

    // La contraseña nunca llega a la app, por eso queda vacía
    private fun UsuarioDto.aEntidad() = UsuarioEntity(
        id = id.toInt(),
        nombre = nombre ?: "",
        email = email ?: "",
        password = "",
        cargo = cargo ?: "",
        departamento = departamento ?: ""
    )

    private fun SolicitudDto.aSolicitudConDetalles(): SolicitudConDetalles {
        val idSolicitud = (id ?: 0L).toInt()
        val solicitud = SolicitudEntity(
            id = idSolicitud,
            usuarioId = usuarioId.toInt(),
            sala = sala ?: "",
            fechaInicio = fechaInicio,
            fechaDevolucion = fechaDevolucion,
            asignatura = asignatura ?: "",
            observaciones = observaciones ?: "",
            estado = estado ?: "",
            comentarioGestion = comentarioGestion ?: "",
            fechaCreacion = fechaCreacion ?: 0L,
            fechaEntrega = fechaEntrega,
            fechaRecepcion = fechaRecepcion,
            notificada = notificada ?: false
        )
        val lista = (detalles ?: emptyList()).map {
            DetalleSolicitudEntity(
                id = (it.id ?: 0L).toInt(),
                solicitudId = idSolicitud,
                equipoId = it.equipoId.toInt(),
                nombreEquipo = it.equipoNombre ?: "",
                categoria = it.categoria ?: "",
                cantidad = it.cantidad
            )
        }
        return SolicitudConDetalles(solicitud, lista, usuarioNombre ?: "")
    }
}
