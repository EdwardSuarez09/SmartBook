package co.edu.cecar.cdi.smartbook.ui.screens.lotes

// ARCHIVO: LoteFormularioScreen.kt
// Formulario para registrar un lote nuevo en el sistema.

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import co.edu.cecar.cdi.smartbook.data.model.CrearLoteDto
import co.edu.cecar.cdi.smartbook.data.repository.LotesRepository
import kotlinx.coroutines.launch

@Composable
fun LoteFormularioScreen(
    repository: LotesRepository,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var codigo by remember { mutableStateOf("") }  // número del lote que escribe el usuario
    var loading by remember { mutableStateOf(false) }  // true = estamos guardando
    var error by remember { mutableStateOf<String?>(null) }  // mensaje si falla el registro
    val scope = rememberCoroutineScope()  // sirve para llamar al servidor al guardar

    Column(
        modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            "Registra un lote para asociarlo a libros e inventario.",
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
                    value = codigo,
                    onValueChange = { codigo = it },
                    label = { Text("Código del lote") },
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
        // Botón registrar: valida el código y crea el lote en el servidor.
        Button(
            onClick = {
                scope.launch {
                    loading = true
                    val loteNum = codigo.trim().toIntOrNull()
                    if (loteNum == null) {
                        loading = false
                        error = "Ingresa un número de lote válido."
                        return@launch
                    }
                    val r = repository.crear(CrearLoteDto(lote = loteNum))
                    loading = false
                    r.fold(onSuccess = { onSaved() }, onFailure = { e -> error = e.message })
                }
            },
            enabled = !loading && codigo.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            shape = MaterialTheme.shapes.large,
        ) {
            Text(if (loading) "Guardando…" else "Registrar")
        }
    }
}
