package co.edu.cecar.cdi.smartbook.ui.screens.ventas

// ARCHIVO: VentasScreen.kt
// Lista de ventas con búsqueda, detalle y botón para registrar una nueva.

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import co.edu.cecar.cdi.smartbook.data.model.VentaDto
import co.edu.cecar.cdi.smartbook.data.model.idVisible
import co.edu.cecar.cdi.smartbook.data.model.tituloVisible
import co.edu.cecar.cdi.smartbook.data.repository.VentasRepository
import co.edu.cecar.cdi.smartbook.ui.common.ErrorState
import co.edu.cecar.cdi.smartbook.ui.common.ListaDto
import co.edu.cecar.cdi.smartbook.ui.common.LoadingState
import co.edu.cecar.cdi.smartbook.ui.common.SearchField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentasScreen(
    repository: VentasRepository,
    onOpen: (String) -> Unit,
    onCreate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }              // texto del buscador
    var loading by remember { mutableStateOf(true) }          // true = estamos cargando la lista
    var error by remember { mutableStateOf<String?>(null) }   // algo falló al pedir datos
    var items by remember { mutableStateOf<List<VentaDto>>(emptyList()) }  // ventas mostradas

    // LaunchedEffect: cuando cambia la búsqueda, volvemos a pedir ventas al servidor.
    LaunchedEffect(query) {
        loading = true
        val r = repository.listar(query.ifBlank { null })
        loading = false
        r.fold(
            onSuccess = { items = it; error = null },
            onFailure = { e -> error = e.message; items = emptyList() },
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            // Botón flotante +: abre el formulario de nueva venta.
            FloatingActionButton(
                onClick = onCreate,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva venta")
            }
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            SearchField(query, { query = it }, "Buscar venta")  // callback: actualiza la búsqueda
            // when: muestra carga, error o la lista de ventas.
            when {
                loading -> LoadingState(modifier = Modifier.weight(1f))           // spinner
                error != null -> ErrorState(error!!, modifier = Modifier.weight(1f))  // error
                else -> ListaDto(
                    items = items,
                    titulo = { it.tituloVisible() },
                    subtitulo = { it.idVisible()?.let { id -> "ID: $id" } },
                    id = { it.idVisible() },
                    onItemClick = { row -> row.idVisible()?.let(onOpen) },  // callback: abre detalle de la venta
                    emptyMessage = "No hay ventas registradas.",
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
