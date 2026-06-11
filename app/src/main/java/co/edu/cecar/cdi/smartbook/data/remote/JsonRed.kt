package co.edu.cecar.cdi.smartbook.data.remote

// =============================================================================
// ARCHIVO: JsonRed.kt
// Define cómo la app lee y escribe JSON en las peticiones de red.
// Ktor usa este objeto para convertir datos Kotlin ↔ JSON automáticamente.
// También lo usan las funciones de ExtensionesClienteHttp.kt.
// =============================================================================

import kotlinx.serialization.json.Json // Librería para parsear y generar JSON

// Objeto Json compartido por toda la app, con reglas flexibles para APIs reales
val networkJson = Json {
    ignoreUnknownKeys = true // Ignora campos del JSON que la clase no tiene
    isLenient = true // Acepta JSON un poco "suelto" (comas extra, etc.)
    explicitNulls = false // No envía campos null al servidor
    coerceInputValues = true // Convierte tipos cuando puede (ej. "1" → 1)
}
