package com.example.panol.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/*
 * Endpoints del microservicio de solicitudes.
 *
 * - listar: Docente: usuarioId con valor. Encargado TI: usuarioId null (trae todas).
 */
interface SolicitudApi {

    @GET("api/solicitudes")
    suspend fun listar(
        @Query("usuarioId") usuarioId: Long?,
        @Query("estado") estado: String?
    ): List<SolicitudDto>

    @GET("api/solicitudes/{id}")
    suspend fun obtener(@Path("id") id: Long): SolicitudDto

    @POST("api/solicitudes")
    suspend fun crear(@Body solicitud: SolicitudDto): SolicitudDto

    @PATCH("api/solicitudes/{id}/estado")
    suspend fun cambiarEstado(@Path("id") id: Long, @Body cambio: CambioEstadoDto): SolicitudDto

    @PATCH("api/solicitudes/{id}/notificada")
    suspend fun marcarNotificada(@Path("id") id: Long): SolicitudDto
}
