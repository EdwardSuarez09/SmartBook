package co.edu.cecar.cdi.smartbook.navigation

// =============================================================================
// ARCHIVO: Rutas.kt
// Define los nombres de todas las pantallas de SmartBook.
// Cada ruta es una clave que la navegación puede guardar y restaurar.
// Hay dos grupos: rutas de autenticación (sin login) y rutas principales.
// También indica qué pestaña inferior debe verse activa.
// =============================================================================

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// --- Pantallas sin iniciar sesión ---

@Serializable // Permite guardar la ruta al rotar o ir atrás
sealed interface AuthRoute : NavKey {
    @Serializable
    data object Login : AuthRoute // Pantalla de inicio de sesión

    @Serializable
    data object ForgotPassword : AuthRoute // Solicitar código de recuperación

    @Serializable
    data object ResetPassword : AuthRoute // Cambiar contraseña con código
}

// --- Pantallas después del login ---

@Serializable
sealed interface MainRoute : NavKey {
    @Serializable
    data object Dashboard : MainRoute // Inicio con resumen

    @Serializable
    data object ClientesList : MainRoute // Lista de clientes

    @Serializable
    data class ClienteDetail(val identificacion: String) : MainRoute // Detalle de un cliente

    @Serializable
    data object ClienteCreate : MainRoute // Formulario nuevo cliente

    @Serializable
    data class ClienteEdit(val identificacion: String) : MainRoute // Editar cliente existente

    @Serializable
    data object LibrosList : MainRoute // Catálogo de libros

    @Serializable
    data class LibroDetail(val id: String) : MainRoute // Detalle de un libro

    @Serializable
    data object LibroCreate : MainRoute // Nuevo libro

    @Serializable
    data class LibroEdit(val id: String) : MainRoute // Editar libro

    @Serializable
    data object VentasList : MainRoute // Lista de ventas

    @Serializable
    data class VentaDetail(val id: String) : MainRoute // Detalle de venta

    @Serializable
    data object VentaCreate : MainRoute // Registrar venta

    @Serializable
    data object MasInicio : MainRoute // Pantalla "Más" (pestaña 4)

    @Serializable
    data object LotesList : MainRoute // Gestión de lotes

    @Serializable
    data object LoteCreate : MainRoute // Crear lote

    @Serializable
    data object IngresosList : MainRoute // Lista de ingresos

    @Serializable
    data object IngresoCreate : MainRoute // Nuevo ingreso

    @Serializable
    data object Inventarios : MainRoute // Inventario general

    @Serializable
    data class InventariosLote(val lote: Int) : MainRoute // Inventario filtrado por lote

    @Serializable
    data object UsuariosList : MainRoute // Lista de usuarios

    @Serializable
    data class UsuarioDetail(val id: String) : MainRoute // Detalle de usuario

    @Serializable
    data object UsuarioCreate : MainRoute // Nuevo usuario

    @Serializable
    data class UsuarioEdit(val id: String) : MainRoute // Editar usuario

    @Serializable
    data object Perfil : MainRoute // Perfil del usuario actual
}

// --- Ayuda para la barra inferior ---

// Devuelve el índice de pestaña (0=Inicio … 4=Más) según la ruta actual.
fun MainRoute.bottomTabIndex(): Int = when (this) {
    is MainRoute.Dashboard -> 0
    is MainRoute.ClientesList,
    is MainRoute.ClienteDetail,
    is MainRoute.ClienteCreate,
    is MainRoute.ClienteEdit,
    -> 1 // Todo el módulo clientes marca la pestaña Clientes
    is MainRoute.LibrosList,
    is MainRoute.LibroDetail,
    is MainRoute.LibroCreate,
    is MainRoute.LibroEdit,
    -> 2
    is MainRoute.VentasList,
    is MainRoute.VentaDetail,
    is MainRoute.VentaCreate,
    -> 3
    else -> 4 // Lotes, ingresos, inventarios, usuarios, perfil → pestaña Más
}
