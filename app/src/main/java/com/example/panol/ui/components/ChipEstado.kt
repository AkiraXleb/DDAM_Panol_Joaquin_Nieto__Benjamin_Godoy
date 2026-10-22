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
import com.example.panol.data.EstadoEquipo
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
    Surface(color = fondo, shape = RoundedCornerShape(50)) {
        Text(
            text = estado,
            color = texto,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// Ícono vectorial según la categoría del equipo
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
