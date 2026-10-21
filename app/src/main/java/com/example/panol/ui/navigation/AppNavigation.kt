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
import com.example.panol.ui.screen.CatalogoScreen
import com.example.panol.ui.screen.LoginScreen

object Routes {
    const val LOGIN = "login"
    const val CATALOGO = "catalogo"
    const val DETALLE = "detalle"
    const val CARRITO = "carrito"
    const val MIS_SOLICITUDES = "mis_solicitudes"
    const val PERFIL = "perfil"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

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
                onNavegar = { ruta ->
                    navController.navigate(ruta) {
                        popUpTo(Routes.CATALOGO) { inclusive = ruta == Routes.CATALOGO }
                    }
                }
            )
        }
        composable(Routes.DETALLE) { Pendiente("Detalle") }
        composable(Routes.CARRITO) { Pendiente("Carrito") }
        composable(Routes.MIS_SOLICITUDES) { Pendiente("Mis Solicitudes") }
        composable(Routes.PERFIL) { Pendiente("Perfil") }
    }
}

// Pantalla provisoria hasta implementar cada módulo
@Composable
private fun Pendiente(nombre: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("$nombre (pendiente)")
    }
}
