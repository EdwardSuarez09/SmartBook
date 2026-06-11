package co.edu.cecar.cdi.smartbook.ui.screens.ventas

// ARCHIVO: VentaDetalleScreen.kt
// Muestra todos los datos de una venta en solo lectura (sin edición).

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.edu.cecar.cdi.smartbook.data.model.VentaDto
import co.edu.cecar.cdi.smartbook.data.model.camposDetalle
import co.edu.cecar.cdi.smartbook.data.repository.VentasRepository
import co.edu.cecar.cdi.smartbook.ui.common.DetalleCamposList
import co.edu.cecar.cdi.smartbook.ui.common.ErrorState
import co.edu.cecar.cdi.smartbook.ui.common.LoadingState

@Composable
fun VentaDetalleScreen(
    repository: VentasRepository,
    id: String,
    modifier: Modifier = Modifier,
) {
    var loading by remember { mutableStateOf(true) }          // true = estamos pidiendo datos al servidor
    var error by remember { mutableStateOf<String?>(null) }   // algo falló
    var venta by remember { mutableStateOf<VentaDto?>(null) }  // datos de la venta cargados

    // LaunchedEffect: al entrar (o si cambia la venta), la pedimos al servidor.
    LaunchedEffect(id) {
        loading = true
        val r = repository.porId(id)
        loading = false
        r.fold(
            onSuccess = { venta = it; error = null },
            onFailure = { e -> error = e.message; venta = null },
        )
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
        // when: muestra carga, error, sin datos o el detalle de la venta.
        when {
            loading -> LoadingState(modifier = Modifier.weight(1f))           // spinner
            error != null -> ErrorState(error!!, modifier = Modifier.weight(1f))  // error
            venta == null -> ErrorState("Sin datos", modifier = Modifier.weight(1f))  // vacío
            else -> DetalleCamposList(venta!!.camposDetalle(), modifier = Modifier.weight(1f))  // detalle
        }
    }
}
