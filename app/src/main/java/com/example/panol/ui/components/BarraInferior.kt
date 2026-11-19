package com.example.panol.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.panol.R
import com.example.panol.data.Constantes
import com.example.panol.data.Sesion
import com.example.panol.ui.navigation.Routes

// Barra inferior según el rol: Catálogo, Carrito, Solicitudes y Perfil (docente) o Gestión y Perfil (Encargado TI)
@Composable
fun BarraInferior(
    rutaActual: String,
    unidadesCarrito: Int,
    onNavegar: (String) -> Unit
) {
    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (Sesion.usuario?.cargo == Constantes.CARGO_TI) {
                ItemBarra("Gestión", R.drawable.ic_solicitudes, rutaActual == Routes.GESTION) {
                    onNavegar(Routes.GESTION)
                }
            } else {
                ItemBarra("Catálogo", R.drawable.ic_catalogo, rutaActual == Routes.CATALOGO) {
                    onNavegar(Routes.CATALOGO)
                }
                ItemBarra(
                    if (unidadesCarrito > 0) "Carrito ($unidadesCarrito)" else "Carrito",
                    R.drawable.ic_carrito,
                    rutaActual == Routes.CARRITO
                ) {
                    onNavegar(Routes.CARRITO)
                }
                ItemBarra("Solicitudes", R.drawable.ic_solicitudes, rutaActual == Routes.MIS_SOLICITUDES) {
                    onNavegar(Routes.MIS_SOLICITUDES)
                }
            }
            ItemBarra("Perfil", R.drawable.ic_perfil, rutaActual == Routes.PERFIL) {
                onNavegar(Routes.PERFIL)
            }
        }
    }
}

// Un botón de la barra inferior con ícono y texto
@Composable
private fun ItemBarra(texto: String, icono: Int, seleccionado: Boolean, onClick: () -> Unit) {
    val color = if (seleccionado) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurfaceVariant
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(painter = painterResource(icono), contentDescription = texto, tint = color)
        Text(text = texto, style = MaterialTheme.typography.labelSmall, color = color)
    }
}
