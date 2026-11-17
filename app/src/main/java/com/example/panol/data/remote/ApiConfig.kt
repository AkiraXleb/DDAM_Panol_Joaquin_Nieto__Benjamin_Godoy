package com.example.panol.data.remote

// Direcciones de los microservicios (terminan con /).
// Emulador de Android Studio: 10.0.2.2 es el PC donde corre docker compose.
// AWS: reemplazar por http://IP_ELASTICA_DE_LA_EC2:8081/ y :8082/
object ApiConfig {
    const val EQUIPOS_URL = "http://13.217.4.179:8081/"
    const val SOLICITUDES_URL = "http://13.217.4.179:8082/"
}
