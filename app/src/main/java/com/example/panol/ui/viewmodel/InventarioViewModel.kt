package com.example.panol.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panol.data.CacheImagenes
import com.example.panol.data.local.entity.EquipoEntity
import com.example.panol.data.repository.PanolRepository
import com.example.panol.ui.model.InventarioModel
import com.example.panol.ui.screen.archivoCamara
import com.example.panol.ui.screen.copiarDeGaleria
import com.example.panol.ui.screen.imagenAJpeg
import com.example.panol.ui.screen.vibrar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

// Las imágenes de equipos se suben chicas: 800 px y JPG de calidad 80
private const val LADO_IMAGEN = 800
private const val CALIDAD_IMAGEN = 80
/*
 * ViewModel de Inventario (Encargado TI): lista, agrega y elimina equipos en el servidor.
 *
 * - cargar: Lee todos los equipos del microservicio.
 * - onNuevoClick: Abre el formulario con los campos vacíos.
 */
class InventarioViewModel : ViewModel() {

    var uiState by mutableStateOf(InventarioModel())
        private set

    fun cargar(context: Context) {
        viewModelScope.launch {
            uiState = uiState.copy(cargando = true)
            try {
                uiState = uiState.copy(equipos = PanolRepository.equipos(), cargando = false)
            } catch (e: Exception) {
                uiState = uiState.copy(cargando = false)
                Toast.makeText(context, PanolRepository.mensajeDeError(e), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun onNuevoClick() {
        uiState = uiState.copy(
            mostrarFormulario = true,
            codigo = "", nombre = "", marca = "", modelo = "", categoria = "",
            descripcion = "", ubicacion = "", stock = "",
            errorCodigo = "", errorNombre = "", errorCategoria = "",
            errorUbicacion = "", errorStock = "", errorGeneral = "",
            fotoNueva = ""
        )
    }

    // ---------- Mantenimiento ----------
    /*
     * - onMantenimientoClick: Abre el diálogo de mantenimiento del equipo.
     * - onEnviarMantenimiento: Envía unidades disponibles a mantenimiento.
     * - onDevolverMantenimiento: Devuelve unidades de mantenimiento al stock disponible.
     * - validarCantidad: Revisa que la cantidad sea un entero entre 1 y el máximo. Devuelve null (y
     *   muestra el error) si no sirve.
     */

    fun onMantenimientoClick(equipo: EquipoEntity) {
        uiState = uiState.copy(equipoMantenimiento = equipo, cantidadMantenimiento = "1", errorMantenimiento = "")
    }

    fun onCantidadMantenimientoChanged(valor: String) {
        uiState = uiState.copy(cantidadMantenimiento = valor, errorMantenimiento = "")
    }

    fun onCancelarMantenimiento() {
        uiState = uiState.copy(equipoMantenimiento = null)
    }

    fun onEnviarMantenimiento(context: Context) {
        val equipo = uiState.equipoMantenimiento ?: return
        val cantidad = validarCantidad(equipo.stockDisponible, "disponibles") ?: return
        guardarMantenimiento(context, equipo, cantidad, "$cantidad unidad(es) de ${equipo.nombre} enviadas a mantenimiento")
    }

    fun onDevolverMantenimiento(context: Context) {
        val equipo = uiState.equipoMantenimiento ?: return
        val cantidad = validarCantidad(equipo.stockMantenimiento, "en mantenimiento") ?: return
        guardarMantenimiento(context, equipo, -cantidad, "$cantidad unidad(es) de ${equipo.nombre} devueltas al stock")
    }

    private fun validarCantidad(maximo: Int, descripcion: String): Int? {
        val cantidad = uiState.cantidadMantenimiento.trim().toIntOrNull()
        val error = when {
            uiState.cantidadMantenimiento.isBlank() -> "Ingresa la cantidad"
            cantidad == null -> "Debe ser un número entero"
            cantidad < 1 -> "Debe ser al menos 1"
            cantidad > maximo -> "Solo hay $maximo $descripcion"
            else -> ""
        }
        uiState = uiState.copy(errorMantenimiento = error)
        return if (error.isEmpty()) cantidad else null
    }

    private fun guardarMantenimiento(context: Context, equipo: EquipoEntity, cantidad: Int, mensaje: String) {
        viewModelScope.launch {
            try {
                PanolRepository.ajustarMantenimiento(equipo.id, cantidad)
                uiState = uiState.copy(equipoMantenimiento = null)
                vibrar(context, 150)
                Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                cargar(context)
            } catch (e: Exception) {
                uiState = uiState.copy(errorMantenimiento = PanolRepository.mensajeDeError(e))
            }
        }
    }

    // ---------- Imágenes de los equipos (img-service) ----------
    /*
     * - onElegirFoto: Indica a quién va la foto que se va a elegir: -1 = equipo nuevo, un id = equipo
     *   existente.
     * - onFotoElegida: Foto elegida en la galería.
     * - onFotoTomada: Foto recién tomada con la cámara.
     * - procesarFoto: La reduce y, según el destino, la deja lista para el equipo nuevo o la sube al
     *   equipo elegido.
     * - onQuitarFoto: Quita la imagen: la del formulario o la que ya tiene el equipo en el servidor.
     * - onCancelarNuevo: Cierra el formulario.
     * - validar: Valida cada campo y devuelve true si no hay errores.
     * - onGuardarClick: Valida y agrega el equipo al inventario.
     * - onEliminarClick: Elimina un equipo y recarga la lista.
     */

    fun onElegirFoto(destino: Int) {
        uiState = uiState.copy(destinoFoto = destino)
    }

    fun onFotoElegida(context: Context, uri: Uri) {
        viewModelScope.launch {
            val copia = withContext(Dispatchers.IO) { copiarDeGaleria(context, uri) }
            procesarFoto(context, copia)
        }
    }

    fun onFotoTomada(context: Context) {
        viewModelScope.launch {
            procesarFoto(context, archivoCamara(context))
        }
    }

    private suspend fun procesarFoto(context: Context, origen: File?) {
        val destino = uiState.destinoFoto
        val reducida = File(context.cacheDir, if (destino == -1) "equipo_nuevo.jpg" else "equipo_subida.jpg")
        val lista = origen != null && withContext(Dispatchers.IO) {
            imagenAJpeg(origen, reducida, LADO_IMAGEN, CALIDAD_IMAGEN)
        }
        if (!lista) {
            Toast.makeText(context, "No se pudo leer la imagen", Toast.LENGTH_LONG).show()
            return
        }
        if (destino == -1) {
            uiState = uiState.copy(fotoNueva = reducida.path, fotoVersion = uiState.fotoVersion + 1)
        } else if (destino > 0) {
            try {
                PanolRepository.subirImagenEquipo(destino, reducida)
                CacheImagenes.invalidar(destino)
                uiState = uiState.copy(fotoVersion = uiState.fotoVersion + 1)
                Toast.makeText(context, "Imagen actualizada", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, PanolRepository.mensajeDeError(e), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun onQuitarFoto(context: Context) {
        val destino = uiState.destinoFoto
        if (destino == -1) {
            uiState = uiState.copy(fotoNueva = "", fotoVersion = uiState.fotoVersion + 1)
        } else if (destino > 0) {
            viewModelScope.launch {
                try {
                    PanolRepository.eliminarImagenEquipo(destino)
                    CacheImagenes.invalidar(destino)
                    uiState = uiState.copy(fotoVersion = uiState.fotoVersion + 1)
                    Toast.makeText(context, "Imagen eliminada", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, PanolRepository.mensajeDeError(e), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun onCancelarNuevo() {
        uiState = uiState.copy(mostrarFormulario = false)
    }

    fun onCodigoChanged(valor: String) {
        uiState = uiState.copy(codigo = valor, errorCodigo = "", errorGeneral = "")
    }

    fun onNombreChanged(valor: String) {
        uiState = uiState.copy(nombre = valor, errorNombre = "", errorGeneral = "")
    }

    fun onMarcaChanged(valor: String) {
        uiState = uiState.copy(marca = valor)
    }

    fun onModeloChanged(valor: String) {
        uiState = uiState.copy(modelo = valor)
    }

    fun onCategoriaChanged(valor: String) {
        uiState = uiState.copy(categoria = valor, errorCategoria = "", errorGeneral = "")
    }

    fun onDescripcionChanged(valor: String) {
        uiState = uiState.copy(descripcion = valor)
    }

    fun onUbicacionChanged(valor: String) {
        uiState = uiState.copy(ubicacion = valor, errorUbicacion = "", errorGeneral = "")
    }

    fun onStockChanged(valor: String) {
        uiState = uiState.copy(stock = valor, errorStock = "", errorGeneral = "")
    }

    private fun validar(): Boolean {
        val state = uiState
        val stock = state.stock.trim().toIntOrNull()

        val errorCodigo = when {
            state.codigo.isBlank() -> "Ingresa el código"
            state.equipos.any { it.codigo.equals(state.codigo.trim(), ignoreCase = true) } ->
                "Ya existe un equipo con ese código"
            else -> ""
        }
        val errorNombre = if (state.nombre.isBlank()) "Ingresa el nombre" else ""
        val errorCategoria = if (state.categoria.isEmpty()) "Selecciona una categoría" else ""
        val errorUbicacion = if (state.ubicacion.isBlank()) "Ingresa la ubicación" else ""
        val errorStock = when {
            state.stock.isBlank() -> "Ingresa el stock"
            stock == null -> "Debe ser un número entero"
            stock < 1 -> "Debe ser al menos 1"
            stock > 999 -> "Máximo 999 unidades"
            else -> ""
        }

        uiState = state.copy(
            errorCodigo = errorCodigo,
            errorNombre = errorNombre,
            errorCategoria = errorCategoria,
            errorUbicacion = errorUbicacion,
            errorStock = errorStock
        )
        return errorCodigo.isEmpty() && errorNombre.isEmpty() && errorCategoria.isEmpty() &&
            errorUbicacion.isEmpty() && errorStock.isEmpty()
    }

    fun onGuardarClick(context: Context) {
        if (!validar()) return
        val state = uiState
        viewModelScope.launch {
            try {
                val creado = PanolRepository.crearEquipo(
                    state.codigo.trim(),
                    state.nombre.trim(),
                    state.marca.trim(),
                    state.modelo.trim(),
                    state.categoria,
                    state.descripcion.trim(),
                    state.ubicacion.trim(),
                    state.stock.trim().toInt()
                )
                uiState = uiState.copy(mostrarFormulario = false)
                var mensaje = "${creado.nombre} agregado al inventario"
                // Si eligió una imagen se sube con el id que el servidor le dio al equipo
                if (state.fotoNueva.isNotEmpty()) {
                    try {
                        PanolRepository.subirImagenEquipo(creado.id, File(state.fotoNueva))
                        CacheImagenes.invalidar(creado.id)
                    } catch (e: Exception) {
                        mensaje = "${creado.nombre} agregado, pero no se pudo subir la imagen"
                    }
                }
                vibrar(context, 150)
                Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                cargar(context)
            } catch (e: Exception) {
                uiState = uiState.copy(errorGeneral = PanolRepository.mensajeDeError(e))
            }
        }
    }

    fun onEliminarClick(context: Context, equipo: EquipoEntity) {
        viewModelScope.launch {
            try {
                PanolRepository.eliminarEquipo(equipo.id)
                // También se borra su imagen; si falla no importa, el equipo ya no existe
                try {
                    PanolRepository.eliminarImagenEquipo(equipo.id)
                } catch (e: Exception) {
                    // sin conexión con img-service: se ignora
                }
                CacheImagenes.invalidar(equipo.id)
                vibrar(context, 150)
                Toast.makeText(context, "${equipo.nombre} eliminado", Toast.LENGTH_SHORT).show()
                cargar(context)
            } catch (e: Exception) {
                Toast.makeText(context, PanolRepository.mensajeDeError(e), Toast.LENGTH_LONG).show()
            }
        }
    }
}
