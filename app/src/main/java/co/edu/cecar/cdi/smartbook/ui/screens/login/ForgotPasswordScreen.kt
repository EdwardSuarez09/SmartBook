package co.edu.cecar.cdi.smartbook.ui.screens.login

// ARCHIVO: ForgotPasswordScreen.kt
// Pantalla para solicitar recuperación de contraseña enviando el correo al servidor.

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import co.edu.cecar.cdi.smartbook.data.repository.AuthRepository
import co.edu.cecar.cdi.smartbook.ui.common.SmartBookTopAppBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    authRepository: AuthRepository,
    onBack: () -> Unit,
    onGoReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Datos de la pantalla (esta pantalla no usa ViewModel).
    var correo by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }       // true = estamos enviando al servidor
    var error by remember { mutableStateOf<String?>(null) }  // algo salió mal
    var info by remember { mutableStateOf<String?>(null) }   // mensaje de éxito
    // Sirve para llamar al servidor cuando el usuario toca un botón.
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SmartBookTopAppBar(
                title = "Restablecer acceso",
                navigationIcon = {
                    // Botón atrás: vuelve a la pantalla de login.
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "Ingresa el correo registrado. Si el servidor lo permite, recibirás un enlace o código.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it; error = null },
                        label = { Text("Correo") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        ),
                    )
                    error?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                    info?.let {
                        Text(it, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
                    }
                    // Cuando toca enviar, mandamos el correo al servidor.
                    Button(
                        onClick = {
                            scope.launch {
                                loading = true
                                error = null
                                info = null
                                val r = authRepository.solicitarRestablecimiento(correo.trim())
                                loading = false
                                r.fold(
                                    onSuccess = {
                                        // Callback: solicitud enviada correctamente, muestra mensaje informativo.
                                        info = "Solicitud enviada. Revisa tu buzón y sigue el enlace, o usa un código en la siguiente pantalla."
                                    },
                                    onFailure = { e ->
                                        // Callback: falló el envío, muestra el error.
                                        error = e.message ?: "No se pudo enviar la solicitud."
                                    },
                                )
                            }
                        },
                        enabled = !loading && correo.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                    ) {
                        Text(if (loading) "Enviando…" else "Enviar solicitud")
                    }
                }
            }
            // Botón atajo: si ya tiene código, va directo a restablecer contraseña.
            FilledTonalButton(onClick = onGoReset, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
                Text("Ya tengo código")
            }
        }
    }
}
