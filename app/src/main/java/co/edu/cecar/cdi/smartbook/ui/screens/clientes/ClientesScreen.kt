package co.edu.cecar.cdi.smartbook.ui.screens.clientes

// ARCHIVO: ClientesScreen.kt
// Lista de clientes con búsqueda, detalle y botón para crear uno nuevo.

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
import co.edu.cecar.cdi.smartbook.data.model.ClienteDto
import co.edu.cecar.cdi.smartbook.data.model.idVisible
import co.edu.cecar.cdi.smartbook.data.model.tituloVisible
import co.edu.cecar.cdi.smartbook.data.repository.ClientesRepository
import co.edu.cecar.cdi.smartbook.ui.common.ErrorState
import co.edu.cecar.cdi.smartbook.ui.common.ListaDto
import co.edu.cecar.cdi.smartbook.ui.common.LoadingState
import co.edu.cecar.cdi.smartbook.ui.common.SearchField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientesScreen(
    repository: ClientesRepository,
    onOpen: (String) -> Unit,
    onCreate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }              // lo que escribe en el buscador
    var loading by remember { mutableStateOf(true) }          // true = estamos cargando la lista
    var error by remember { mutableStateOf<String?>(null) }   // algo falló al pedir datos
    var items by remember { mutableStateOf<List<ClienteDto>>(emptyList()) }  // lista que muestra en pantalla

    // LaunchedEffect: cada vez que cambia la búsqueda, volvemos a pedir la lista al servidor.
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
            // Botón flotante +: abre el formulario de nuevo cliente.
            FloatingActionButton(
                onClick = onCreate,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo cliente")
            }
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            SearchField(query, { query = it }, "Buscar por nombre")  // callback: actualiza el texto de búsqueda
            // when: muestra carga, error o la lista de clientes.
            when {
                loading -> LoadingState(modifier = Modifier.weight(1f))           // spinner
                error != null -> ErrorState(error!!, modifier = Modifier.weight(1f))  // error
                else -> ListaDto(
                    items = items,
                    titulo = { it.tituloVisible() },
                    subtitulo = { it.idVisible()?.let { id -> "ID: $id" } },
                    id = { it.idVisible() },
                    onItemClick = { row -> row.idVisible()?.let(onOpen) },  // callback: abre detalle del cliente
                    emptyMessage = "No hay clientes. Pulsa + para registrar uno.",
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
