package co.edu.cecar.cdi.smartbook.data.remote

// =============================================================================
// ARCHIVO: ClienteHttpSmartBook.kt
// Arma el cliente HTTP principal de SmartBook con Ktor en Android.
// Envía el token JWT, reintenta si falla la red, y maneja errores 401.
// Convierte errores del servidor en mensajes cortos para el usuario.
// Se crea una sola vez en ContenedorAplicacion.
// =============================================================================

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android // Motor HTTP nativo de Android
import io.ktor.client.plugins.HttpCallValidator // Valida respuestas (ej. 401)
import io.ktor.client.plugins.HttpRequestRetry // Reintentos automáticos
import io.ktor.client.plugins.HttpTimeout // Límites de tiempo de espera
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation // JSON ↔ Kotlin
import io.ktor.client.plugins.defaultRequest // Configuración común a cada petición
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging // Registro en Logcat
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText // Lee el cuerpo como texto
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import java.io.IOException // Errores de red (sin internet, etc.)
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

// --- Crear el cliente HTTP ---

// Crea y configura el HttpClient que usa toda la app para hablar con el servidor
fun createSmartBookHttpClient(
    tokenProvider: () -> String?, // Función que devuelve el JWT guardado (o null)
    onUnauthorized: () -> Unit, // Se ejecuta cuando el servidor responde 401
): HttpClient {
    return HttpClient(Android) { // Usa el motor Android de Ktor
        expectSuccess = false // Nosotros revisamos si la respuesta fue buena o mala

        // Cuánto esperamos antes de dar por fallida una petición
        install(HttpTimeout) {
            requestTimeoutMillis = 60_000 // 60 s para toda la petición
            connectTimeoutMillis = 30_000 // 30 s para conectar
            socketTimeoutMillis = 60_000 // 60 s esperando datos
        }

        // Reintentos automáticos si se cae internet o el servidor tarda
        install(HttpRequestRetry) {
            maxRetries = 2 // Máximo 2 reintentos
            retryOnExceptionIf { _, cause ->
                cause is IOException // Reintenta solo en errores de red
            }
            retryIf { _, response ->
                // Reintenta en timeouts o servidor no disponible
                response.status in listOf(
                    HttpStatusCode.RequestTimeout,
                    HttpStatusCode.GatewayTimeout,
                    HttpStatusCode.ServiceUnavailable,
                )
            }
        }

        // Convierte JSON ↔ objetos Kotlin automáticamente
        install(ContentNegotiation) {
            json(networkJson) // Usa la config de JsonRed.kt
        }

        // Escribe las peticiones en Logcat (útil para depurar)
        install(Logging) {
            level = LogLevel.INFO
            logger = object : Logger {
                override fun log(message: String) {
                    android.util.Log.i("SmartBookHttp", message) // Tag fijo en Logcat
                }
            }
        }

        // Si el servidor responde 401, avisamos para cerrar sesión
        install(HttpCallValidator) {
            validateResponse { response ->
                if (response.status == HttpStatusCode.Unauthorized) {
                    onUnauthorized() // Borra token y avisa a la UI
                }
            }
        }

        // Lo que va en cada petición por defecto
        defaultRequest {
            url(ApiConstants.BASE_URL) // URL base del API
            // Si hay sesión, pegamos el token; si no (login), va sin él
            tokenProvider()?.let { token ->
                headers.append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }
}

// --- Funciones privadas para mensajes de error ---

// Máximo de caracteres de un error que mostramos en pantalla
private const val MAX_USER_ERROR_LENGTH = 280

// Revisa si la respuesta es HTML (página de error), no JSON
private fun looksLikeHtml(text: String): Boolean {
    val head = text.trimStart().take(400).lowercase() // Primeros 400 caracteres
    return head.contains("<!doctype html") ||
        head.contains("<html") ||
        head.contains("<title>") ||
        head.startsWith("<html")
}

// Mensaje genérico según el código HTTP cuando el servidor no da más detalle
private fun HttpResponse.defaultUserFacingMessage(): String {
    val code = status.value // Número del código (404, 500, etc.)
    return when {
        status == HttpStatusCode.BadRequest ->
            "Los datos enviados no son válidos."
        status == HttpStatusCode.Unauthorized ->
            "Correo o contraseña incorrectos, o la sesión no es válida."
        status == HttpStatusCode.Forbidden ->
            "No tienes permiso para realizar esta acción."
        status == HttpStatusCode.NotFound ->
            "No se encontró el recurso en el servidor (404). Revisa la URL del API o que el servicio esté publicado."
        status == HttpStatusCode.RequestTimeout || status == HttpStatusCode.GatewayTimeout ->
            "Tiempo de espera agotado. Revisa tu conexión a internet."
        status == HttpStatusCode.ServiceUnavailable || status == HttpStatusCode.BadGateway ->
            "El servidor no está disponible en este momento. Intenta más tarde."
        code in 500..599 ->
            "Error en el servidor ($code). Intenta más tarde."
        else ->
            "No se pudo completar la solicitud (código $code)."
    }
}

// Busca el mensaje de error en un JSON del servidor (mensaje, detail, errors, etc.)
private fun extractJsonErrorMessage(text: String): String? {
    val trimmed = text.trim()
    if (!trimmed.startsWith("{") && !trimmed.startsWith("[")) return null // No es JSON
    val root = runCatching { networkJson.parseToJsonElement(trimmed) }.getOrNull() ?: return null
    if (root is JsonPrimitive) return root.contentOrNull?.takeIf { it.isNotBlank() }

    val obj = root as? JsonObject ?: return null
    // Claves comunes donde el servidor guarda el mensaje de error
    val directKeys = listOf(
        "mensaje", "Mensaje", "message", "Message", "detail", "Detail",
        "title", "Title", "error", "Error", "descripcion", "Descripcion",
    )
    for (k in directKeys) {
        when (val v = obj[k]) {
            is JsonPrimitive -> v.contentOrNull?.takeIf { it.isNotBlank() }?.let { return it }
            else -> Unit
        }
    }
    // Algunos APIs ponen errores dentro de un objeto "errors"
    obj["errors"]?.jsonObject?.values?.firstOrNull()?.let { v ->
        when (v) {
            is JsonArray -> v.firstOrNull()?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }?.let { return it }
            is JsonPrimitive -> v.contentOrNull?.takeIf { it.isNotBlank() }?.let { return it }
            else -> Unit
        }
    }
    return null // No se encontró mensaje en el JSON
}

// Acorta el mensaje para que quepa bien en la pantalla
private fun sanitizeForUi(message: String): String {
    val oneLine = message.replace("\r\n", " ").replace("\n", " ").trim() // Una sola línea
    return if (oneLine.length <= MAX_USER_ERROR_LENGTH) oneLine
    else oneLine.take(MAX_USER_ERROR_LENGTH).trimEnd() + "…" // Corta y añade puntos suspensivos
}

// --- Funciones públicas de HttpResponse ---

// Convierte el error del servidor en un mensaje corto para el usuario
suspend fun HttpResponse.readErrorMessage(): String {
    val fallback = defaultUserFacingMessage() // Mensaje por defecto según código HTTP
    val text = try {
        bodyAsText() // Lee el cuerpo de la respuesta
    } catch (_: Exception) {
        return fallback // Si no se puede leer, usa el mensaje genérico
    }
    if (text.isBlank()) return fallback
    if (looksLikeHtml(text)) return fallback // HTML no sirve como mensaje
    extractJsonErrorMessage(text)?.let { return sanitizeForUi(it) } // Busca en JSON
    val plain = text.trim()
    // Si el texto es corto y no parece HTML, lo mostramos tal cual
    if (plain.length <= MAX_USER_ERROR_LENGTH && plain.none { it == '<' || it == '>' }) {
        return sanitizeForUi(plain)
    }
    return fallback
}

// Revisa si la petición salió bien (200, 201 o 204) o falló
suspend fun HttpResponse.toResult(): Result<Unit> {
    if (status == HttpStatusCode.OK || status == HttpStatusCode.Created || status == HttpStatusCode.NoContent) {
        return Result.success(Unit) // Respuesta exitosa
    }
    if (status == HttpStatusCode.Unauthorized) {
        return Result.failure(IllegalStateException("Sesión expirada. Inicia sesión de nuevo."))
    }
    return Result.failure(IllegalStateException(readErrorMessage())) // Error con mensaje legible
}
