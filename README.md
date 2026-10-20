# SmartBook

Aplicación Android para apoyar la gestión de una librería. SmartBook ofrece una interfaz móvil para consultar y administrar libros, clientes, ventas e inventario, conectándose al servicio web de SmartBooks.

## Funcionalidades

- Inicio de sesión, cierre de sesión y recuperación de contraseña.
- Panel principal con resumen de la operación.
- Gestión de clientes y consulta de sus detalles.
- Catálogo de libros, detalles y formularios de creación y edición.
- Registro y consulta de ventas.
- Gestión de lotes e ingresos de inventario.
- Consulta de inventario general y por lote.
- Administración de usuarios y consulta del perfil.

> Las operaciones que requieren datos del negocio dependen de la disponibilidad del API y de una cuenta válida.

## Tecnologías

- Kotlin y Jetpack Compose con Material 3.
- Navigation 3 para la navegación entre pantallas.
- Ktor Client para las solicitudes HTTP y Kotlin Serialization para JSON.
- DataStore y AndroidX Security Crypto para almacenamiento local.
- Gradle y Android Gradle Plugin.

## Requisitos

- Android Studio compatible con Android Gradle Plugin 9.1.1.
- JDK 17.
- Android SDK Platform 36.1.
- Dispositivo o emulador con Android 8.0 (API 26) o posterior.
- Acceso a Internet y disponibilidad de `https://api.smartbooks.cecar.cloud/api/` para las funciones conectadas.

El proyecto incluye Gradle Wrapper, por lo que no es necesario instalar Gradle por separado.

## Abrir y ejecutar

1. Clona el repositorio y ábrelo en Android Studio:

   ```bash
   git clone https://github.com/EdwardSuarez09/SmartBook.git
   cd SmartBook
   ```

2. Deja que Android Studio sincronice el proyecto e instale los componentes del SDK que solicite.
3. Selecciona un emulador o conecta un dispositivo con depuración USB habilitada.
4. Ejecuta la configuración `app` desde Android Studio.

También puedes compilar desde la terminal en la raíz del repositorio:

```bash
# Windows
.\gradlew.bat assembleDebug

# macOS o Linux
./gradlew assembleDebug
```

El APK de depuración se genera en `app/build/outputs/apk/debug/app-debug.apk`.

## Pruebas

Ejecuta las pruebas unitarias del módulo `app` con:

```bash
# Windows
.\gradlew.bat testDebugUnitTest

# macOS o Linux
./gradlew testDebugUnitTest
```

Las pruebas instrumentadas requieren un emulador o dispositivo conectado:

```bash
# Windows
.\gradlew.bat connectedDebugAndroidTest

# macOS o Linux
./gradlew connectedDebugAndroidTest
```

## Estructura del proyecto

```text
app/src/main/
├── java/co/edu/cecar/cdi/smartbook/
│   ├── data/          # Modelos, cliente HTTP, almacenamiento y repositorios
│   ├── di/            # Contenedor de dependencias de la aplicación
│   ├── navigation/    # Rutas y navegación de autenticación y pantallas
│   └── ui/            # Compose, tema y pantallas
└── res/               # Recursos Android
```

## API

La aplicación usa el API de SmartBooks en `https://api.smartbooks.cecar.cloud/api/`. La URL base está definida en `app/src/main/java/co/edu/cecar/cdi/smartbook/data/remote/ConstantesApi.kt`. El backend no forma parte de este repositorio.

No guardes credenciales ni secretos en el código fuente. Para trabajar con otro entorno de API, cambia la URL base según la configuración de ese entorno.

## Contribuir

1. Crea una rama para tu cambio.
2. Realiza los cambios y ejecuta las pruebas pertinentes.
3. Abre un pull request describiendo el propósito y las pruebas ejecutadas.
