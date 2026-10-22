package com.example.panol.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.panol.ui.screen.CarritoScreen
import com.example.panol.ui.screen.CatalogoScreen
import com.example.panol.ui.screen.ConfirmacionScreen
import com.example.panol.ui.screen.DetalleScreen
import com.example.panol.ui.screen.FormularioScreen
import com.example.panol.ui.screen.LoginScreen
import com.example.panol.ui.screen.MisSolicitudesScreen
import com.example.panol.ui.screen.PerfilScreen

object Routes {
    const val LOGIN = "login"
    const val CATALOGO = "catalogo"
    const val DETALLE = "detalle"
    const val CARRITO = "carrito"
    const val MIS_SOLICITUDES = "mis_solicitudes"
    const val FORMULARIO = "formulario"
    const val CONFIRMACION = "confirmacion"
    const val PERFIL = "perfil"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Navegación de la barra inferior: volver al Catálogo no lo duplica en la pila
    val navegar: (String) -> Unit = { ruta ->
        navController.navigate(ruta) {
            popUpTo(Routes.CATALOGO) { inclusive = ruta == Routes.CATALOGO }
        }
    }

    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(Routes.CATALOGO) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            })
        }
        composable(Routes.CATALOGO) {
            CatalogoScreen(
                onVerDetalle = { navController.navigate(Routes.DETALLE) },
                onNavegar = navegar
            )
        }
        composable(Routes.DETALLE) {
            DetalleScreen(
                onVolver = { navController.popBackStack() },
                onAgregado = { navController.popBackStack() }
            )
        }
        composable(Routes.CARRITO) {
            CarritoScreen(
                onContinuar = { navController.navigate(Routes.FORMULARIO) },
                onNavegar = navegar
            )
        }
        composable(Routes.FORMULARIO) {
            FormularioScreen(
                onVolver = { navController.popBackStack() },
                onEnviado = {
                    // saca Carrito y Formulario de la pila para no volver a ellos
                    navController.navigate(Routes.CONFIRMACION) {
                        popUpTo(Routes.CARRITO) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.CONFIRMACION) { ConfirmacionScreen(onNavegar = navegar) }
        composable(Routes.MIS_SOLICITUDES) { MisSolicitudesScreen(onNavegar = navegar) }
        composable(Routes.PERFIL) {
            PerfilScreen(
                onNavegar = navegar,
                onCerrarSesion = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.CATALOGO) { inclusive = true }
                    }
                }
            )
        }
    }
}

// Pantalla provisoria hasta implementar cada módulo
@Composable
private fun Pendiente(nombre: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("$nombre (pendiente)")
    }
}
