package co.edu.cecar.cdi.smartbook.ui.screens.ingresos

// ARCHIVO: IngresosScreen.kt
// Lista de ingresos al inventario con detalle en diálogo y botón para crear.

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
import co.edu.cecar.cdi.smartbook.data.model.IngresoDto
import co.edu.cecar.cdi.smartbook.data.model.camposDetalle
import co.edu.cecar.cdi.smartbook.data.model.idVisible
import co.edu.cecar.cdi.smartbook.data.model.subtituloVisible
import co.edu.cecar.cdi.smartbook.data.model.tituloVisible
import co.edu.cecar.cdi.smartbook.data.repository.IngresosRepository
import co.edu.cecar.cdi.smartbook.ui.common.DialogoDetalle
import co.edu.cecar.cdi.smartbook.ui.common.ErrorState
import co.edu.cecar.cdi.smartbook.ui.common.ListaDto
import co.edu.cecar.cdi.smartbook.ui.common.LoadingState
import co.edu.cecar.cdi.smartbook.ui.common.mensajeErrorAmigable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngresosScreen(
    repository: IngresosRepository,
    onCreate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var loading by remember { mutableStateOf(true) }          // true = estamos cargando la lista
    var error by remember { mutableStateOf<String?>(null) }   // algo falló al pedir datos
    var items by remember { mutableStateOf<List<IngresoDto>>(emptyList()) }  // ingresos mostrados
    var seleccionado by remember { mutableStateOf<IngresoDto?>(null) }  // ingreso del diálogo de detalle

    // LaunchedEffect: al abrir la pantalla, pedimos todos los ingresos al servidor.
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
            // Botón flotante +: abre el formulario de nuevo ingreso.
            FloatingActionButton(
                onClick = onCreate,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo ingreso")
            }
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // when: muestra carga, error o la lista de ingresos.
            when {
                loading -> LoadingState(modifier = Modifier.weight(1f))           // spinner
                error != null -> ErrorState(error!!, modifier = Modifier.weight(1f))  // error
                else -> ListaDto(
                    items = items,
                    titulo = { it.tituloVisible() },
                    subtitulo = { it.subtituloVisible().ifBlank { null } },
                    id = { it.idVisible() ?: "${it.libroId}-${it.lote}" },
                    onItemClick = { seleccionado = it },  // callback: abre diálogo de detalle
                    emptyMessage = "No hay movimientos de ingreso.",
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }

    // Diálogo de detalle. Callback onDismiss: limpia la selección al cerrar.
    seleccionado?.let { ingreso ->
        DialogoDetalle(
            titulo = ingreso.tituloVisible(),
            campos = ingreso.camposDetalle(),
            onDismiss = { seleccionado = null },
        )
    }
}
