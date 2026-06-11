package co.edu.cecar.cdi.smartbook.navigation

// =============================================================================
// ARCHIVO: NavegacionPrincipal.kt
// Navegación cuando el usuario ya inició sesión.
// Mantiene la pila de pantallas visitadas y muestra barra, pestañas y menú lateral.
// Conecta cada ruta (MainRoute) con su pantalla Compose correspondiente.
// =============================================================================

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import co.edu.cecar.cdi.smartbook.di.AppContainer
import co.edu.cecar.cdi.smartbook.ui.main.MainShellWithDrawer
import co.edu.cecar.cdi.smartbook.ui.screens.clientes.ClienteDetalleScreen
import co.edu.cecar.cdi.smartbook.ui.screens.clientes.ClienteFormularioScreen
import co.edu.cecar.cdi.smartbook.ui.screens.clientes.ClientesScreen
import co.edu.cecar.cdi.smartbook.ui.screens.dashboard.DashboardScreen
import co.edu.cecar.cdi.smartbook.ui.screens.ingresos.IngresoFormularioScreen
import co.edu.cecar.cdi.smartbook.ui.screens.ingresos.IngresosScreen
import co.edu.cecar.cdi.smartbook.ui.screens.inventarios.InventariosScreen
import co.edu.cecar.cdi.smartbook.ui.screens.libros.LibroDetalleScreen
import co.edu.cecar.cdi.smartbook.ui.screens.libros.LibroFormularioScreen
import co.edu.cecar.cdi.smartbook.ui.screens.libros.LibrosScreen
import co.edu.cecar.cdi.smartbook.ui.screens.lotes.LoteFormularioScreen
import co.edu.cecar.cdi.smartbook.ui.screens.lotes.LotesScreen
import co.edu.cecar.cdi.smartbook.ui.screens.mas.MasInicioScreen
import co.edu.cecar.cdi.smartbook.ui.screens.perfil.PerfilScreen
import co.edu.cecar.cdi.smartbook.ui.screens.usuarios.UsuarioDetalleScreen
import co.edu.cecar.cdi.smartbook.ui.screens.usuarios.UsuarioFormularioScreen
import co.edu.cecar.cdi.smartbook.ui.screens.usuarios.UsuariosScreen
import co.edu.cecar.cdi.smartbook.ui.screens.ventas.VentaDetalleScreen
import co.edu.cecar.cdi.smartbook.ui.screens.ventas.VentaFormularioScreen
import co.edu.cecar.cdi.smartbook.ui.screens.ventas.VentasScreen

@Composable
fun MainNavHost(
    container: AppContainer,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Historial de pantallas; la primera es el Dashboard.
    val backStack = rememberNavBackStack(MainRoute.Dashboard)
    var userName by remember { mutableStateOf<String?>(null) } // Nombre en la barra superior

    // Carga el perfil del usuario al abrir la navegación principal.
    LaunchedEffect(Unit) {
        container.usuariosRepository.perfil().getOrNull()?.let { perfil ->
            userName = perfil.nombres?.takeIf { it.isNotBlank() } ?: perfil.email
        }
    }

    // Vacía la pila y deja solo la ruta raíz de una pestaña o del menú.
    fun goRoot(route: MainRoute) {
        while (backStack.removeLastOrNull() != null) {
        }
        backStack.add(route)
    }

    // Pantalla visible = la última de la pila.
    val current by remember {
        derivedStateOf {
            (backStack.lastOrNull() as? MainRoute) ?: MainRoute.Dashboard
        }
    }
    val selectedTab = current.bottomTabIndex() // Pestaña inferior resaltada

    // Marco con barra arriba, pestañas abajo y cajón lateral.
    MainShellWithDrawer(
        title = current.mainTitle(),
        userName = userName,
        selectedTab = selectedTab,
        onTabSelected = { index ->
            when (index) {
                0 -> goRoot(MainRoute.Dashboard)
                1 -> goRoot(MainRoute.ClientesList)
                2 -> goRoot(MainRoute.LibrosList)
                3 -> goRoot(MainRoute.VentasList)
                4 -> goRoot(MainRoute.MasInicio)
                else -> goRoot(MainRoute.Dashboard)
            }
        },
        onNavigateDrawer = { route -> goRoot(route) }, // Opción del menú lateral
        onLogout = onLogout,
        modifier = modifier,
    ) { paddingModifier ->
        // Muestra la pantalla que corresponde a la ruta del tope de la pila.
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            modifier = paddingModifier,
            entryProvider = { key ->
                when (key) {
                    // --- Inicio ---
                    is MainRoute.Dashboard -> NavEntry(key) {
                        DashboardScreen(
                            repository = container.dashboardRepository,
                            onAccionRapida = { route -> backStack.add(route) },
                        )
                    }

                    // --- Clientes ---
                    is MainRoute.ClientesList -> NavEntry(key) {
                        ClientesScreen(
                            repository = container.clientesRepository,
                            onOpen = { id -> backStack.add(MainRoute.ClienteDetail(id)) },
                            onCreate = { backStack.add(MainRoute.ClienteCreate) },
                        )
                    }

                    is MainRoute.ClienteDetail -> NavEntry(key) {
                        ClienteDetalleScreen(
                            repository = container.clientesRepository,
                            identificacion = key.identificacion,
                            onEdit = {
                                backStack.removeLastOrNull()
                                backStack.add(MainRoute.ClienteEdit(key.identificacion))
                            },
                        )
                    }

                    is MainRoute.ClienteCreate -> NavEntry(key) {
                        ClienteFormularioScreen(
                            repository = container.clientesRepository,
                            identificacionInicial = null,
                            onSaved = { backStack.removeLastOrNull() },
                        )
                    }

                    is MainRoute.ClienteEdit -> NavEntry(key) {
                        ClienteFormularioScreen(
                            repository = container.clientesRepository,
                            identificacionInicial = key.identificacion,
                            onSaved = { backStack.removeLastOrNull() },
                        )
                    }

                    // --- Libros ---
                    is MainRoute.LibrosList -> NavEntry(key) {
                        LibrosScreen(
                            repository = container.librosRepository,
                            onOpen = { id -> backStack.add(MainRoute.LibroDetail(id)) },
                            onCreate = { backStack.add(MainRoute.LibroCreate) },
                        )
                    }

                    is MainRoute.LibroDetail -> NavEntry(key) {
                        LibroDetalleScreen(
                            repository = container.librosRepository,
                            id = key.id,
                            onEdit = {
                                backStack.removeLastOrNull()
                                backStack.add(MainRoute.LibroEdit(key.id))
                            },
                        )
                    }

                    is MainRoute.LibroCreate -> NavEntry(key) {
                        LibroFormularioScreen(
                            repository = container.librosRepository,
                            idInicial = null,
                            onSaved = { backStack.removeLastOrNull() },
                        )
                    }

                    is MainRoute.LibroEdit -> NavEntry(key) {
                        LibroFormularioScreen(
                            repository = container.librosRepository,
                            idInicial = key.id,
                            onSaved = { backStack.removeLastOrNull() },
                        )
                    }

                    // --- Ventas ---
                    is MainRoute.VentasList -> NavEntry(key) {
                        VentasScreen(
                            repository = container.ventasRepository,
                            onOpen = { id -> backStack.add(MainRoute.VentaDetail(id)) },
                            onCreate = { backStack.add(MainRoute.VentaCreate) },
                        )
                    }

                    is MainRoute.VentaDetail -> NavEntry(key) {
                        VentaDetalleScreen(repository = container.ventasRepository, id = key.id)
                    }

                    is MainRoute.VentaCreate -> NavEntry(key) {
                        VentaFormularioScreen(
                            repository = container.ventasRepository,
                            onSaved = { backStack.removeLastOrNull() },
                        )
                    }

                    // --- Más y pantallas del menú lateral ---
                    is MainRoute.MasInicio -> NavEntry(key) {
                        MasInicioScreen(onNavigate = { route -> goRoot(route) })
                    }

                    is MainRoute.LotesList -> NavEntry(key) {
                        LotesScreen(
                            repository = container.lotesRepository,
                            onCreate = { backStack.add(MainRoute.LoteCreate) },
                            onVerInventario = { lote ->
                                backStack.add(MainRoute.InventariosLote(lote))
                            },
                        )
                    }

                    is MainRoute.LoteCreate -> NavEntry(key) {
                        LoteFormularioScreen(
                            repository = container.lotesRepository,
                            onSaved = { backStack.removeLastOrNull() },
                        )
                    }

                    is MainRoute.IngresosList -> NavEntry(key) {
                        IngresosScreen(
                            repository = container.ingresosRepository,
                            onCreate = { backStack.add(MainRoute.IngresoCreate) },
                        )
                    }

                    is MainRoute.IngresoCreate -> NavEntry(key) {
                        IngresoFormularioScreen(
                            repository = container.ingresosRepository,
                            onSaved = { backStack.removeLastOrNull() },
                        )
                    }

                    is MainRoute.Inventarios -> NavEntry(key) {
                        InventariosScreen(repository = container.inventariosRepository)
                    }

                    is MainRoute.InventariosLote -> NavEntry(key) {
                        InventariosScreen(
                            repository = container.inventariosRepository,
                            loteFiltro = key.lote,
                        )
                    }

                    // --- Usuarios y perfil ---
                    is MainRoute.UsuariosList -> NavEntry(key) {
                        UsuariosScreen(
                            repository = container.usuariosRepository,
                            onOpen = { id -> backStack.add(MainRoute.UsuarioDetail(id)) },
                            onCreate = { backStack.add(MainRoute.UsuarioCreate) },
                        )
                    }

                    is MainRoute.UsuarioDetail -> NavEntry(key) {
                        UsuarioDetalleScreen(
                            repository = container.usuariosRepository,
                            id = key.id,
                            onEdit = {
                                backStack.removeLastOrNull()
                                backStack.add(MainRoute.UsuarioEdit(key.id))
                            },
                        )
                    }

                    is MainRoute.UsuarioCreate -> NavEntry(key) {
                        UsuarioFormularioScreen(
                            repository = container.usuariosRepository,
                            idInicial = null,
                            onSaved = { backStack.removeLastOrNull() },
                        )
                    }

                    is MainRoute.UsuarioEdit -> NavEntry(key) {
                        UsuarioFormularioScreen(
                            repository = container.usuariosRepository,
                            idInicial = key.id,
                            onSaved = { backStack.removeLastOrNull() },
                        )
                    }

                    is MainRoute.Perfil -> NavEntry(key) {
                        PerfilScreen(repository = container.usuariosRepository)
                    }

                    else -> NavEntry(key) {
                        Text("Ruta no soportada: $key")
                    }
                }
            },
        )
    }
}

// Título que aparece en la barra superior según la pantalla actual.
private fun MainRoute.mainTitle(): String = when (this) {
    is MainRoute.Dashboard -> "SmartBook CDI"
    is MainRoute.ClientesList -> "Clientes"
    is MainRoute.ClienteDetail -> "Cliente"
    is MainRoute.ClienteCreate -> "Nuevo cliente"
    is MainRoute.ClienteEdit -> "Editar cliente"
    is MainRoute.LibrosList -> "Libros"
    is MainRoute.LibroDetail -> "Libro"
    is MainRoute.LibroCreate -> "Nuevo libro"
    is MainRoute.LibroEdit -> "Editar libro"
    is MainRoute.VentasList -> "Ventas"
    is MainRoute.VentaDetail -> "Venta"
    is MainRoute.VentaCreate -> "Nueva venta"
    is MainRoute.MasInicio -> "Más"
    is MainRoute.LotesList -> "Lotes"
    is MainRoute.LoteCreate -> "Nuevo lote"
    is MainRoute.IngresosList -> "Ingresos"
    is MainRoute.IngresoCreate -> "Nuevo ingreso"
    is MainRoute.Inventarios -> "Inventarios"
    is MainRoute.InventariosLote -> "Inventario lote ${lote}"
    is MainRoute.UsuariosList -> "Usuarios"
    is MainRoute.UsuarioDetail -> "Usuario"
    is MainRoute.UsuarioCreate -> "Nuevo usuario"
    is MainRoute.UsuarioEdit -> "Editar usuario"
    is MainRoute.Perfil -> "Mi perfil"
}
