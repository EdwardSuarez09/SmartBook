package co.edu.cecar.cdi.smartbook.ui.screens.mas

// ARCHIVO: MasInicioScreen.kt
// Menú "Más" con enlaces a lotes, ingresos, inventarios, usuarios y perfil.

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import co.edu.cecar.cdi.smartbook.navigation.MainRoute

@Composable
fun MasInicioScreen(
    onNavigate: (MainRoute) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Opciones fijas: nombre, icono y pantalla a la que llevan.
    val opciones = listOf(
        Triple("Lotes", Icons.Default.LocalShipping, MainRoute.LotesList),
        Triple("Ingresos", Icons.Default.Inventory2, MainRoute.IngresosList),
        Triple("Inventarios", Icons.Default.Warehouse, MainRoute.Inventarios),
        Triple("Usuarios", Icons.Default.People, MainRoute.UsuariosList),
        Triple("Mi perfil", Icons.Default.Person, MainRoute.Perfil),
    )

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text("Más opciones", style = MaterialTheme.typography.titleLarge)
        Text(
            "Accesos del menú lateral según la especificación técnica.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        opciones.forEach { (titulo, icono, ruta) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    // Callback: al tocar la tarjeta, navega a la pantalla correspondiente.
                    .clickable { onNavigate(ruta) },
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                ListItem(
                    headlineContent = { Text(titulo) },
                    leadingContent = {
                        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                )
            }
        }
    }
}
