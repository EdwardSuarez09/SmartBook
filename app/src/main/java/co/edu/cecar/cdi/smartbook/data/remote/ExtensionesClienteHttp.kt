package co.edu.cecar.cdi.smartbook.data.remote

// =============================================================================
// ARCHIVO: ExtensionesClienteHttp.kt
// ¿QUÉ HACE EN GENERAL?
// Funciones auxiliares para hablar con el servidor en JSON.
// - Al ENVIAR (POST/PUT): convierte objetos Kotlin → texto JSON.
// - Al RECIBIR (GET): convierte texto JSON → objetos Kotlin.
// En Android no usamos setBody(DTO) directo; lo hacemos manual aquí.
// =============================================================================

import io.ktor.client.request.HttpRequestBuilder // Constructor de peticiones HTTP
import io.ktor.client.request.setBody             // Pone el cuerpo de la petición
import io.ktor.client.statement.HttpResponse      // Respuesta del servidor
import io.ktor.client.statement.bodyAsText        // Lee la respuesta como texto
import io.ktor.http.ContentType                   // Tipo de contenido (JSON, etc.)
import io.ktor.http.contentType                   // Asigna Content-Type a la petición
import io.ktor.http.isSuccess                     // true si el código HTTP es 2xx
import kotlinx.serialization.builtins.ListSerializer // Serializador para listas
import kotlinx.serialization.serializer          // Obtiene serializador del tipo T

// Comprueba que el servidor respondió bien (200, 201...). Si no, lanza error.
suspend fun HttpResponse.verificarExitoHttp() {
    if (!status.isSuccess()) {           // ¿El código HTTP NO es de éxito?
        error(readErrorMessage())        // Lanza excepción con mensaje del servidor
    }
}

// Convierte un objeto Kotlin a JSON y lo pone como cuerpo de la petición (POST/PUT).
inline fun <reified T> HttpRequestBuilder.setJsonBody(value: T) {
    contentType(ContentType.Application.Json)                          // Avisamos que enviamos JSON
    setBody(networkJson.encodeToString(serializer<T>(), value))        // Objeto → texto JSON → cuerpo
}

// Lee la respuesta del servidor y la convierte en UN objeto Kotlin (ej. LibroDto).
suspend inline fun <reified T> HttpResponse.bodyJson(): T {
    val text = bodyAsText()                                            // Respuesta como String
    return networkJson.decodeFromString(serializer<T>(), text)         // JSON → objeto Kotlin
}

// Igual que bodyJson, pero la respuesta es una LISTA (ej. List<LibroDto>).
suspend inline fun <reified T> HttpResponse.bodyJsonList(): List<T> {
    val text = bodyAsText()                                            // Respuesta como String
    return networkJson.decodeFromString(ListSerializer(serializer<T>()), text) // JSON array → lista
}
