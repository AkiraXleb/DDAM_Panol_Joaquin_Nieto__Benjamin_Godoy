package com.example.panol.data.remote

// Direcciones de los microservicios (terminan con /).
// Emulador de Android Studio: 10.0.2.2 es el PC donde corre docker compose.
// AWS: reemplazar por http://IP_ELASTICA_DE_LA_EC2:8081/, :8082/, :8083/ y :8084/
object ApiConfig {
    const val EQUIPOS_URL = "http://32.197.25.36:8081/"
    const val SOLICITUDES_URL = "http://32.197.25.36:8082/"
    const val USUARIOS_URL = "http://32.197.25.36:8083/"
    const val IMAGENES_URL = "http://32.197.25.36:8084/"
}
