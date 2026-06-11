package co.edu.cecar.cdi.smartbook.ui.main

// =============================================================================
// ARCHIVO: MarcoPrincipal.kt
// Marco visual de la app con sesión abierta.
// MainScaffold: barra superior roja, cinco pestañas inferiores y botón Salir.
// MainShellWithDrawer: añade menú lateral con Lotes, Ingresos, Inventarios, etc.
// =============================================================================

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.edu.cecar.cdi.smartbook.navigation.MainRoute
import co.edu.cecar.cdi.smartbook.ui.common.SalirTextButton
import co.edu.cecar.cdi.smartbook.ui.common.SmartBookTopAppBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    title: String,
    userName: String? = null,
    selectedTab: Int, // 0=Inicio … 4=Más
    onTabSelected: (Int) -> Unit,
    onOpenDrawer: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit, // Pantalla actual con padding de barras
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SmartBookTopAppBar(
                title = title,
                subtitle = userName,
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                },
                actions = { SalirTextButton(onClick = onLogout) },
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
            ) {
                // Colores compartidos por las cinco pestañas.
                val colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { onTabSelected(0) },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Inicio") },
                    label = { Text("Inicio") },
                    colors = colors,
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { onTabSelected(1) },
                    icon = { Icon(Icons.Default.People, contentDescription = "Clientes") },
                    label = { Text("Clientes") },
                    colors = colors,
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { onTabSelected(2) },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = "Libros") },
                    label = { Text("Libros") },
                    colors = colors,
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { onTabSelected(3) },
                    icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Ventas") },
                    label = { Text("Ventas") },
                    colors = colors,
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { onTabSelected(4) },
                    icon = { Icon(Icons.Default.MoreHoriz, contentDescription = "Más") },
                    label = { Text("Más") },
                    colors = colors,
                )
            }
        },
    ) { padding ->
        content(Modifier.padding(padding)) // Respeta espacio de barra y pestañas
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainShellWithDrawer(
    title: String,
    userName: String? = null,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onNavigateDrawer: (MainRoute) -> Unit, // Navega al elegir opción del menú
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope() // Para abrir/cerrar el cajón con corrutinas

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Cabecera del menú lateral con marca SmartBook.
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                ) {
                    Text("SmartBook", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onPrimary)
                    Text("CDI · CECAR", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f))
                }
                Text(
                    "Más opciones",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                )
                // Enlaces del menú lateral a rutas secundarias.
                val items = listOf(
                    "Lotes" to MainRoute.LotesList,
                    "Ingresos" to MainRoute.IngresosList,
                    "Inventarios" to MainRoute.Inventarios,
                    "Usuarios" to MainRoute.UsuariosList,
                    "Mi perfil" to MainRoute.Perfil,
                )
                val drawerColors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = MaterialTheme.colorScheme.surface,
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                )
                items.forEach { (label, route) ->
                    NavigationDrawerItem(
                        label = { Text(label) },
                        selected = false,
                        colors = drawerColors,
                        onClick = {
                            scope.launch { drawerState.close() } // Cierra el menú al elegir
                            onNavigateDrawer(route)
                        },
                    )
                }
            }
        },
        modifier = modifier,
    ) {
        MainScaffold(
            title = title,
            userName = userName,
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            onOpenDrawer = { scope.launch { drawerState.open() } },
            onLogout = onLogout,
        ) { inner ->
            Box(Modifier.fillMaxSize()) {
                content(inner)
            }
        }
    }
}
