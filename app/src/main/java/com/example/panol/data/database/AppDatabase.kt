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
    // Versión 2: la tabla usuarios pasó a guardar solo la sesión (se recrea la base al actualizar)
    version = 3,
    exportSchema = false
)
/*
 * - usuarioDao: DAO de usuarios.
 * - equipoDao: DAO de equipos.
 * - carritoDao: DAO del carrito.
 * - solicitudDao: DAO de solicitudes.
 */
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun equipoDao(): EquipoDao
    abstract fun carritoDao(): CarritoDao
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
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
