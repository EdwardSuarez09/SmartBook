package co.edu.cecar.cdi.smartbook.ui

// =============================================================================
// ARCHIVO: AppSmartBook.kt
// Raíz de la interfaz de SmartBook.
// Decide si mostrar login o la app principal según haya token guardado.
// Escucha errores 401 del servidor y cierra sesión automáticamente.
// Aplica el tema visual SmartBook a todo el contenido.
// =============================================================================

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import co.edu.cecar.cdi.smartbook.di.AppContainer
import co.edu.cecar.cdi.smartbook.navigation.AuthNavHost
import co.edu.cecar.cdi.smartbook.navigation.MainNavHost
import co.edu.cecar.cdi.smartbook.ui.theme.SmartBookTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SmartBookApp(
    container: AppContainer, // Herramientas compartidas (datos, token, avisos)
    modifier: Modifier = Modifier,
) {
    // Estado de sesión; se conserva al rotar el teléfono.
    var session by rememberSaveable { mutableStateOf(container.tokenStore.hasToken()) }

    // Si el servidor responde 401, cerramos sesión en pantalla.
    LaunchedEffect(container) {
        container.unauthorized.collectLatest {
            session = false
        }
    }

    SmartBookTheme {
        if (session) {
            // Usuario autenticado: navegación principal con pestañas y menú.
            MainNavHost(
                container = container,
                modifier = modifier,
                onLogout = {
                    container.authRepository.logout() // Borra token en el servidor y local
                    session = false // Vuelve al flujo de login
                },
            )
        } else {
            // Sin sesión: pantallas de login y recuperar contraseña.
            AuthNavHost(
                container = container,
                modifier = modifier,
                onAuthenticated = { session = true }, // Login exitoso
            )
        }
    }
}
