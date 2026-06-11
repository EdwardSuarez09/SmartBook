package co.edu.cecar.cdi.smartbook.di

// =============================================================================
// ARCHIVO: ContenedorAplicacion.kt
// Junta las piezas importantes de la app en un solo lugar (sin Hilt ni Koin).
// Crea el almacén de token, el cliente HTTP y todos los repositorios.
// Se instancia una vez en AplicacionSmartBook y la UI pide lo que necesita.
// Patrón útil en proyectos de curso antes de inyección de dependencias.
// =============================================================================

import android.app.Application
import co.edu.cecar.cdi.smartbook.data.local.SecureTokenStore
import co.edu.cecar.cdi.smartbook.data.remote.HttpClientProvider
import co.edu.cecar.cdi.smartbook.data.remote.createSmartBookHttpClient
import co.edu.cecar.cdi.smartbook.data.repository.AuthRepository
import co.edu.cecar.cdi.smartbook.data.repository.ClientesRepository
import co.edu.cecar.cdi.smartbook.data.repository.DashboardRepository
import co.edu.cecar.cdi.smartbook.data.repository.IngresosRepository
import co.edu.cecar.cdi.smartbook.data.repository.InventariosRepository
import co.edu.cecar.cdi.smartbook.data.repository.LibrosRepository
import co.edu.cecar.cdi.smartbook.data.repository.LotesRepository
import co.edu.cecar.cdi.smartbook.data.repository.UsuariosRepository
import co.edu.cecar.cdi.smartbook.data.repository.VentasRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

// Punto central para obtener servicios de red y datos
class AppContainer(private val application: Application) {

    // --- Avisos de sesión ---

    // Se dispara por dentro cuando el servidor responde 401 (no autorizado)
    private val _unauthorized = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    // La UI puede escuchar esto para mandar al usuario al login
    val unauthorized = _unauthorized.asSharedFlow()

    // --- Guardado en el celular ---

    // Guarda y lee el JWT cifrado en el dispositivo
    val tokenStore = SecureTokenStore(application)

    // --- Cliente HTTP ---

    // Cliente Ktor con token, reintentos y manejo de 401
    val httpClient = createSmartBookHttpClient(
        tokenProvider = { tokenStore.getToken() }, // Lee el JWT guardado
        onUnauthorized = {
            tokenStore.clear() // Borra sesión inválida
            _unauthorized.tryEmit(Unit) // Avisa a la UI
        },
    ).also { HttpClientProvider.init(it) } // Registra el cliente para los repositorios

    // --- Repositorios (uno por área del negocio) ---

    val authRepository = AuthRepository(tokenStore) // Login y sesión
    val dashboardRepository = DashboardRepository() // Resumen general
    val clientesRepository = ClientesRepository() // CRUD de clientes
    val librosRepository = LibrosRepository() // CRUD de libros
    val lotesRepository = LotesRepository() // Lotes de mercancía
    val inventariosRepository = InventariosRepository() // Stock por lote
    val ventasRepository = VentasRepository() // Ventas a clientes
    val usuariosRepository = UsuariosRepository() // Usuarios del sistema
    val ingresosRepository = IngresosRepository() // Entradas de mercancía
}
