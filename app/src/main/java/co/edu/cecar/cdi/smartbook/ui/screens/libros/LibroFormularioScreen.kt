package co.edu.cecar.cdi.smartbook.ui.screens.libros

// ARCHIVO: LibroFormularioScreen.kt
// Formulario para crear un libro nuevo o editar uno del catálogo.

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.edu.cecar.cdi.smartbook.data.model.ActualizarLibroDto
import co.edu.cecar.cdi.smartbook.data.model.CrearLibroDto
import co.edu.cecar.cdi.smartbook.data.repository.LibrosRepository
import kotlinx.coroutines.launch

@Composable
fun LibroFormularioScreen(
    repository: LibrosRepository,
    idInicial: String?,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Lo que escribe el usuario. Al crear también pide unidades, lote y precios.
    var nombre by remember { mutableStateOf("") }
    var nivel by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var edicion by remember { mutableStateOf("") }
    var unidades by remember { mutableStateOf("") }
    var lote by remember { mutableStateOf("") }
    var valorCompra by remember { mutableStateOf("") }
    var valorVenta by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }  // true = estamos guardando
    var error by remember { mutableStateOf<String?>(null) }   // mensaje si falla el guardado
    val scope = rememberCoroutineScope()  // sirve para llamar al servidor al guardar
    val editMode = !idInicial.isNullOrBlank()  // true = estamos editando, no creando

    // LaunchedEffect: si estamos editando, llenamos el formulario con datos del servidor.
    LaunchedEffect(idInicial) {
        if (idInicial.isNullOrBlank()) return@LaunchedEffect
        repository.porId(idInicial).getOrNull()?.let { l ->
            nombre = l.nombre.orEmpty()
            nivel = l.nivel.orEmpty()
            tipo = l.tipo?.toString().orEmpty()
            edicion = l.edicion.orEmpty()
            unidades = l.unidades?.toString().orEmpty()
            lote = l.lote?.toString().orEmpty()
            valorCompra = l.valorCompra?.toString().orEmpty()
            valorVenta = l.valorVentaPublico?.toString().orEmpty()
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            if (editMode) "Actualiza el libro." else "Al registrar, el libro se agrega al inventario.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                campo("Nombre", nombre, { nombre = it })
                campo("Nivel", nivel, { nivel = it })
                campo("Tipo (ej. StudentsBook)", tipo, { tipo = it })
                campo("Edición", edicion, { edicion = it })
                // Estos campos solo aparecen al crear un libro nuevo.
                if (!editMode) {
                    campo("Unidades", unidades, { unidades = it })
                    campo("Lote (número)", lote, { lote = it })
                    campo("Valor compra", valorCompra, { valorCompra = it })
                    campo("Valor venta público", valorVenta, { valorVenta = it })
                }
                error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            }
        }
        // Botón guardar: crea o actualiza el libro según el modo. Si sale bien, vuelve atrás.
        Button(
            onClick = {
                scope.launch {
                    loading = true
                    error = null
                    val r = if (editMode) {
                        repository.actualizar(
                            idInicial!!,
                            ActualizarLibroDto(
                                nombre = nombre.trim().ifBlank { null },
                                nivel = nivel.trim().ifBlank { null },
                                tipo = tipo.trim().ifBlank { null },
                                edicion = edicion.trim().ifBlank { null },
                            ),
                        )
                    } else {
                        if (nombre.isBlank()) {
                            loading = false
                            error = "El nombre es obligatorio."
                            return@launch
                        }
                        repository.crear(
                            CrearLibroDto(
                                nombre = nombre.trim(),
                                nivel = nivel.trim().ifBlank { null },
                                tipo = tipo.trim().ifBlank { null },
                                edicion = edicion.trim().ifBlank { null },
                                unidades = unidades.toIntOrNull(),
                                lote = lote.toIntOrNull(),
                                valorCompra = valorCompra.toDoubleOrNull(),
                                valorVentaPublico = valorVenta.toDoubleOrNull(),
                            ),
                        )
                    }
                    loading = false
                    r.fold(onSuccess = { onSaved() }, onFailure = { e -> error = e.message })
                }
            },
            enabled = !loading && nombre.isNotBlank(),
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 24.dp),
            shape = MaterialTheme.shapes.large,
        ) {
            Text(if (loading) "Guardando…" else "Guardar")
        }
    }
}

@Composable
private fun campo(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        ),
    )
}
