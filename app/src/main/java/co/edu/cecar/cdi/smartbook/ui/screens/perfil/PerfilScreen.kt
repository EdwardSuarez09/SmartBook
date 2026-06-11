package co.edu.cecar.cdi.smartbook.ui.screens.perfil

// ARCHIVO: PerfilScreen.kt
// Muestra los datos del usuario que inició sesión (solo lectura).

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import co.edu.cecar.cdi.smartbook.data.model.UsuarioDto
import co.edu.cecar.cdi.smartbook.data.model.camposDetalle
import co.edu.cecar.cdi.smartbook.data.repository.UsuariosRepository
import co.edu.cecar.cdi.smartbook.ui.common.DetalleCamposList
import co.edu.cecar.cdi.smartbook.ui.common.ErrorState
import co.edu.cecar.cdi.smartbook.ui.common.LoadingState
import co.edu.cecar.cdi.smartbook.ui.common.mensajeErrorAmigable

@Composable
fun PerfilScreen(
    repository: UsuariosRepository,
    modifier: Modifier = Modifier,
) {
    var loading by remember { mutableStateOf(true) }          // true = estamos pidiendo tu perfil al servidor
    var error by remember { mutableStateOf<String?>(null) }   // algo falló
    var perfil by remember { mutableStateOf<UsuarioDto?>(null) }  // datos del perfil cargados

    // LaunchedEffect: al abrir la pantalla, pedimos tu perfil al servidor una vez.
    LaunchedEffect(Unit) {
        loading = true
        val r = repository.perfil()
        loading = false
        r.fold(
            onSuccess = { perfil = it; error = null },
            onFailure = { e -> error = mensajeErrorAmigable(e.message); perfil = null },
        )
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            "Datos de tu cuenta en SmartBook.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        // when: muestra carga, error, sin datos o el detalle del perfil.
        when {
            loading -> LoadingState(modifier = Modifier.weight(1f))           // spinner
            error != null -> ErrorState(error!!, modifier = Modifier.weight(1f))  // error
            perfil == null -> ErrorState("Sin datos", modifier = Modifier.weight(1f))  // vacío
            else -> DetalleCamposList(perfil!!.camposDetalle(), modifier = Modifier.weight(1f))  // detalle
        }
    }
}
