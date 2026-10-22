package com.example.panol.ui.screen

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import kotlinx.coroutines.delay

private const val TAG = "RecursosNativos"

// Hace vibrar el teléfono por los milisegundos indicados
fun vibrar(context: Context, milisegundos: Long) {
    val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    Log.d(TAG, "Vibrando $milisegundos ms")
    vibrator.vibrate(
        VibrationEffect.createOneShot(milisegundos, VibrationEffect.DEFAULT_AMPLITUDE)
    )
}

// Enciende o apaga la linterna; devuelve false si no hay flash o falla (ej. emulador)
fun cambiarLinterna(context: Context, encender: Boolean): Boolean {
    return try {
        val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val idCamara = manager.cameraIdList.firstOrNull { id ->
            manager.getCameraCharacteristics(id)
                .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
        if (idCamara == null) {
            Log.d(TAG, "No se encontró una cámara con flash")
            false
        } else {
            manager.setTorchMode(idCamara, encender)
            Log.d(TAG, "Linterna ${if (encender) "encendida" else "apagada"}")
            true
        }
    } catch (e: Exception) {
        Log.d(TAG, "No se pudo cambiar la linterna: ${e.message}")
        false
    }
}

// Señal de una solicitud revisada: 2 veces si fue aceptada, 1 vez si fue rechazada
suspend fun senalSolicitud(context: Context, aprobada: Boolean) {
    val repeticiones = if (aprobada) 2 else 1
    Log.d(TAG, "Señal de solicitud ${if (aprobada) "aceptada" else "rechazada"} x$repeticiones")
    repeat(repeticiones) {
        vibrar(context, 300)
        cambiarLinterna(context, true)
        delay(300)
        cambiarLinterna(context, false)
        delay(300)
    }
}
