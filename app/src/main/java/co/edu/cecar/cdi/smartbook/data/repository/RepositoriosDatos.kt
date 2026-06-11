package co.edu.cecar.cdi.smartbook.data.repository

// =============================================================================
// ARCHIVO: RepositoriosDatos.kt
// Repositorios de datos: uno por cada parte del negocio SmartBook.
// Cada clase hace peticiones HTTP a un recurso del API (Clientes, Libros, etc.).
// Todos usan el mismo patrón: GET/POST/PUT, verificarExitoHttp y Result<T>.
// =============================================================================

import android.net.Uri
import co.edu.cecar.cdi.smartbook.data.model.ActualizarClienteDto
import co.edu.cecar.cdi.smartbook.data.model.ActualizarLibroDto
import co.edu.cecar.cdi.smartbook.data.model.ActualizarUsuarioDto
import co.edu.cecar.cdi.smartbook.data.model.ClienteDto
import co.edu.cecar.cdi.smartbook.data.model.CrearClienteDto
import co.edu.cecar.cdi.smartbook.data.model.CrearIngresoDto
import co.edu.cecar.cdi.smartbook.data.model.CrearLibroDto
import co.edu.cecar.cdi.smartbook.data.model.CrearLoteDto
import co.edu.cecar.cdi.smartbook.data.model.CrearVentaDto
import co.edu.cecar.cdi.smartbook.data.model.DashboardDto
import co.edu.cecar.cdi.smartbook.data.model.IngresoDto
import co.edu.cecar.cdi.smartbook.data.model.InventarioItemDto
import co.edu.cecar.cdi.smartbook.data.model.LibroDto
import co.edu.cecar.cdi.smartbook.data.model.LoteDto
import co.edu.cecar.cdi.smartbook.data.model.RegistrarUsuarioDto
import co.edu.cecar.cdi.smartbook.data.model.UsuarioDto
import co.edu.cecar.cdi.smartbook.data.model.VentaDto
import co.edu.cecar.cdi.smartbook.data.remote.HttpClientProvider
import co.edu.cecar.cdi.smartbook.data.remote.bodyJson
import co.edu.cecar.cdi.smartbook.data.remote.bodyJsonList
import co.edu.cecar.cdi.smartbook.data.remote.setJsonBody
import co.edu.cecar.cdi.smartbook.data.remote.verificarExitoHttp
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put

// --- Dashboard (resumen general) ---

// Pide al servidor las estadísticas y ventas recientes
class DashboardRepository {
    private val client = HttpClientProvider.client

    // Obtiene totales: clientes, libros, ventas del mes, etc.
    suspend fun obtener(): Result<DashboardDto> = runCatching {
        client.get("Dashboard").also { it.verificarExitoHttp() }.bodyJson<DashboardDto>()
    }
}

// --- Clientes ---

// Crear, leer, actualizar clientes (CRUD)
class ClientesRepository {
    private val client = HttpClientProvider.client

    // Lista clientes; puedes filtrar por nombre si quieres
    suspend fun listar(nombre: String? = null): Result<List<ClienteDto>> = runCatching {
        client.get("Clientes") {
            if (!nombre.isNullOrBlank()) parameter("nombres", nombre) // Filtro opcional
        }.also { it.verificarExitoHttp() }.bodyJsonList<ClienteDto>()
    }

    // Busca un cliente por su número de identificación
    suspend fun porIdentificacion(identificacion: String): Result<ClienteDto> = runCatching {
        client.get("Clientes/${identificacion.encodePath()}").also { it.verificarExitoHttp() }.bodyJson<ClienteDto>()
    }

    // Registra un cliente nuevo en el sistema
    suspend fun crear(payload: CrearClienteDto): Result<Unit> = runCatching {
        client.post("Clientes") { setJsonBody(payload) }.also { it.verificarExitoHttp() }
    }

    // Actualiza los datos de un cliente que ya existe
    suspend fun actualizar(identificacion: String, payload: ActualizarClienteDto): Result<Unit> = runCatching {
        client.put("Clientes/${identificacion.encodePath()}") { setJsonBody(payload) }.also { it.verificarExitoHttp() }
    }
}

// --- Libros ---

// Crear, leer, actualizar libros del catálogo (CRUD)
class LibrosRepository {
    private val client = HttpClientProvider.client

    // Lista libros; puedes buscar por nombre
    suspend fun listar(busqueda: String? = null): Result<List<LibroDto>> = runCatching {
        client.get("Libros") {
            if (!busqueda.isNullOrBlank()) parameter("Nombre", busqueda) // Filtro por nombre
        }.also { it.verificarExitoHttp() }.bodyJsonList<LibroDto>()
    }

    // Obtiene un libro por su ID (número)
    suspend fun porId(id: String): Result<LibroDto> = runCatching {
        client.get("Libros/${id.encodePath()}").also { it.verificarExitoHttp() }.bodyJson<LibroDto>()
    }

    // Crea un libro nuevo en el catálogo
    suspend fun crear(payload: CrearLibroDto): Result<Unit> = runCatching {
        client.post("Libros") { setJsonBody(payload) }.also { it.verificarExitoHttp() }
    }

    // Cambia datos de un libro que ya existe
    suspend fun actualizar(id: String, payload: ActualizarLibroDto): Result<Unit> = runCatching {
        client.put("Libros/${id.encodePath()}") { setJsonBody(payload) }.also { it.verificarExitoHttp() }
    }
}

// --- Lotes ---

// Lotes de mercancía: grupos de entradas al inventario
class LotesRepository {
    private val client = HttpClientProvider.client

    // Devuelve todos los lotes registrados
    suspend fun listar(): Result<List<LoteDto>> = runCatching {
        client.get("Lotes").also { it.verificarExitoHttp() }.bodyJsonList<LoteDto>()
    }

    // Crea un lote nuevo con el número que indiques
    suspend fun crear(payload: CrearLoteDto): Result<Unit> = runCatching {
        client.post("Lotes") { setJsonBody(payload) }.also { it.verificarExitoHttp() }
    }
}

// --- Inventarios ---

// Consulta cuántas unidades hay de cada libro por lote (stock)
class InventariosRepository {
    private val client = HttpClientProvider.client

    // Lista el inventario actual; opcionalmente filtra por lote
    suspend fun listar(lote: Int? = null): Result<List<InventarioItemDto>> = runCatching {
        client.get("Inventarios") {
            lote?.let { parameter("lote", it) } // Filtro por número de lote
        }.also { it.verificarExitoHttp() }.bodyJsonList<InventarioItemDto>()
    }
}

// --- Ventas ---

// Registrar y consultar ventas a clientes
class VentasRepository {
    private val client = HttpClientProvider.client

    // Lista ventas; puedes filtrar por nombre de cliente
    suspend fun listar(filtro: String? = null): Result<List<VentaDto>> = runCatching {
        client.get("Ventas") {
            if (!filtro.isNullOrBlank()) parameter("cliente", filtro)
        }.also { it.verificarExitoHttp() }.bodyJsonList<VentaDto>()
    }

    // Obtiene una venta por su ID
    suspend fun porId(id: String): Result<VentaDto> = runCatching {
        client.get("Ventas/${id.encodePath()}").also { it.verificarExitoHttp() }.bodyJson<VentaDto>()
    }

    // Registra una venta nueva con sus ítems (libros, cantidades, lotes)
    suspend fun crear(payload: CrearVentaDto): Result<Unit> = runCatching {
        client.post("Ventas") { setJsonBody(payload) }.also { it.verificarExitoHttp() }
    }
}

// --- Usuarios ---

// Usuarios del sistema: empleados, roles, etc.
class UsuariosRepository {
    private val client = HttpClientProvider.client

    // Lista todos los usuarios
    suspend fun listar(): Result<List<UsuarioDto>> = runCatching {
        client.get("Usuarios").also { it.verificarExitoHttp() }.bodyJsonList<UsuarioDto>()
    }

    // Busca un usuario por su ID
    suspend fun porId(id: String): Result<UsuarioDto> = runCatching {
        client.get("Usuarios/${id.encodePath()}").also { it.verificarExitoHttp() }.bodyJson<UsuarioDto>()
    }

    // Datos del usuario con el que iniciaste sesión
    suspend fun perfil(): Result<UsuarioDto> = runCatching {
        client.get("Usuarios/perfil").also { it.verificarExitoHttp() }.bodyJson<UsuarioDto>()
    }

    // Crea un usuario nuevo con rol y contraseña
    suspend fun crear(payload: RegistrarUsuarioDto): Result<Unit> = runCatching {
        client.post("Usuarios") { setJsonBody(payload) }.also { it.verificarExitoHttp() }
    }

    // Cambia nombre, correo, rol o estado de un usuario
    suspend fun actualizar(id: String, payload: ActualizarUsuarioDto): Result<Unit> = runCatching {
        client.put("Usuarios/${id.encodePath()}") { setJsonBody(payload) }.also { it.verificarExitoHttp() }
    }

    // Activa o desactiva un usuario (cambia su estado)
    suspend fun cambiarEstado(id: String): Result<Unit> = runCatching {
        client.patch("Usuarios/${id.encodePath()}/estado").also { it.verificarExitoHttp() }
    }
}

// --- Ingresos ---

// Entradas de mercancía al inventario (compras a proveedores)
class IngresosRepository {
    private val client = HttpClientProvider.client

    // Lista todos los ingresos registrados
    suspend fun listar(): Result<List<IngresoDto>> = runCatching {
        client.get("Ingresos").also { it.verificarExitoHttp() }.bodyJsonList<IngresoDto>()
    }

    // Registra un ingreso nuevo: unidades de un libro en un lote
    suspend fun crear(payload: CrearIngresoDto): Result<Unit> = runCatching {
        client.post("Ingresos") { setJsonBody(payload) }.also { it.verificarExitoHttp() }
    }
}

// --- Utilidad interna ---

// Codifica caracteres raros en la URL (espacios, @, etc.)
private fun String.encodePath(): String = Uri.encode(this)
