package com.example.panol.ui.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

private const val TAG = "FotoPerfil"

// Tamaño máximo (en píxeles) de la foto guardada
private const val LADO_MAXIMO = 1024

// Archivo donde queda la foto de un usuario (memoria interna de la app, no se sube al servidor)
fun archivoFoto(context: Context, usuarioId: Int): File {
    return File(context.filesDir, "perfil_$usuarioId.jpg")
}

// Archivo temporal donde la cámara deja la foto recién tomada
fun archivoCamara(context: Context): File {
    return File(context.cacheDir, "camara.jpg")
}

// Dirección que se le entrega a la cámara para que guarde la foto (FileProvider del manifest)
fun uriCamara(context: Context): Uri {
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivoCamara(context))
}

// Copia la imagen elegida en la galería a un archivo temporal. Devuelve null si falla
fun copiarDeGaleria(context: Context, uri: Uri): File? {
    return try {
        val destino = File(context.cacheDir, "galeria.jpg")
        val entrada = context.contentResolver.openInputStream(uri) ?: return null
        entrada.use { origen ->
            FileOutputStream(destino).use { salida -> origen.copyTo(salida) }
        }
        destino
    } catch (e: Exception) {
        Log.d(TAG, "No se pudo leer la imagen: ${e.message}")
        null
    }
}

// Reduce la imagen, corrige su rotación y la guarda como JPG de ese usuario. Devuelve true si salió bien
fun guardarFoto(context: Context, usuarioId: Int, origen: File): Boolean {
    val guardada = imagenAJpeg(origen, archivoFoto(context, usuarioId), LADO_MAXIMO, 90)
    if (guardada) Log.d(TAG, "Foto guardada para el usuario $usuarioId")
    return guardada
}

// Lee una imagen, la reduce a "lado" píxeles como máximo, corrige su rotación y la deja como JPG en "destino".
// La usan la foto de perfil (queda en el celular) y las fotos de equipos (se suben a img-service)
fun imagenAJpeg(origen: File, destino: File, lado: Int, calidad: Int): Boolean {
    return try {
        // Primero se leen solo las medidas para no cargar una foto enorme en memoria
        val medidas = BitmapFactory.Options()
        medidas.inJustDecodeBounds = true
        BitmapFactory.decodeFile(origen.path, medidas)
        var reduccion = 1
        while (medidas.outWidth / reduccion > lado || medidas.outHeight / reduccion > lado) {
            reduccion *= 2
        }

        val opciones = BitmapFactory.Options()
        opciones.inSampleSize = reduccion
        val original = BitmapFactory.decodeFile(origen.path, opciones) ?: return false

        // Las fotos de la cámara pueden venir giradas: se lee la orientación y se corrige
        val exif = ExifInterface(origen.path)
        val grados = when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
        val matriz = Matrix()
        matriz.postRotate(grados)
        val corregida = Bitmap.createBitmap(original, 0, 0, original.width, original.height, matriz, true)

        FileOutputStream(destino).use { salida ->
            corregida.compress(Bitmap.CompressFormat.JPEG, calidad, salida)
        }
        true
    } catch (e: Exception) {
        Log.d(TAG, "No se pudo procesar la imagen: ${e.message}")
        false
    }
}
