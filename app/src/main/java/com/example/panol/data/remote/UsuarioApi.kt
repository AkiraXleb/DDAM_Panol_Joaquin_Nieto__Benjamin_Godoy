package com.example.panol.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// Endpoints del microservicio de usuarios
interface UsuarioApi {

    @POST("api/usuarios/login")
    suspend fun login(@Body datos: LoginDto): UsuarioDto

    @GET("api/usuarios")
    suspend fun listar(): List<UsuarioDto>

    @POST("api/usuarios")
    suspend fun crear(@Body usuario: NuevoUsuarioDto): UsuarioDto

    @DELETE("api/usuarios/{id}")
    suspend fun eliminar(@Path("id") id: Long)
}
