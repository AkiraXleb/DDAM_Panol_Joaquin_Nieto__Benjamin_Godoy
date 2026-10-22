package com.example.panol.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.panol.data.local.dao.CarritoDao
import com.example.panol.data.local.dao.EquipoDao
import com.example.panol.data.local.dao.SolicitudDao
import com.example.panol.data.local.dao.UsuarioDao
import com.example.panol.data.local.entity.CarritoItemEntity
import com.example.panol.data.local.entity.DetalleSolicitudEntity
import com.example.panol.data.local.entity.EquipoEntity
import com.example.panol.data.local.entity.SolicitudEntity
import com.example.panol.data.local.entity.UsuarioEntity

// Base de datos Room con las tablas de usuarios, equipos, carrito y solicitudes
@Database(
    entities = [
        UsuarioEntity::class,
        EquipoEntity::class,
        CarritoItemEntity::class,
        SolicitudEntity::class,
        DetalleSolicitudEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // DAO de usuarios
    abstract fun usuarioDao(): UsuarioDao
    // DAO de equipos
    abstract fun equipoDao(): EquipoDao
    // DAO del carrito
    abstract fun carritoDao(): CarritoDao
    // DAO de solicitudes
    abstract fun solicitudDao(): SolicitudDao

    // Singleton: una sola instancia de la base de datos
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Devuelve la base de datos y la crea la primera vez
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "panol_database.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
