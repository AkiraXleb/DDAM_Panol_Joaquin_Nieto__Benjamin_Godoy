package com.example.panol.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.panol.R
import androidx.compose.ui.graphics.Color
import com.example.panol.data.EstadoEquipo
import com.example.panol.data.EstadoSolicitud
import com.example.panol.ui.theme.DisponibleFondo
import com.example.panol.ui.theme.DisponibleTexto
import com.example.panol.ui.theme.MantenimientoFondo
import com.example.panol.ui.theme.MantenimientoTexto
import com.example.panol.ui.theme.PrestadoFondo
import com.example.panol.ui.theme.PrestadoTexto
import com.example.panol.ui.theme.ReservadoFondo
import com.example.panol.ui.theme.ReservadoTexto

// Chip de color según el estado del equipo
@Composable
fun ChipEstado(estado: String) {
    val (fondo, texto) = when (estado) {
        EstadoEquipo.DISPONIBLE -> DisponibleFondo to DisponibleTexto
        EstadoEquipo.RESERVADO -> ReservadoFondo to ReservadoTexto
        EstadoEquipo.PRESTADO -> PrestadoFondo to PrestadoTexto
        else -> MantenimientoFondo to MantenimientoTexto
    }
    Pildora(estado, fondo, texto)
}

// Chip de color según el estado de la solicitud
@Composable
fun ChipEstadoSolicitud(estado: String) {
    val (fondo, texto) = when (estado) {
        EstadoSolicitud.PENDIENTE -> ReservadoFondo to ReservadoTexto
        EstadoSolicitud.ACEPTADA, EstadoSolicitud.DEVUELTA -> DisponibleFondo to DisponibleTexto
        EstadoSolicitud.ENTREGADA -> PrestadoFondo to PrestadoTexto
        else -> MantenimientoFondo to MantenimientoTexto
    }
    Pildora(estado, fondo, texto)
}

// Píldora con fondo y texto de color
@Composable
private fun Pildora(texto: String, fondo: Color, colorTexto: Color) {
    Surface(color = fondo, shape = RoundedCornerShape(50)) {
        Text(
            text = texto,
            color = colorTexto,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// Devuelve el ícono de la categoría del equipo
fun iconoCategoria(categoria: String): Int {
    return when (categoria) {
        "Notebook" -> R.drawable.ic_cat_notebook
        "Computador" -> R.drawable.ic_cat_computador
        "Proyector" -> R.drawable.ic_cat_proyector
        "Parlante" -> R.drawable.ic_cat_parlante
        "Alargador" -> R.drawable.ic_cat_alargador
        "Robótica" -> R.drawable.ic_cat_robotica
        else -> R.drawable.ic_cat_red
    }
}
