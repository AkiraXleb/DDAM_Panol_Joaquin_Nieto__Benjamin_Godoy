package com.example.panol.ui.screen

import androidx.compose.animation.core.Animatable
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
            for (valor in listOf(-12f, 12f, -8f, 8f, 0f)) {
                desplazamiento.animateTo(valor, tween(50))
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .offset(x = desplazamiento.value.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "MPAZ Pañol",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Escuela Escritora Marcela Paz",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(32.dp))

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
