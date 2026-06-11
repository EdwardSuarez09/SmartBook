package co.edu.cecar.cdi.smartbook.ui.screens.login

// ARCHIVO: LoginViewModel.kt
// ViewModel del login: valida el formulario y llama al servidor para autenticar.

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import co.edu.cecar.cdi.smartbook.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,   // true = estamos esperando respuesta del servidor
    val error: String? = null,      // mensaje de error para el usuario; null = todo bien
)

class LoginViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    // Estado interno. Solo el ViewModel puede cambiarlo.
    private val _ui = MutableStateFlow(LoginUiState())
    // Lo que la pantalla puede leer (solo lectura).
    val ui: StateFlow<LoginUiState> = _ui.asStateFlow()

    /** Cuando escribe el correo, lo guardamos y borramos errores viejos. */
    fun setEmail(v: String) = _ui.update { it.copy(email = v, error = null) }

    /** Cuando escribe la contraseña, la guardamos y borramos errores viejos. */
    fun setPassword(v: String) = _ui.update { it.copy(password = v, error = null) }

    /**
     * Intenta iniciar sesión en el servidor.
     * @param onSuccess Si sale bien, la pantalla usa esto para ir al dashboard.
     */
    fun login(onSuccess: () -> Unit) {
        val email = _ui.value.email.trim()
        val password = _ui.value.password
        // Primero revisamos en el teléfono que no falten datos.
        if (email.isBlank() || password.isBlank()) {
            _ui.update { it.copy(error = "Completa correo y contraseña.") }
            return
        }
        // Llamamos al servidor sin bloquear la pantalla.
        viewModelScope.launch {
            _ui.update { it.copy(loading = true, error = null) }
            val result = authRepository.login(email, password)
            _ui.update { it.copy(loading = false) }
            result.fold(
                onSuccess = { onSuccess() },  // callback: login correcto, navega al dashboard
                onFailure = { e ->
                    _ui.update { it.copy(error = e.message ?: "No se pudo iniciar sesión.") }
                },
            )
        }
    }

    companion object {
        /** Crea el ViewModel y le pasa el repositorio de login. */
        fun factory(repo: AuthRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    LoginViewModel(repo) as T
            }
    }
}
