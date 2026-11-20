package com.example.panol.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

// Endpoints del microservicio de equipos (listar, ver, crear y eliminar)
interface EquipoApi {

    @GET("api/equipos")
    suspend fun listar(): List<EquipoDto>

    @GET("api/equipos/{id}")
    suspend fun obtener(@Path("id") id: Long): EquipoDto

    @POST("api/equipos")
    suspend fun crear(@Body equipo: EquipoDto): EquipoDto

    @PATCH("api/equipos/{id}/mantenimiento")
    suspend fun mantenimiento(@Path("id") id: Long, @Body ajuste: AjusteDto): EquipoDto

    @DELETE("api/equipos/{id}")
    suspend fun eliminar(@Path("id") id: Long)
}
