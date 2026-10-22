package com.example.panol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.panol.ui.navigation.AppNavigation
import com.example.panol.ui.theme.PanolTheme

// Actividad principal: muestra el tema y la navegación de la app
class MainActivity : ComponentActivity() {
    // Arranca la interfaz con Compose
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PanolTheme {
                AppNavigation()
            }
        }
    }
}
