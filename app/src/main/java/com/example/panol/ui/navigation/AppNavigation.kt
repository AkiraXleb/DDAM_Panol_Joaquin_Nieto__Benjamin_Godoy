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
import com.example.panol.data.Constantes
import com.example.panol.data.Sesion
import com.example.panol.ui.screen.CarritoScreen
import com.example.panol.ui.screen.CatalogoScreen
import com.example.panol.ui.screen.ConfirmacionScreen
import com.example.panol.ui.screen.DetalleScreen
import com.example.panol.ui.screen.FormularioScreen
import com.example.panol.ui.screen.GestionScreen
import com.example.panol.ui.screen.LoginScreen
import com.example.panol.ui.screen.MisSolicitudesScreen
import com.example.panol.ui.screen.PerfilScreen

// Nombres de las rutas de navegación
object Routes {
    const val LOGIN = "login"
    const val CATALOGO = "catalogo"
    const val DETALLE = "detalle"
    const val CARRITO = "carrito"
    const val MIS_SOLICITUDES = "mis_solicitudes"
    const val FORMULARIO = "formulario"
    const val CONFIRMACION = "confirmacion"
    const val PERFIL = "perfil"
    const val GESTION = "gestion"
}

// Pantalla de inicio según el rol: el Encargado TI gestiona, el resto solicita
private fun rutaInicio(): String {
    return if (Sesion.usuario?.cargo == Constantes.CARGO_TI) Routes.GESTION else Routes.CATALOGO
}

// Define las rutas de la app y cómo se navega entre pantallas
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Navegación de la barra inferior, sin duplicar la pantalla de inicio del rol en la pila
    val navegar: (String) -> Unit = { ruta ->
        val inicio = rutaInicio()
        val actual = navController.currentBackStackEntry?.destination?.route
        // Si ya estás en esa pantalla no se navega, así no se vuelve a cargar al tocar repetidamente
        if (ruta != actual) {
            navController.navigate(ruta) {
                popUpTo(inicio) { inclusive = ruta == inicio }
            }
        }
    }

    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(rutaInicio()) {
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
                    navController.navigate(Routes.CONFIRMACION) {
                        popUpTo(Routes.CARRITO) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.CONFIRMACION) { ConfirmacionScreen(onNavegar = navegar) }
        composable(Routes.GESTION) { GestionScreen(onNavegar = navegar) }
        composable(Routes.MIS_SOLICITUDES) { MisSolicitudesScreen(onNavegar = navegar) }
        composable(Routes.PERFIL) {
            PerfilScreen(
                onNavegar = navegar,
                onCerrarSesion = {
                    // Limpia toda la pila, sin importar el rol que estaba conectado
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
    }
}

// Pantalla provisoria para rutas sin implementar
@Composable
private fun Pendiente(nombre: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("$nombre (pendiente)")
    }
}
