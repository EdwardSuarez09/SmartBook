package co.edu.cecar.cdi.smartbook.navigation

// =============================================================================
// ARCHIVO: NavegacionAutenticacion.kt
// Navegación del flujo de login (sin sesión abierta).
// Une login, olvidé contraseña y restablecer contraseña.
// Usa una pila de pantallas; el botón atrás quita la última.
// No tiene pestañas ni menú lateral.
// =============================================================================

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import co.edu.cecar.cdi.smartbook.di.AppContainer
import co.edu.cecar.cdi.smartbook.ui.screens.login.ForgotPasswordScreen
import co.edu.cecar.cdi.smartbook.ui.screens.login.LoginScreen
import co.edu.cecar.cdi.smartbook.ui.screens.login.ResetPasswordScreen

@Composable
fun AuthNavHost(
    container: AppContainer, // Acceso al repositorio de autenticación
    onAuthenticated: () -> Unit, // El padre muestra la app principal al entrar bien
    modifier: Modifier = Modifier,
) {
    // Pila de pantallas; empieza siempre en Login.
    val backStack = rememberNavBackStack(AuthRoute.Login)
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() }, // Botón atrás del sistema
        modifier = modifier,
        entryProvider = { key ->
                when (key) {
                    is AuthRoute.Login -> NavEntry(key) {
                        LoginScreen(
                            authRepository = container.authRepository,
                            onAuthenticated = onAuthenticated,
                            onForgotPassword = { backStack.add(AuthRoute.ForgotPassword) },
                            onResetWithCode = { backStack.add(AuthRoute.ResetPassword) },
                        )
                    }

                    is AuthRoute.ForgotPassword -> NavEntry(key) {
                        ForgotPasswordScreen(
                            authRepository = container.authRepository,
                            onBack = { backStack.removeLastOrNull() },
                            onGoReset = { backStack.add(AuthRoute.ResetPassword) },
                        )
                    }

                    is AuthRoute.ResetPassword -> NavEntry(key) {
                        ResetPasswordScreen(
                            authRepository = container.authRepository,
                            onBack = { backStack.removeLastOrNull() },
                            onSuccess = {
                                // Limpia la pila y vuelve al login tras cambiar la clave.
                                while (backStack.removeLastOrNull() != null) {
                                }
                                backStack.add(AuthRoute.Login)
                            },
                        )
                    }

                    else -> error("Ruta de autenticación desconocida: $key")
                }
            },
    )
}
