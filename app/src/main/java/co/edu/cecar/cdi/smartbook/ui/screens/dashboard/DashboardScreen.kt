package co.edu.cecar.cdi.smartbook.ui.screens.dashboard

// ARCHIVO: DashboardScreen.kt
// Pantalla principal con indicadores del negocio, accesos rápidos y ventas del día.

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.edu.cecar.cdi.smartbook.data.model.DashboardDto
import co.edu.cecar.cdi.smartbook.data.model.VentaResumenDto
import co.edu.cecar.cdi.smartbook.data.model.datosReales
import co.edu.cecar.cdi.smartbook.data.repository.DashboardRepository
import co.edu.cecar.cdi.smartbook.navigation.MainRoute
import co.edu.cecar.cdi.smartbook.ui.common.ErrorState
import co.edu.cecar.cdi.smartbook.ui.common.LoadingState

@Composable
fun DashboardScreen(
    repository: DashboardRepository,
    onAccionRapida: (MainRoute) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Guardamos si está cargando, si hubo error y los datos que llegaron.
    var loading by remember { mutableStateOf(true) }          // true = estamos cargando datos del servidor
    var error by remember { mutableStateOf<String?>(null) }   // mensaje si falló la petición
    var data by remember { mutableStateOf<DashboardDto?>(null) }  // resumen del negocio recibido

    // LaunchedEffect: al abrir la pantalla, pedimos los datos al servidor una vez.
    // Ponemos loading en true, luego guardamos data o error según lo que responda.
    LaunchedEffect(Unit) {
        loading = true
        val r = repository.obtener()
        loading = false
        r.fold(
            onSuccess = { data = it; error = null },
            onFailure = { e -> error = e.message; data = null },
        )
    }

    // when: muestra carga, error o el contenido del dashboard según el estado.
    when {
        loading -> LoadingState(modifier = modifier.fillMaxSize())       // spinner mientras carga
        error != null -> ErrorState(error!!, modifier = modifier.fillMaxSize())  // mensaje de error
        data == null -> ErrorState("Sin datos", modifier = modifier.fillMaxSize())  // sin datos
        else -> DashboardContent(data!!.datosReales(), onAccionRapida, modifier)  // contenido normal
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DashboardContent(
    dashboard: DashboardDto,
    onAccionRapida: (MainRoute) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Usamos ventas de hoy; si no hay, mostramos las últimas ventas.
    val ventasHoy = dashboard.ventasHoy ?: dashboard.ultimasVentas ?: emptyList()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text("Resumen del negocio", style = MaterialTheme.typography.titleLarge)
        Text(
            "Indicadores en tiempo real desde el servidor.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(16.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard("Clientes", dashboard.totalClientes?.toString() ?: "—", Modifier.weight(1f))
            MetricCard("Libros", dashboard.totalLibros?.toString() ?: "—", Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard("Ventas del mes", dashboard.ventasMes?.toString() ?: "—", Modifier.weight(1f))
            MetricCard("Ingresos del mes", dashboard.ingresosMes ?: "—", Modifier.weight(1f))
        }

        Spacer(Modifier.height(20.dp))
        Text("Acciones rápidas", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        // Cuando toca un chip, vamos a crear cliente, libro o venta.
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(
                onClick = { onAccionRapida(MainRoute.ClienteCreate) },
                label = { Text("Nuevo cliente") },
                leadingIcon = { Icon(Icons.Default.People, null) },
            )
            AssistChip(
                onClick = { onAccionRapida(MainRoute.LibroCreate) },
                label = { Text("Nuevo libro") },
                leadingIcon = { Icon(Icons.Default.MenuBook, null) },
            )
            AssistChip(
                onClick = { onAccionRapida(MainRoute.VentaCreate) },
                label = { Text("Nueva venta") },
                leadingIcon = { Icon(Icons.Default.ReceiptLong, null) },
            )
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
        Spacer(Modifier.height(16.dp))
        Text("Últimas ventas del día", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(10.dp))
        if (ventasHoy.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                shape = MaterialTheme.shapes.large,
            ) {
                Text(
                    "No hay ventas registradas hoy.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(20.dp),
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ventasHoy.forEach { row ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(ventaTitulo(row), style = MaterialTheme.typography.titleSmall)
                            Text(
                                ventaTotal(row),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun ventaTitulo(row: VentaResumenDto): String =
    row.cliente?.takeIf { it.isNotBlank() } ?: row.nombre?.takeIf { it.isNotBlank() } ?: "—"

private fun ventaTotal(row: VentaResumenDto): String =
    row.total?.takeIf { it.isNotBlank() } ?: row.valor?.takeIf { it.isNotBlank() } ?: "—"

@Composable
private fun MetricCard(title: String, value: String?, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(104.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            Modifier.fillMaxSize().padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text(
                value ?: "—",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.align(Alignment.End),
            )
        }
    }
}
