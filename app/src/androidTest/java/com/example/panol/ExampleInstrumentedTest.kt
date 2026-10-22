package com.example.panol

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

// Prueba instrumentada de ejemplo que corre en el emulador o teléfono
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    // Revisa que el paquete de la app sea el correcto
    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.panol", appContext.packageName)
    }
}