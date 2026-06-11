package co.edu.cecar.cdi.smartbook.ui.common

// =============================================================================
// ARCHIVO: ComponentesJson.kt
// Piezas reutilizables para listas y detalles que usan JSON del servidor.
// Incluye ayudas para leer campos, estados de carga/error/vacío y listas.
// Muchas pantallas reciben JsonObject o JsonArray y las muestran aquí.
// =============================================================================

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

// --- Lectura de campos del JSON ---

// Busca el identificador probando nombres comunes del API.
fun JsonObject.pickId(): String? {
    val keys = listOf("identificacion", "Identificacion", "id", "Id", "codigo", "Codigo")
    for (k in keys) {
        this[k]?.jsonPrimitive?.contentOrNull?.let { if (it.isNotBlank()) return it }
    }
    return null
}

// Elige un texto para el título de la fila (nombre, título, correo…).
fun JsonObject.pickTitle(): String {
    val keys = listOf("nombre", "Nombre", "titulo", "Titulo", "descripcion", "Descripcion", "correo", "Correo")
    for (k in keys) {
        this[k]?.jsonPrimitive?.contentOrNull?.let { if (it.isNotBlank()) return it }
    }
    // Si no hay campo conocido, usa el primer valor primitivo o "—".
    return entries.firstOrNull { it.value is JsonPrimitive }?.value?.jsonPrimitive?.contentOrNull ?: "—"
}

// --- Pantallas de espera, error o vacío ---

@Composable
fun LoadingState(message: String = "Cargando…", modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Composable
fun ErrorState(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            Icons.Default.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.padding(bottom = 8.dp),
            tint = MaterialTheme.colorScheme.error,
        )
        Text(
            message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun EmptyListState(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            Icons.Default.Inbox,
            contentDescription = null,
            modifier = Modifier.padding(bottom = 8.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

// --- Listas ---

@Composable
fun JsonArrayList(
    data: JsonArray,
    onItemClick: (JsonObject) -> Unit,
    modifier: Modifier = Modifier,
    emptyMessage: String = "No hay elementos para mostrar.",
) {
    val list = data.mapNotNull { it as? JsonObject } // Solo objetos JSON válidos
    if (list.isEmpty()) {
        EmptyListState(emptyMessage, modifier = modifier.fillMaxSize())
        return
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(list, key = { it.pickId() ?: it.hashCode().toString() }) { row ->
            val id = row.pickId()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(row) },
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                ListItem(
                    headlineContent = {
                        Text(row.pickTitle(), style = MaterialTheme.typography.titleSmall)
                    },
                    supportingContent = {
                        if (id != null) {
                            Text(
                                "ID: $id",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                    trailingContent = {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
                )
            }
        }
    }
}

@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        singleLine = true,
        shape = MaterialTheme.shapes.large,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        ),
    )
}

@Composable
fun DetalleCamposList(
    campos: List<Pair<String, String>>,
    modifier: Modifier = Modifier,
    vacio: String = "Sin datos para mostrar.",
) {
    if (campos.isEmpty()) {
        EmptyListState(vacio, modifier = modifier.fillMaxSize())
        return
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(campos, key = { it.first }) { (k, v) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                ListItem(
                    headlineContent = {
                        Text(
                            k,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    supportingContent = {
                        Text(v, style = MaterialTheme.typography.bodyLarge)
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                )
            }
        }
    }
}

@Composable
fun <T> ListaDto(
    items: List<T>,
    titulo: (T) -> String,
    subtitulo: (T) -> String?,
    id: (T) -> String?,
    onItemClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    emptyMessage: String = "No hay elementos para mostrar.",
) {
    if (items.isEmpty()) {
        EmptyListState(emptyMessage, modifier = modifier.fillMaxSize())
        return
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items, key = { id(it) ?: it.hashCode().toString() }) { row ->
            val itemId = id(row)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(row) },
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                ListItem(
                    headlineContent = {
                        Text(titulo(row), style = MaterialTheme.typography.titleSmall)
                    },
                    supportingContent = {
                        subtitulo(row)?.let {
                            Text(
                                it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                    trailingContent = {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
                )
            }
        }
    }
}

// Muestra cada clave del JsonObject como tarjeta nombre-valor.
@Composable
fun JsonKeyValueList(obj: JsonObject, modifier: Modifier = Modifier) {
    val entriesList = obj.entries.toList()
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(entriesList, key = { it.key }) { (k, v) ->
            val text = when (v) {
                is JsonPrimitive -> v.contentOrNull ?: v.toString()
                is JsonObject -> v.toString()
                else -> v.toString()
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                ListItem(
                    headlineContent = {
                        Text(
                            k,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    supportingContent = {
                        Text(text, style = MaterialTheme.typography.bodyLarge)
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                )
            }
        }
    }
}
