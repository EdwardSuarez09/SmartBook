package co.edu.cecar.cdi.smartbook.ui.screens.ingresos

// ARCHIVO: IngresoFormularioScreen.kt
// Formulario para registrar la entrada de libros al inventario.

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.edu.cecar.cdi.smartbook.data.model.CrearIngresoDto
import co.edu.cecar.cdi.smartbook.data.repository.IngresosRepository
import kotlinx.coroutines.launch

@Composable
fun IngresoFormularioScreen(
    repository: IngresosRepository,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var libroId by remember { mutableStateOf("") }       // ID del libro que ingresa
    var unidades by remember { mutableStateOf("") }      // cantidad de unidades
    var lote by remember { mutableStateOf("") }          // número de lote
    var valorCompra by remember { mutableStateOf("") }   // precio de compra
    var valorVenta by remember { mutableStateOf("") }    // precio de venta al público
    var loading by remember { mutableStateOf(false) }  // true = estamos guardando
    var error by remember { mutableStateOf<String?>(null) }  // mensaje si falla el registro
    val scope = rememberCoroutineScope()  // sirve para llamar al servidor al guardar

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            "Registra la entrada de libros al inventario.",
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
                campo("ID del libro", libroId) { libroId = it }
                campo("Unidades", unidades) { unidades = it }
                campo("Lote (número)", lote) { lote = it }
                campo("Valor compra", valorCompra) { valorCompra = it }
                campo("Valor venta público", valorVenta) { valorVenta = it }
                error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            }
        }
        // Botón registrar: valida los campos y envía el ingreso al servidor.
        Button(
            onClick = {
                val libro = libroId.toIntOrNull()
                val u = unidades.toIntOrNull()
                val l = lote.toIntOrNull()
                // when: revisa cada campo antes de llamar al servidor.
                when {
                    libro == null -> error = "ID de libro inválido."
                    u == null || u < 1 -> error = "Unidades inválidas."
                    l == null -> error = "Lote inválido."
                    else -> scope.launch {
                        loading = true
                        error = null
                        val r = repository.crear(
                            CrearIngresoDto(
                                libroId = libro,
                                unidades = u,
                                lote = l,
                                valorCompra = valorCompra.toDoubleOrNull(),
                                valorVentaPublico = valorVenta.toDoubleOrNull(),
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
            Text(if (loading) "Guardando…" else "Registrar ingreso")
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
