package com.example.panol.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TealInverso,
    onPrimary = TealSobreContenedor,
    primaryContainer = Teal,
    onPrimaryContainer = Color.White,
    secondary = Naranjo,
    onSecondary = Color.White,
    secondaryContainer = NaranjoSobreContenedor,
    onSecondaryContainer = NaranjoContenedor,
    tertiary = TerciarioContenedor,
    onTertiary = TerciarioSobreContenedor,
    background = FondoOscuro,
    onBackground = TextoSobreOscuro,
    surface = FondoOscuro,
    onSurface = TextoSobreOscuro,
    surfaceVariant = SuperficieOscura,
    onSurfaceVariant = BordeSuave,
    error = ErrorContenedor,
    onError = ErrorSobreContenedor,
    surfaceContainerLowest = FondoOscuro,
    surfaceContainerLow = Color(0xFF212425),
    surfaceContainer = Color(0xFF262A2B),
    surfaceContainerHigh = SuperficieOscura,
    surfaceContainerHighest = Color(0xFF343839)
)

private val LightColorScheme = lightColorScheme(
    primary = Teal,
    onPrimary = Color.White,
    primaryContainer = TealClaro,
    onPrimaryContainer = TealSobreContenedor,
    secondary = Naranjo,
    onSecondary = Color.White,
    secondaryContainer = NaranjoContenedor,
    onSecondaryContainer = NaranjoSobreContenedor,
    tertiary = Terciario,
    onTertiary = Color.White,
    tertiaryContainer = TerciarioContenedor,
    onTertiaryContainer = TerciarioSobreContenedor,
    background = Fondo,
    onBackground = TextoPrincipal,
    surface = Fondo,
    onSurface = TextoPrincipal,
    surfaceVariant = SuperficieVariante,
    onSurfaceVariant = TextoSecundario,
    outline = Borde,
    outlineVariant = BordeSuave,
    error = Error,
    onError = Color.White,
    errorContainer = ErrorContenedor,
    onErrorContainer = ErrorSobreContenedor,
    // Tarjetas y barras blancas sobre el fondo gris claro
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color.White,
    surfaceContainer = Color.White,
    surfaceContainerHigh = Color(0xFFF8FAFB),
    surfaceContainerHighest = Color.White
)

// Tema de la app con la paleta del mockup, sin dynamic color
@Composable
fun PanolTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Colores naranjos de los botones de acción
@Composable
fun coloresBotonAccion(): ButtonColors {
    return ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary
    )
}
