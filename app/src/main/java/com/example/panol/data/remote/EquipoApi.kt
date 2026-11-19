package com.example.panol.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

// Endpoints del microservicio de equipos
interface EquipoApi {

    @GET("api/equipos")
    suspend fun listar(): List<EquipoDto>

    @GET("api/equipos/{id}")
    suspend fun obtener(@Path("id") id: Long): EquipoDto
}
