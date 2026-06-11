package co.edu.cecar.cdi.smartbook.ui.screens.libros

// ARCHIVO: LibroDetalleScreen.kt
// Muestra stock e información de un libro con botón para editar.

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
import co.edu.cecar.cdi.smartbook.data.model.LibroDto
import co.edu.cecar.cdi.smartbook.data.model.camposDetalle
import co.edu.cecar.cdi.smartbook.data.repository.LibrosRepository
import co.edu.cecar.cdi.smartbook.ui.common.DetalleCamposList
import co.edu.cecar.cdi.smartbook.ui.common.ErrorState
import co.edu.cecar.cdi.smartbook.ui.common.LoadingState

@Composable
fun LibroDetalleScreen(
    repository: LibrosRepository,
    id: String,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var loading by remember { mutableStateOf(true) }          // true = estamos pidiendo datos al servidor
    var error by remember { mutableStateOf<String?>(null) }   // algo falló
    var libro by remember { mutableStateOf<LibroDto?>(null) }  // datos del libro cargados

    // LaunchedEffect: al entrar (o si cambia el libro), pedimos sus datos al servidor.
    LaunchedEffect(id) {
        loading = true
        val r = repository.porId(id)
        loading = false
        r.fold(
            onSuccess = { libro = it; error = null },
            onFailure = { e -> error = e.message; libro = null },
        )
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
        // when: muestra carga, error, sin datos o el detalle con botón editar.
        when {
            loading -> LoadingState(modifier = Modifier.weight(1f))           // spinner
            error != null -> ErrorState(error!!, modifier = Modifier.weight(1f))  // error
            libro == null -> ErrorState("Sin datos", modifier = Modifier.weight(1f))  // vacío
            else -> {
                Text(
                    "Consulta de stock e información del libro.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                DetalleCamposList(libro!!.camposDetalle(), modifier = Modifier.weight(1f))
                // Botón editar: navega al formulario de edición del libro.
                Button(
                    onClick = onEdit,
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 8.dp),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Text("Editar")
                }
            }
        }
    }
}
