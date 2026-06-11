package co.edu.cecar.cdi.smartbook.data.repository

// =============================================================================
// ARCHIVO: RepositorioAutenticacion.kt
// Repositorio de login y sesión del usuario.
// Habla con el API de Seguridad: login, restablecer contraseña y logout.
// Guarda el JWT en AlmacenTokenSeguro después de un login exitoso.
// Usa el cliente HTTP de ProveedorClienteHttp.
// =============================================================================

import co.edu.cecar.cdi.smartbook.data.local.SecureTokenStore
import co.edu.cecar.cdi.smartbook.data.model.LoginRequestDto
import co.edu.cecar.cdi.smartbook.data.model.RespuestaLoginDto
import co.edu.cecar.cdi.smartbook.data.model.RestablecerContrasenaDto
import co.edu.cecar.cdi.smartbook.data.model.SolicitarRestablecimientoDto
import co.edu.cecar.cdi.smartbook.data.model.extraerToken
import co.edu.cecar.cdi.smartbook.data.remote.HttpClientProvider
import co.edu.cecar.cdi.smartbook.data.remote.networkJson
import co.edu.cecar.cdi.smartbook.data.remote.setJsonBody
import co.edu.cecar.cdi.smartbook.data.remote.verificarExitoHttp
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.serializer

// Acceso a los endpoints (rutas) de Seguridad del API
class AuthRepository(
    private val tokenStore: SecureTokenStore, // Donde guardamos el JWT
) {
    private val client = HttpClientProvider.client // Cliente HTTP compartido

    // Inicia sesión con correo y contraseña; guarda el JWT si el servidor responde bien
    suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        val response = client.post("Seguridad/iniciar-sesion") {
            setJsonBody(LoginRequestDto(email = email, password = password)) // Envía credenciales
        }.also { it.verificarExitoHttp() } // Lanza error si el código HTTP no es exitoso

        val raw = response.bodyAsText().trim() // Respuesta cruda del servidor
        // El API puede devolver el token como texto plano (eyJ...) o dentro de un JSON
        val token = if (raw.startsWith("eyJ") && raw.length > 20) {
            raw // Token JWT directo en el cuerpo
        } else {
            networkJson.decodeFromString(serializer<RespuestaLoginDto>(), raw).extraerToken()
        } ?: error("Respuesta sin token JWT.") // Sin token no hay sesión

        tokenStore.saveToken(token) // Guarda el JWT cifrado en el celular
    }

    // Manda al correo un código para restablecer la contraseña
    suspend fun solicitarRestablecimiento(correo: String): Result<Unit> = runCatching {
        client.post("Seguridad/solicitar-restablecimiento") {
            setJsonBody(SolicitarRestablecimientoDto(email = correo))
        }.also { it.verificarExitoHttp() }
    }

    // Cambia la contraseña con el código que llegó al correo
    @Suppress("UNUSED_PARAMETER") // correo reservado para validación futura
    suspend fun restablecerContrasena(correo: String, codigo: String, nueva: String): Result<Unit> =
        runCatching {
            client.post("Seguridad/restablecer-contrasena") {
                setJsonBody(RestablecerContrasenaDto(codigo = codigo, newPassword = nueva))
            }.also { it.verificarExitoHttp() }
        }

    // Cierra sesión borrando el token del celular (no avisa al servidor)
    fun logout() {
        tokenStore.clear()
    }
}
