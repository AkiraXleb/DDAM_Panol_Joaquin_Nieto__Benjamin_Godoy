package com.example.panol.data

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.panol.data.repository.PanolRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * Guarda en memoria las imágenes ya descargadas para no pedirlas al servidor cada vez que se abre una
 * pantalla. Si un equipo no tiene imagen también se recuerda (null) hasta que se invalide.
 *
 * - obtener: Lo que hay en memoria ahora (null si no hay imagen o aún no se descarga).
 * - cargar: Devuelve la imagen del equipo: de memoria si ya está, si no la descarga. Null si no tiene
 *   o falla la red.
 * - invalidar: Olvida la imagen de un equipo (después de subir una nueva o de borrarla).
 */
object CacheImagenes {

    private val imagenes = HashMap<Int, ImageBitmap?>()

    fun obtener(equipoId: Int): ImageBitmap? = imagenes[equipoId]

    suspend fun cargar(equipoId: Int): ImageBitmap? {
        if (imagenes.containsKey(equipoId)) return imagenes[equipoId]
        return try {
            val bytes = PanolRepository.imagenEquipo(equipoId)
            val imagen = withContext(Dispatchers.Default) {
                bytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size)?.asImageBitmap() }
            }
            imagenes[equipoId] = imagen
            imagen
        } catch (e: Exception) {
            // Sin conexión: no se guarda, así se reintenta la próxima vez
            null
        }
    }

    fun invalidar(equipoId: Int) {
        imagenes.remove(equipoId)
    }
}
