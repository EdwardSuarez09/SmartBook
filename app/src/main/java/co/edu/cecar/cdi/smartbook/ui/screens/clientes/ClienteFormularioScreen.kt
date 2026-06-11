package co.edu.cecar.cdi.smartbook.ui.screens.clientes

// ARCHIVO: ClienteFormularioScreen.kt
// Formulario para crear un cliente nuevo o editar uno existente.

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
import co.edu.cecar.cdi.smartbook.data.model.ActualizarClienteDto
import co.edu.cecar.cdi.smartbook.data.model.CrearClienteDto
import co.edu.cecar.cdi.smartbook.data.repository.ClientesRepository
import kotlinx.coroutines.launch

@Composable
fun ClienteFormularioScreen(
    repository: ClientesRepository,
    identificacionInicial: String?,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Lo que el usuario escribe en cada campo.
    var identificacion by remember { mutableStateOf(identificacionInicial.orEmpty()) }
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }         // true = estamos guardando
    var error by remember { mutableStateOf<String?>(null) }   // mensaje si falla el guardado
    val scope = rememberCoroutineScope()  // sirve para llamar al servidor al guardar
    val editMode = !identificacionInicial.isNullOrBlank()  // true = estamos editando, no creando

    // LaunchedEffect: si estamos editando, traemos los datos actuales del servidor.
    LaunchedEffect(identificacionInicial) {
        if (identificacionInicial.isNullOrBlank()) return@LaunchedEffect
        val r = repository.porIdentificacion(identificacionInicial)
        r.getOrNull()?.let { c ->
            identificacion = identificacionInicial
            nombres = c.nombres.orEmpty()
            apellidos = c.apellidos.orEmpty()
            correo = c.email ?: c.correo.orEmpty()
            telefono = c.celular ?: c.telefono.orEmpty()
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            if (editMode) "Actualiza los datos del cliente." else "Completa los datos del nuevo cliente.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(
                Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = identificacion,
                    onValueChange = { identificacion = it },
                    label = { Text("Identificación") },
                    enabled = !editMode, // al editar, la identificación no se puede cambiar
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    ),
                )
                OutlinedTextField(
                    value = nombres,
                    onValueChange = { nombres = it },
                    label = { Text("Nombres") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    ),
                )
                OutlinedTextField(
                    value = apellidos,
                    onValueChange = { apellidos = it },
                    label = { Text("Apellidos") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    ),
                )
                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    ),
                )
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Celular") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    ),
                )
                error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            }
        }
        // Cuando toca guardar: creamos o actualizamos. Si sale bien, onSaved nos lleva atrás.
        Button(
            onClick = {
                scope.launch {
                    loading = true
                    error = null
                    val r = if (editMode) {
                        repository.actualizar(
                            identificacionInicial!!,
                            ActualizarClienteDto(
                                nombres = nombres.trim().ifBlank { null },
                                email = correo.trim().ifBlank { null },
                                celular = telefono.trim().ifBlank { null },
                            ),
                        )
                    } else {
                        repository.crear(
                            CrearClienteDto(
                                identificacion = identificacion.trim(),
                                nombres = nombres.trim(),
                                email = correo.trim().ifBlank { null },
                                celular = telefono.trim().ifBlank { null },
                            ),
                        )
                    }
                    loading = false
                    r.fold(
                        onSuccess = { onSaved() },
                        onFailure = { e -> error = e.message },
                    )
                }
            },
            enabled = !loading && identificacion.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 24.dp),
            shape = MaterialTheme.shapes.large,
        ) {
            Text(if (loading) "Guardando…" else "Guardar")
        }
    }
}
