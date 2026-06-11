package co.edu.cecar.cdi.smartbook

// =============================================================================
// ARCHIVO: AplicacionSmartBook.kt
// Se crea antes que cualquier pantalla al iniciar SmartBook.
// Aquí viven las herramientas compartidas: servidor, token, repositorios, etc.
// Todo se agrupa en AppContainer para usarlo en toda la app.
// =============================================================================

import android.app.Application
import co.edu.cecar.cdi.smartbook.di.AppContainer

// Clase Application: arranca la app a nivel de sistema.
class SmartBookApplication : Application() {

    // Contenedor con repositorios y servicios; se llena en onCreate.
    lateinit var container: AppContainer
        private set // Solo esta clase puede cambiar el valor

    // Android llama aquí al iniciar la aplicación.
    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this) // Crea dependencias con contexto del teléfono
    }
}
