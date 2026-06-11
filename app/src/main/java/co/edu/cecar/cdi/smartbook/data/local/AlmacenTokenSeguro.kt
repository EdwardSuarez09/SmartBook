package co.edu.cecar.cdi.smartbook.data.local

// =============================================================================
// ARCHIVO: AlmacenTokenSeguro.kt
// Guarda el token JWT (pase de sesión) de forma segura en el celular.
// Usa EncryptedSharedPreferences para que el token no quede en texto plano.
// Lo usan ContenedorAplicacion y RepositorioAutenticacion.
// =============================================================================

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences // Preferencias cifradas
import androidx.security.crypto.MasterKey // Clave maestra para cifrar/descifrar

// Guarda, lee y borra el token de sesión del usuario
class SecureTokenStore(context: Context) {

    // Archivo de preferencias cifradas donde guardamos el JWT
    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context, // Contexto de la app
        PREFS_NAME, // Nombre del archivo de preferencias
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(), // Clave AES
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, // Cifrado de claves
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM, // Cifrado de valores
    )

    // Devuelve el token guardado, o null si no hay sesión
    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)?.takeIf { it.isNotBlank() }

    // Guarda el JWT después de un login exitoso
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply() // apply() guarda en segundo plano
    }

    // Borra el token al cerrar sesión o si la sesión ya no sirve
    fun clear() {
        prefs.edit().clear().apply() // Vacía todas las preferencias cifradas
    }

    // True si hay un token guardado y no está vacío
    fun hasToken(): Boolean = !getToken().isNullOrBlank()

    // Constantes internas: nombre del archivo y clave del token
    companion object {
        private const val PREFS_NAME = "smartbook_secure_prefs" // Nombre del archivo
        private const val KEY_TOKEN = "jwt_token" // Clave donde se guarda el JWT
    }
}
