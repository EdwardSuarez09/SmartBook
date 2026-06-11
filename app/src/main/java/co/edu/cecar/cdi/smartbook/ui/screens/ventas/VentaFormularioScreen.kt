package co.edu.cecar.cdi.smartbook.ui.screens.ventas

// ARCHIVO: VentaFormularioScreen.kt
// Formulario para registrar una venta con cliente, libro, lote y cantidad.

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.edu.cecar.cdi.smartbook.data.model.CrearVentaDto
import co.edu.cecar.cdi.smartbook.data.model.CrearVentaItemDto
import co.edu.cecar.cdi.smartbook.data.repository.VentasRepository
import kotlinx.coroutines.launch

@Composable
fun VentaFormularioScreen(
    repository: VentasRepository,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Datos del cliente y del comprobante.
    var identificacionCliente by remember { mutableStateOf("") }
    var comprobante by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }
    // Datos del producto vendido (por ahora solo un ítem).
    var libroId by remember { mutableStateOf("") }
    var lote by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("1") }
    var loading by remember { mutableStateOf(false) }  // true = estamos guardando
    var error by remember { mutableStateOf<String?>(null) }   // mensaje si falla el registro
    val scope = rememberCoroutineScope()  // sirve para llamar al servidor al guardar

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            "Registra una venta con cliente e ítems (libro, lote y cantidad).",
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
                campo("Identificación del cliente", identificacionCliente) { identificacionCliente = it }
                campo("Número de comprobante", comprobante) { comprobante = it }
                campo("Observaciones", observaciones) { observaciones = it }
                HorizontalDivider()
                Text("Ítem de venta", style = MaterialTheme.typography.titleSmall)
                campo("ID del libro", libroId) { libroId = it }
                campo("Lote (número)", lote) { lote = it }
                campo("Cantidad", cantidad) { cantidad = it }
                error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            }
        }
        // Botón registrar: valida los datos y envía la venta al servidor.
        Button(
            onClick = {
                val libro = libroId.toIntOrNull()
                val loteNum = lote.toIntOrNull()
                val cant = cantidad.toIntOrNull()
                // when: revisa cada campo antes de llamar al servidor.
                when {
                    identificacionCliente.isBlank() -> error = "Ingresa la identificación del cliente."
                    libro == null -> error = "ID de libro inválido."
                    loteNum == null -> error = "Lote inválido."
                    cant == null || cant < 1 -> error = "Cantidad inválida."
                    else -> scope.launch {
                        loading = true
                        error = null
                        val r = repository.crear(
                            CrearVentaDto(
                                identificacionCliente = identificacionCliente.trim(),
                                numeroComprobante = comprobante.trim().ifBlank { null },
                                observaciones = observaciones.trim().ifBlank { null },
                                items = listOf(CrearVentaItemDto(libroId = libro, lote = loteNum, cantidad = cant)),
                            ),
                        )
                        loading = false
                        r.fold(onSuccess = { onSaved() }, onFailure = { e -> error = e.message })
                    }
                }
            },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 24.dp),
            shape = MaterialTheme.shapes.large,
        ) {
            Text(if (loading) "Guardando…" else "Registrar venta")
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
        singleLine = label != "Observaciones",
        shape = MaterialTheme.shapes.large,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        ),
    )
}
