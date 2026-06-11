package co.edu.cecar.cdi.smartbook.ui.common

// =============================================================================
// ARCHIVO: DialogoDetalle.kt
// Ventana emergente para ver detalles sin cambiar de pantalla.
// También convierte errores técnicos en mensajes claros para el usuario.
// =============================================================================

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DialogoDetalle(
    titulo: String,
    campos: List<Pair<String, String>>, // Pares etiqueta → valor
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss, // Cerrar al pulsar fuera
        title = { Text(titulo) },
        text = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()), // Scroll si hay muchos campos
            ) {
                campos.forEach { (etiqueta, valor) ->
                    Text(
                        etiqueta,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    Text(valor, style = MaterialTheme.typography.bodyLarge)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        },
    )
}

// Traduce mensajes de error crudos a texto que el usuario entienda.
fun mensajeErrorAmigable(mensaje: String?): String {
    if (mensaje.isNullOrBlank()) return "Ocurrió un error inesperado."
    return when {
        mensaje.contains("Unexpected JSON", ignoreCase = true) ->
            "No se pudieron leer los datos del servidor. El formato no coincide con lo esperado."
        mensaje.contains("Unable to resolve host", ignoreCase = true) ->
            "Sin conexión a internet. Revisa tu red e intenta de nuevo."
        mensaje.length > 200 -> mensaje.take(200) + "…" // Evita textos demasiado largos
        else -> mensaje
    }
}
