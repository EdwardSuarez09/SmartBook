package co.edu.cecar.cdi.smartbook.ui.screens.inventarios

// ARCHIVO: InventariosScreen.kt
// Inventario de libros con resumen, avisos de stock bajo y filtro por lote.

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.edu.cecar.cdi.smartbook.data.model.InventarioItemDto
import co.edu.cecar.cdi.smartbook.data.model.camposDetalle
import co.edu.cecar.cdi.smartbook.data.model.esStockBajo
import co.edu.cecar.cdi.smartbook.data.model.idVisible
import co.edu.cecar.cdi.smartbook.data.model.subtituloVisible
import co.edu.cecar.cdi.smartbook.data.model.tituloVisible
import co.edu.cecar.cdi.smartbook.data.repository.InventariosRepository
import co.edu.cecar.cdi.smartbook.ui.common.DialogoDetalle
import co.edu.cecar.cdi.smartbook.ui.common.EmptyListState
import co.edu.cecar.cdi.smartbook.ui.common.ErrorState
import co.edu.cecar.cdi.smartbook.ui.common.LoadingState
import co.edu.cecar.cdi.smartbook.ui.common.mensajeErrorAmigable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventariosScreen(
    repository: InventariosRepository,
    loteFiltro: Int? = null,
    modifier: Modifier = Modifier,
) {
    var loading by remember { mutableStateOf(true) }          // true = estamos cargando la lista
    var error by remember { mutableStateOf<String?>(null) }   // algo falló al pedir datos
    var items by remember { mutableStateOf<List<InventarioItemDto>>(emptyList()) }  // libros en inventario
    var seleccionado by remember { mutableStateOf<InventarioItemDto?>(null) }  // ítem del diálogo de detalle

    // LaunchedEffect: si cambia el filtro de lote (p. ej. desde Lotes), recargamos la lista.
    LaunchedEffect(loteFiltro) {
        loading = true
        val r = repository.listar(loteFiltro)
        loading = false
        r.fold(
            onSuccess = { items = it; error = null },
            onFailure = { e -> error = mensajeErrorAmigable(e.message); items = emptyList() },
        )
    }

    // Contamos títulos, unidades y cuántos tienen poco stock (con la lista que ya tenemos).
    val totalUnidades = items.sumOf { (it.unidades ?: it.cantidad ?: it.stock ?: 0) }
    val stockBajo = items.count { it.esStockBajo() }

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            if (loteFiltro != null) "Inventario del lote $loteFiltro" else "Inventario general",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        // when: muestra carga, error, lista vacía o inventario con resumen.
        when {
            loading -> LoadingState(modifier = Modifier.weight(1f))           // spinner
            error != null -> ErrorState(error!!, modifier = Modifier.weight(1f))  // error
            items.isEmpty() -> EmptyListState("No hay libros en inventario.", modifier = Modifier.weight(1f))  // vacío
            else -> {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ResumenCard("Títulos", items.size.toString(), Modifier.weight(1f))
                    ResumenCard("Unidades", totalUnidades.toString(), Modifier.weight(1f))
                    ResumenCard("Stock bajo", stockBajo.toString(), Modifier.weight(1f))
                }
                Text(
                    "Libros en inventario",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                )
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(items, key = { it.idVisible() ?: it.hashCode().toString() }) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { seleccionado = item },  // callback: abre diálogo de detalle
                            // Si queda poco stock, la tarjeta se ve en rojo.
                            colors = CardDefaults.cardColors(
                                containerColor = if (item.esStockBajo()) {
                                    MaterialTheme.colorScheme.errorContainer
                                } else {
                                    MaterialTheme.colorScheme.surface
                                },
                            ),
                        ) {
                            ListItem(
                                headlineContent = {
                                    Text(item.tituloVisible(), style = MaterialTheme.typography.titleSmall)
                                },
                                supportingContent = {
                                    Text(
                                        item.subtituloVisible(),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    // Diálogo de detalle del ítem seleccionado. Callback onDismiss: limpia la selección.
    seleccionado?.let { item ->
        DialogoDetalle(
            titulo = item.tituloVisible(),
            campos = item.camposDetalle(),
            onDismiss = { seleccionado = null },
        )
    }
}

@Composable
private fun ResumenCard(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(titulo, style = MaterialTheme.typography.labelMedium)
            Text(valor, style = MaterialTheme.typography.titleLarge)
        }
    }
}
