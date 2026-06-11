package co.edu.cecar.cdi.smartbook.ui.screens.clientes

// ARCHIVO: ClienteDetalleScreen.kt
// Muestra los datos de un cliente en solo lectura con botón para editar.

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import co.edu.cecar.cdi.smartbook.data.model.ClienteDto
import co.edu.cecar.cdi.smartbook.data.model.camposDetalle
import co.edu.cecar.cdi.smartbook.data.repository.ClientesRepository
import co.edu.cecar.cdi.smartbook.ui.common.DetalleCamposList
import co.edu.cecar.cdi.smartbook.ui.common.ErrorState
import co.edu.cecar.cdi.smartbook.ui.common.LoadingState

@Composable
fun ClienteDetalleScreen(
    repository: ClientesRepository,
    identificacion: String,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var loading by remember { mutableStateOf(true) }          // true = estamos pidiendo datos al servidor
    var error by remember { mutableStateOf<String?>(null) }   // algo falló
    var cliente by remember { mutableStateOf<ClienteDto?>(null) }  // datos del cliente cargados

    // LaunchedEffect: al entrar (o si cambia el cliente), pedimos sus datos al servidor.
    LaunchedEffect(identificacion) {
        loading = true
        val r = repository.porIdentificacion(identificacion)
        loading = false
        r.fold(
            onSuccess = { cliente = it; error = null },
            onFailure = { e -> error = e.message; cliente = null },
        )
    }

    Column(modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
        // when: muestra carga, error, sin datos o el detalle con botón editar.
        when {
            loading -> LoadingState(modifier = Modifier.weight(1f))           // spinner
            error != null -> ErrorState(error!!, modifier = Modifier.weight(1f))  // error
            cliente == null -> ErrorState("Sin datos", modifier = Modifier.weight(1f))  // vacío
            else -> {
                DetalleCamposList(cliente!!.camposDetalle(), modifier = Modifier.weight(1f))
                // Botón editar: navega al formulario de edición.
                Button(
                    onClick = onEdit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 8.dp),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Text("Editar")
                }
            }
        }
    }
}
