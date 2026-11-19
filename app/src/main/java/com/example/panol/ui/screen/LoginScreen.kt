package com.example.panol.ui.screen

import androidx.compose.animation.core.Animatable
import com.example.panol.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Card
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.panol.ui.components.MensajeError
import com.example.panol.ui.theme.coloresBotonAccion
import com.example.panol.ui.viewmodel.LoginViewModel

// Pantalla de inicio de sesión con validación por campo
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val context = LocalContext.current
    val state = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.cargarDatosIniciales(context)
    }

    // Temblor del formulario cuando hay errores
    val desplazamiento = remember { Animatable(0f) }
    LaunchedEffect(state.errorTrigger) {
        if (state.errorTrigger > 0) {
            for (valor in listOf(-5f, 5f, -3f, 3f, 0f)) {
                desplazamiento.animateTo(valor, tween(80))
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Fondo con la fachada de la escuela y una capa oscura para que se lea el texto
            Image(
                painter = painterResource(R.drawable.fondo_escuela),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
                    .offset(x = desplazamiento.value.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo de la escuela al frente
                Image(
                    painter = painterResource(R.drawable.logo_escuela),
                    contentDescription = "Logo de la escuela",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "MPAZ Pañol",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White
                )
                Text(
                    text = "Escuela Escritora Marcela Paz",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Formulario sobre una tarjeta clara
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = state.correo,
                            onValueChange = { viewModel.onCorreoChanged(it) },
                            label = { Text("Correo institucional") },
                            singleLine = true,
                            isError = state.errorCorreo.isNotEmpty(),
                            supportingText = { MensajeError(state.errorCorreo) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = state.password,
                            onValueChange = { viewModel.onPasswordChanged(it) },
                            label = { Text("Contraseña") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            isError = state.errorPassword.isNotEmpty(),
                            supportingText = { MensajeError(state.errorPassword) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth()
                        )

                        MensajeError(state.errorGeneral)
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.onLoginClick(context, onLoginSuccess) },
                            colors = coloresBotonAccion(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Iniciar sesión")
                        }
                    }
                }
            }
        }
    }
}
