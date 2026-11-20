package com.example.panol.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.panol.data.CacheImagenes

// Imagen de un equipo: la del servidor si existe y, si no, el ícono de su categoría.
// "version" cambia cuando se sube o se borra una imagen, para que se vuelva a pedir
@Composable
fun ImagenEquipo(
    equipoId: Int,
    categoria: String,
    modifier: Modifier = Modifier,
    version: Int = 0
) {
    val imagen by produceState<ImageBitmap?>(CacheImagenes.obtener(equipoId), equipoId, version) {
        value = CacheImagenes.cargar(equipoId)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        val foto = imagen
        if (foto != null) {
            Image(
                bitmap = foto,
                contentDescription = categoria,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                painter = painterResource(iconoCategoria(categoria)),
                contentDescription = categoria,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxSize(0.55f)
            )
        }
    }
}
