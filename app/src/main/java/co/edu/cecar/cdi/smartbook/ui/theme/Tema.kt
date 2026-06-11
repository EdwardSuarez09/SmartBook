package co.edu.cecar.cdi.smartbook.ui.theme

// =============================================================================
// ARCHIVO: Tema.kt
// Define el aspecto visual global de SmartBook (Material Design 3).
// Combina colores claros, tipografía y formas redondeadas.
// Envuelve la app en SmartBookTheme para que todas las pantallas hereden el estilo.
// =============================================================================

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Esquema de colores claro basado en la paleta CDI.
private val LightColors = lightColorScheme(
    primary = CdiRojo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD6),
    onPrimaryContainer = Color(0xFF410002),
    secondary = CdiAzulOscuro,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD0E4FF),
    onSecondaryContainer = Color(0xFF001D36),
    tertiary = CdiAzulOscuro,
    onTertiary = Color.White,
    background = CdiFondoPantalla,
    onBackground = CdiGrisOscuroTexto,
    surface = Color.White,
    onSurface = CdiGrisOscuroTexto,
    surfaceVariant = CdiSurfaceVariante,
    onSurfaceVariant = Color(0xFF4A5F71),
    outline = CdiGrisBorde,
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
)

// Bordes redondeados para tarjetas, campos y botones.
private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(22.dp),
)

@Composable
fun SmartBookTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography, // Definida en Tipografia.kt
        shapes = AppShapes,
        content = content,
    )
}
