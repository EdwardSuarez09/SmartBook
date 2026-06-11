package co.edu.cecar.cdi.smartbook.data.remote

// =============================================================================
// ARCHIVO: ConstantesApi.kt
// Guarda la dirección base del servidor (API) de SmartBooks.
// Si cambia la URL del backend, solo se edita aquí.
// Apunta al API, no a la página web pública.
// =============================================================================

// Valores fijos usados en todas las peticiones HTTP de la app
object ApiConstants {

    // Dirección base del API; a esto se le pegan rutas como Clientes, Libros, etc.
    const val BASE_URL = "https://api.smartbooks.cecar.cloud/api/"
}
