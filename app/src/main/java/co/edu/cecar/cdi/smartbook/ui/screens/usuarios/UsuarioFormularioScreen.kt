package co.edu.cecar.cdi.smartbook.ui.screens.usuarios

// ARCHIVO: UsuarioFormularioScreen.kt
// Formulario para crear o editar un usuario (rol, estado y contraseña).

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import co.edu.cecar.cdi.smartbook.data.model.ActualizarUsuarioDto
import co.edu.cecar.cdi.smartbook.data.model.RegistrarUsuarioDto
import co.edu.cecar.cdi.smartbook.data.repository.UsuariosRepository
import kotlinx.coroutines.launch

@Composable
fun UsuarioFormularioScreen(
    repository: UsuariosRepository,
    idInicial: String?,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var identificacion by remember { mutableStateOf("") }  // cédula del usuario (solo al crear)
    var nombres by remember { mutableStateOf("") }         // nombre completo
    var email by remember { mutableStateOf("") }           // correo electrónico
    var password by remember { mutableStateOf("") }        // contraseña (solo al crear)
    var rol by remember { mutableStateOf(2) }       // 1 = administrador, 2 = vendedor
    var activo by remember { mutableStateOf(true) }  // true = usuario activo
    var loading by remember { mutableStateOf(false) }  // true = estamos guardando
    var error by remember { mutableStateOf<String?>(null) }  // mensaje si falla el guardado
    val scope = rememberCoroutineScope()  // sirve para llamar al servidor al guardar
    val editMode = !idInicial.isNullOrBlank()  // true = estamos editando, no creando

    // LaunchedEffect: si estamos editando, traemos los datos actuales del servidor.
    LaunchedEffect(idInicial) {
        if (idInicial.isNullOrBlank()) return@LaunchedEffect
        repository.porId(idInicial).getOrNull()?.let { u ->
            identificacion = u.identificacion.orEmpty()
            nombres = u.nombres.orEmpty()
            email = u.email.orEmpty()
            rol = when (u.rol?.trim()?.lowercase()) {  // when: convierte el rol del servidor a número
                "admin", "administrador", "1" -> 1
                else -> 2
            }
            activo = u.activo ?: true
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (!editMode) {
                    campo("Identificación", identificacion) { identificacion = it }
                }
                campo("Nombres", nombres) { nombres = it }
                campo("Correo", email) { email = it }
                if (!editMode) {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        ),
                    )
                }
                Text("Rol", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Callbacks: el usuario elige rol administrador o vendedor.
                    FilterChip(selected = rol == 1, onClick = { rol = 1 }, label = { Text("Administrador") })
                    FilterChip(selected = rol == 2, onClick = { rol = 2 }, label = { Text("Vendedor") })
                }
                if (editMode) {
                    Text("Estado", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Callbacks: el usuario elige si el usuario queda activo o inactivo.
                        FilterChip(selected = activo, onClick = { activo = true }, label = { Text("Activo") })
                        FilterChip(selected = !activo, onClick = { activo = false }, label = { Text("Inactivo") })
                    }
                }
                error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            }
        }
        // Botón guardar: crea o actualiza el usuario según el modo. Si sale bien, vuelve atrás.
        Button(
            onClick = {
                scope.launch {
                    loading = true
                    error = null
                    val r = if (editMode) {
                        repository.actualizar(
                            idInicial!!,
                            ActualizarUsuarioDto(
                                nombres = nombres.trim().ifBlank { null },
                                email = email.trim().ifBlank { null },
                                rol = rol,
                                activo = activo,
                            ),
                        )
                    } else {
                        if (identificacion.isBlank() || nombres.isBlank() || email.isBlank() || password.isBlank()) {
                            loading = false
                            error = "Completa todos los campos obligatorios."
                            return@launch
                        }
                        repository.crear(
                            RegistrarUsuarioDto(
                                identificacion = identificacion.trim(),
                                nombres = nombres.trim(),
                                email = email.trim(),
                                password = password,
                                rol = rol,
                            ),
                        )
                    }
                    loading = false
                    r.fold(onSuccess = { onSaved() }, onFailure = { e -> error = e.message })
                }
            },
            enabled = !loading && nombres.isNotBlank(),
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 24.dp),
            shape = MaterialTheme.shapes.large,
        ) {
            Text(if (loading) "Guardando…" else "Guardar")
        }
    }
}

@Composable
private fun campo(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        ),
    )
}
