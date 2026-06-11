package co.edu.cecar.cdi.smartbook.ui.screens.login

// ARCHIVO: LoginScreen.kt
// Pantalla de inicio de sesión: correo, contraseña y enlaces de recuperación.

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.cecar.cdi.smartbook.data.repository.AuthRepository
import co.edu.cecar.cdi.smartbook.ui.theme.CdiAzulOscuro

@Composable
fun LoginScreen(
    authRepository: AuthRepository,
    onAuthenticated: () -> Unit,
    onForgotPassword: () -> Unit,
    onResetWithCode: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // El ViewModel revisa los datos y llama al servidor.
    val vm: LoginViewModel = viewModel(factory = LoginViewModel.factory(authRepository))
    // Miramos el estado. Si cambia, la pantalla se actualiza sola.
    val state by vm.ui.collectAsStateWithLifecycle()
    // Si hay error, marcamos los campos en rojo.
    val hasError = state.error != null  // true si hay mensaje de error
    // Fondo con degradado azul institucional.
    val gradient = Brush.verticalGradient(
        colors = listOf(
            CdiAzulOscuro,
            CdiAzulOscuro.copy(alpha = 0.88f),
            MaterialTheme.colorScheme.background,
        ),
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradient),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "SmartBook",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
            )
            Text(
                "Gestión CDI · CECAR",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.92f),
                modifier = Modifier.padding(top = 4.dp, bottom = 28.dp),
                textAlign = TextAlign.Center,
            )

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text(
                        "Inicia sesión",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                    Text(
                        "Usa el correo y contraseña de tu cuenta.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    // Cuando escribe el correo, lo guardamos en el ViewModel.
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = vm::setEmail,
                        label = { Text("Correo") },
                        singleLine = true,
                        isError = hasError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = MaterialTheme.shapes.large,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                    )
                    // La contraseña se ve oculta (puntos, no letras).
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = vm::setPassword,
                        label = { Text("Contraseña") },
                        singleLine = true,
                        isError = hasError,
                        visualTransformation = PasswordVisualTransformation(),
                        shape = MaterialTheme.shapes.large,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                    )

                    // Aquí mostramos el mensaje de error si algo falló.
                    state.error?.let { err ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            shape = MaterialTheme.shapes.medium,
                        ) {
                            Text(
                                err,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp),
                            )
                        }
                    }

                    // Cuando toca "Ingresar", el ViewModel intenta el login. Si sale bien, vamos adelante.
                    Button(
                        onClick = { vm.login(onAuthenticated) },
                        enabled = !state.loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        shape = MaterialTheme.shapes.large,
                    ) {
                        Text(if (state.loading) "Ingresando…" else "Ingresar")
                    }

                    // Estos botones solo cambian de pantalla. No llaman al servidor aquí.
                    TextButton(onClick = onForgotPassword, modifier = Modifier.fillMaxWidth()) {
                        Text("Olvidé mi contraseña")
                    }
                    TextButton(onClick = onResetWithCode, modifier = Modifier.fillMaxWidth()) {
                        Text("Tengo un código de restablecimiento")
                    }
                }
            }
        }
    }
}
