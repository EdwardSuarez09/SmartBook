package co.edu.cecar.cdi.smartbook.ui.common

// =============================================================================
// ARCHIVO: BarraSuperiorSmartBook.kt
// Barra superior reutilizable con estilo rojo CDI.
// Muestra título, subtítulo opcional (nombre de usuario), icono izquierdo y acciones.
// Incluye el botón "Salir" para cerrar sesión.
// =============================================================================

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartBookTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null, // Ej.: nombre del usuario logueado
    navigationIcon: @Composable () -> Unit = {}, // Normalmente icono de menú
    actions: @Composable RowScope.() -> Unit = {}, // Botones a la derecha
) {
    TopAppBar(
        title = {
            if (subtitle.isNullOrBlank()) {
                // Solo título en una línea.
                Text(title, maxLines = 1, style = MaterialTheme.typography.titleLarge)
            } else {
                // Título + subtítulo en columna.
                Column {
                    Text(title, maxLines = 1, style = MaterialTheme.typography.titleMedium)
                    Text(
                        subtitle,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                    )
                }
            }
        },
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary, // Rojo CDI
            scrolledContainerColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        navigationIcon = navigationIcon,
        actions = actions,
    )
}

@Composable
fun SalirTextButton(onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onPrimary),
    ) {
        Text("Salir")
    }
}
