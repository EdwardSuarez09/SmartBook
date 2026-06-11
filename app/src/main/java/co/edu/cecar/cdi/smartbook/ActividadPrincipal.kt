package co.edu.cecar.cdi.smartbook

// =============================================================================
// ARCHIVO: ActividadPrincipal.kt
// Punto de entrada visible de SmartBook en Android.
// Prepara la ventana a pantalla completa y conecta Jetpack Compose con la app.
// Obtiene el contenedor de dependencias y muestra SmartBookApp.
// No define pantallas propias; solo arranca la interfaz.
// =============================================================================

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import co.edu.cecar.cdi.smartbook.ui.SmartBookApp

// Activity principal: Android abre la app por aquí.
class MainActivity : ComponentActivity() {

    // Se ejecuta una sola vez al abrir la app.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Inicialización estándar de Android
        enableEdgeToEdge() // Dibuja debajo de la barra de estado y navegación
        val app = application as SmartBookApplication // Referencia a la aplicación global
        setContent {
            SmartBookApp(app.container) // Muestra toda la interfaz con Compose
        }
    }
}
