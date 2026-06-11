package co.edu.cecar.cdi.smartbook.ui.screens.lotes

// ARCHIVO: LotesScreen.kt
// Lista de lotes con detalle en diálogo e inventario por lote.

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.edu.cecar.cdi.smartbook.data.model.LoteDto
import co.edu.cecar.cdi.smartbook.data.model.camposDetalle
import co.edu.cecar.cdi.smartbook.data.model.idVisible
import co.edu.cecar.cdi.smartbook.data.model.subtituloVisible
import co.edu.cecar.cdi.smartbook.data.model.tituloVisible
import co.edu.cecar.cdi.smartbook.data.repository.LotesRepository
import co.edu.cecar.cdi.smartbook.ui.common.ErrorState
import co.edu.cecar.cdi.smartbook.ui.common.ListaDto
import co.edu.cecar.cdi.smartbook.ui.common.LoadingState
import co.edu.cecar.cdi.smartbook.ui.common.mensajeErrorAmigable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LotesScreen(
    repository: LotesRepository,
    onCreate: () -> Unit,
    onVerInventario: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var loading by remember { mutableStateOf(true) }          // true = estamos cargando la lista
    var error by remember { mutableStateOf<String?>(null) }   // algo falló al pedir datos
    var items by remember { mutableStateOf<List<LoteDto>>(emptyList()) }  // lotes mostrados
    var seleccionado by remember { mutableStateOf<LoteDto?>(null) } // lote del que vemos el detalle en diálogo

    // LaunchedEffect: al abrir la pantalla, pedimos todos los lotes al servidor.
    LaunchedEffect(Unit) {
        loading = true
        val r = repository.listar()
        loading = false
        r.fold(
            onSuccess = { items = it; error = null },
            onFailure = { e -> error = mensajeErrorAmigable(e.message); items = emptyList() },
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            // Botón flotante +: abre el formulario de nuevo lote.
            FloatingActionButton(
                onClick = onCreate,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo lote")
            }
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // when: muestra carga, error o la lista de lotes.
            when {
                loading -> LoadingState(modifier = Modifier.weight(1f))           // spinner
                error != null -> ErrorState(error!!, modifier = Modifier.weight(1f))  // error
                else -> ListaDto(
                    items = items,
                    titulo = { it.tituloVisible() },
                    subtitulo = { it.subtituloVisible() },
                    id = { it.idVisible() },
                    onItemClick = { seleccionado = it },  // callback: abre diálogo de detalle
                    emptyMessage = "No hay lotes. Crea el primero con +.",
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }

    // Cuando el usuario toca un lote, mostramos este diálogo con sus datos.
    seleccionado?.let { lote ->
        val campos = lote.camposDetalle()
        AlertDialog(
            onDismissRequest = { seleccionado = null },  // callback: cierra el diálogo
            title = { Text(lote.tituloVisible()) },
            text = {
                Column {
                    campos.forEach { (k, v) ->
                        Text(k, style = MaterialTheme.typography.labelMedium)
                        Text(v, modifier = Modifier.padding(bottom = 8.dp))
                    }
                }
            },
            confirmButton = {
                lote.lote?.let { num ->
                    // Botón ver inventario: cierra el diálogo y navega al inventario del lote.
                    TextButton(onClick = {
                        seleccionado = null
                        onVerInventario(num)
                    }) {
                        Text("Ver inventario")
                    }
                }
            },
            dismissButton = {
                // Botón cerrar: solo cierra el diálogo.
                TextButton(onClick = { seleccionado = null }) { Text("Cerrar") }
            },
        )
    }
}
