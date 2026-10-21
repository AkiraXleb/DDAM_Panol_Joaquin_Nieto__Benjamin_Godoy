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

    abstract fun usuarioDao(): UsuarioDao
    abstract fun equipoDao(): EquipoDao
    abstract fun carritoDao(): CarritoDao
    abstract fun solicitudDao(): SolicitudDao

    companion object {          // patrón singleton: una sola instancia de la BD
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "panol_database.db" // nombre del archivo SQLite
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
