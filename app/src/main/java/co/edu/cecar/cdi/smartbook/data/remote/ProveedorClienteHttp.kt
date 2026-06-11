package co.edu.cecar.cdi.smartbook.data.remote

// =============================================================================
// ARCHIVO: ProveedorClienteHttp.kt
// Guarda el cliente HTTP (conexión con el servidor) en un solo lugar.
// La app crea un cliente al iniciar y todos los repositorios lo reutilizan.
// ContenedorAplicacion lo inicializa; los repos lo leen desde aquí.
// =============================================================================

import io.ktor.client.HttpClient // Cliente de red de Ktor

// Objeto único que almacena el HttpClient de SmartBook
object HttpClientProvider {

    // Cliente listo para usar; solo se asigna una vez en init()
    lateinit var client: HttpClient
        private set // Nadie fuera de este objeto puede cambiar el cliente

    // Guarda el cliente cuando la app arranca (antes de cualquier petición)
    fun init(httpClient: HttpClient) {
        client = httpClient // Asigna el cliente creado en ContenedorAplicacion
    }
}
