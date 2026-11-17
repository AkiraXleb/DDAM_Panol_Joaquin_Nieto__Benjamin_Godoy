package com.example.panol.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Crea los clientes Retrofit, uno por microservicio, con log de las peticiones
object RetrofitClient {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private fun crearRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val equipoApi: EquipoApi by lazy {
        crearRetrofit(ApiConfig.EQUIPOS_URL).create(EquipoApi::class.java)
    }

    val solicitudApi: SolicitudApi by lazy {
        crearRetrofit(ApiConfig.SOLICITUDES_URL).create(SolicitudApi::class.java)
    }
}
