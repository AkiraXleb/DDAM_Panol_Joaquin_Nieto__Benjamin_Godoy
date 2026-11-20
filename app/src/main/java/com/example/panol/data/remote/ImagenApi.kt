package com.example.panol.data.remote

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

// Endpoints de img-service: una imagen por equipo (el id es el del equipo)
interface ImagenApi {

    @Multipart
    @PUT("api/imagenes/equipos/{id}")
    suspend fun subir(@Path("id") id: Long, @Part archivo: MultipartBody.Part)

    // Response para poder distinguir el 404 (equipo sin imagen) de un error de red
    @GET("api/imagenes/equipos/{id}")
    suspend fun obtener(@Path("id") id: Long): Response<ResponseBody>

    @DELETE("api/imagenes/equipos/{id}")
    suspend fun eliminar(@Path("id") id: Long)
}
