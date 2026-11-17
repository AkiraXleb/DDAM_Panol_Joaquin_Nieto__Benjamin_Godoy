package com.example.panol.data

import com.example.panol.data.database.AppDatabase
import com.example.panol.data.local.entity.UsuarioEntity

// Usuarios de prueba para el login (Room/SQLite). Equipos y solicitudes vienen de los microservicios.
object DatosIniciales {

    // Inserta los usuarios de ejemplo la primera vez
    suspend fun cargar(db: AppDatabase) {
        if (db.usuarioDao().contarUsuarios() > 0) return

        // Usuarios
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
    }
}
