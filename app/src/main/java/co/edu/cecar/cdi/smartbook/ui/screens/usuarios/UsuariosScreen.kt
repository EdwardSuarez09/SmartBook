package co.edu.cecar.cdi.smartbook.ui.screens.usuarios

// ARCHIVO: UsuariosScreen.kt
// Lista de usuarios, detalle con activar/desactivar y formulario de creación/edición.

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.edu.cecar.cdi.smartbook.data.model.UsuarioDto
import co.edu.cecar.cdi.smartbook.data.model.camposDetalle
import co.edu.cecar.cdi.smartbook.data.model.idVisible
import co.edu.cecar.cdi.smartbook.data.model.tituloVisible
import co.edu.cecar.cdi.smartbook.data.repository.UsuariosRepository
import co.edu.cecar.cdi.smartbook.ui.common.DetalleCamposList
import co.edu.cecar.cdi.smartbook.ui.common.ErrorState
import co.edu.cecar.cdi.smartbook.ui.common.ListaDto
import co.edu.cecar.cdi.smartbook.ui.common.LoadingState
import co.edu.cecar.cdi.smartbook.ui.common.mensajeErrorAmigable
import co.edu.cecar.cdi.smartbook.data.model.subtituloVisible

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosScreen(
    repository: UsuariosRepository,
    onOpen: (String) -> Unit,
    onCreate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var loading by remember { mutableStateOf(true) }          // true = estamos cargando la lista
    var error by remember { mutableStateOf<String?>(null) }   // algo falló al pedir datos
    var items by remember { mutableStateOf<List<UsuarioDto>>(emptyList()) }  // usuarios mostrados

    // LaunchedEffect: al abrir la pantalla, pedimos todos los usuarios al servidor.
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
            // Botón flotante +: abre el formulario de nuevo usuario.
            FloatingActionButton(
                onClick = onCreate,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo usuario")
            }
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // when: muestra carga, error o la lista de usuarios.
            when {
                loading -> LoadingState(modifier = Modifier.weight(1f))           // spinner
                error != null -> ErrorState(error!!, modifier = Modifier.weight(1f))  // error
                else -> ListaDto(
                    items = items,
                    titulo = { it.tituloVisible() },
                    subtitulo = { it.subtituloVisible().ifBlank { null } },
                    id = { it.idVisible() },
                    onItemClick = { row -> row.idVisible()?.let(onOpen) },  // callback: abre detalle del usuario
                    emptyMessage = "No hay usuarios en el sistema.",
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
fun UsuarioDetalleScreen(
    repository: UsuariosRepository,
    id: String,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var loading by remember { mutableStateOf(true) }          // true = estamos pidiendo datos al servidor
    var error by remember { mutableStateOf<String?>(null) }   // algo falló
    var usuario by remember { mutableStateOf<UsuarioDto?>(null) }  // datos del usuario cargados
    var toggling by remember { mutableStateOf(false) } // true = estamos cambiando activo/inactivo
    val scope = rememberCoroutineScope()  // sirve para llamar al servidor

    // Volvemos a pedir los datos del usuario (p. ej. después de activar o desactivar).
    fun recargar() {
        scope.launch {
            loading = true
            val r = repository.porId(id)
            loading = false
            r.fold(
                onSuccess = { usuario = it; error = null },
                onFailure = { e -> error = mensajeErrorAmigable(e.message); usuario = null },
            )
        }
    }

    // LaunchedEffect: al entrar (o si cambia el usuario), pedimos sus datos al servidor.
    LaunchedEffect(id) { recargar() }

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
        // when: muestra carga, error, sin datos o detalle con botones editar/activar.
        when {
            loading -> LoadingState(modifier = Modifier.weight(1f))           // spinner
            error != null -> ErrorState(error!!, modifier = Modifier.weight(1f))  // error
            usuario == null -> ErrorState("Sin datos", modifier = Modifier.weight(1f))  // vacío
            else -> {
                DetalleCamposList(usuario!!.camposDetalle(), modifier = Modifier.weight(1f))
                // Botón editar: navega al formulario de edición del usuario.
                Button(
                    onClick = onEdit,
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Text("Editar")
                }
                // Botón activar/desactivar: cambia el estado del usuario en el servidor.
                Button(
                    onClick = {
                        scope.launch {
                            toggling = true
                            repository.cambiarEstado(id).fold(
                                onSuccess = { recargar() },  // callback: vuelve a pedir los datos
                                onFailure = { e -> error = e.message },
                            )
                            toggling = false
                        }
                    },
                    enabled = !toggling,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Text(if (usuario?.activo == true) "Desactivar usuario" else "Activar usuario")
                }
            }
        }
    }
}
