package co.edu.cecar.cdi.smartbook.data.model

// =============================================================================
// ARCHIVO: ModelosApi.kt
// Modelos de datos (DTO) para hablar con el API de SmartBooks.
// Serializan Kotlin → JSON al enviar (POST/PUT) y deserializan JSON → Kotlin al recibir (GET).
// La anotación @Serializable permite convertir entre objeto Kotlin y texto JSON.
// Al final hay funciones extra solo para la pantalla (tituloVisible, camposDetalle).
// =============================================================================

import kotlinx.serialization.Serializable

// --- Seguridad (login y recuperar contraseña) ---

// Lo que envías al hacer login: correo y contraseña
@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String,
)

// Pides que manden un código al correo para restablecer la contraseña
@Serializable
data class SolicitarRestablecimientoDto(
    val email: String,
)

// Confirmas el cambio: código del correo + contraseña nueva
@Serializable
data class RestablecerContrasenaDto(
    val codigo: String,
    val newPassword: String,
)

// Respuesta del login cuando el token viene dentro de un objeto JSON
@Serializable
data class RespuestaLoginDto(
    val token: String? = null,
    val jwt: String? = null,
    val accessToken: String? = null,
    val datos: RespuestaLoginDto? = null,
    val data: RespuestaLoginDto? = null,
)

// --- Clientes (quienes compran en la librería) ---

// Cliente tal como lo devuelve el API; campos opcionales porque el JSON puede variar
@Serializable
data class ClienteDto(
    val identificacion: String? = null,
    val nombres: String? = null,
    val apellidos: String? = null,
    val email: String? = null,
    val correo: String? = null,
    val celular: String? = null,
    val telefono: String? = null,
    val fechaNacimiento: String? = null,
)

// Datos mínimos para crear un cliente nuevo (POST)
@Serializable
data class CrearClienteDto(
    val identificacion: String,
    val nombres: String,
    val email: String? = null,
    val celular: String? = null,
    val fechaNacimiento: String? = null,
)

// Solo envías los campos que quieres cambiar de un cliente (PUT)
@Serializable
data class ActualizarClienteDto(
    val nombres: String? = null,
    val email: String? = null,
    val celular: String? = null,
    val fechaNacimiento: String? = null,
)

// --- Libros (catálogo de productos) ---

// Libro con stock, precios y lote
@Serializable
data class LibroDto(
    val id: Int? = null,
    val nombre: String? = null,
    val nivel: String? = null,
    val tipo: String? = null,
    val edicion: String? = null,
    val unidades: Int? = null,
    val lote: Int? = null,
    val valorCompra: Double? = null,
    val valorVentaPublico: Double? = null,
)

// Lo que envías para crear un libro en el catálogo
@Serializable
data class CrearLibroDto(
    val nombre: String,
    val nivel: String? = null,
    val tipo: String? = null,
    val edicion: String? = null,
    val unidades: Int? = null,
    val lote: Int? = null,
    val valorCompra: Double? = null,
    val valorVentaPublico: Double? = null,
)

// Campos que puedes editar de un libro (nombre, nivel, tipo, edición)
@Serializable
data class ActualizarLibroDto(
    val nombre: String? = null,
    val nivel: String? = null,
    val tipo: String? = null,
    val edicion: String? = null,
)

// --- Lotes (grupos de mercancía que entra al almacén) ---

// Lote con número, código y descripción
@Serializable
data class LoteDto(
    val lote: Int? = null,
    val codigo: String? = null,
    val descripcion: String? = null,
)

// Solo necesitas el número de lote para crear uno nuevo
@Serializable
data class CrearLoteDto(
    val lote: Int,
)

// --- Ventas (transacciones con clientes) ---

// Una línea dentro de una venta: libro, cantidad, lote
@Serializable
data class VentaItemDto(
    val libroId: Int? = null,
    val libro: String? = null,
    val lote: Int? = null,
    val cantidad: Int? = null,
)

// Venta completa: cliente, total, fecha e ítems
@Serializable
data class VentaDto(
    val id: Int? = null,
    val cliente: String? = null,
    val total: Double? = null,
    val numeroComprobante: String? = null,
    val observaciones: String? = null,
    val fecha: String? = null,
    val items: List<VentaItemDto>? = null,
)

// Un ítem al registrar una venta nueva
@Serializable
data class CrearVentaItemDto(
    val libroId: Int,
    val lote: Int,
    val cantidad: Int,
)

// Cabecera de venta: cliente, comprobante opcional y lista de ítems
@Serializable
data class CrearVentaDto(
    val identificacionCliente: String,
    val numeroComprobante: String? = null,
    val observaciones: String? = null,
    val items: List<CrearVentaItemDto>,
)

// --- Usuarios (empleados del sistema) ---

// Usuario con rol y si está activo o no
@Serializable
data class UsuarioDto(
    val id: Int? = null,
    val identificacion: String? = null,
    val nombres: String? = null,
    val email: String? = null,
    val rol: String? = null,
    val activo: Boolean? = null,
)

// Datos para crear un usuario (incluye contraseña en texto)
@Serializable
data class RegistrarUsuarioDto(
    val identificacion: String,
    val nombres: String,
    val email: String,
    val password: String,
    val rol: Int,
)

// Cambios parciales de un usuario (aquí no cambias la contraseña)
@Serializable
data class ActualizarUsuarioDto(
    val nombres: String? = null,
    val email: String? = null,
    val rol: Int? = null,
    val activo: Boolean? = null,
)

// --- Inventarios (stock por libro y lote) ---

// Una fila del inventario: libro, unidades y si hay poco stock
@Serializable
data class InventarioItemDto(
    val nivelLibro: String? = null,
    val nombreLibro: String? = null,
    val libroId: Int? = null,
    val nombre: String? = null,
    val unidades: Int? = null,
    val cantidad: Int? = null,
    val stock: Int? = null,
    val lote: Int? = null,
    val stockBajo: Boolean? = null,
    val bajoStock: Boolean? = null,
)

// Resumen del inventario: totales + lista de ítems
@Serializable
data class InventarioDto(
    val totalLibros: Int? = null,
    val totalUnidades: Int? = null,
    val stockBajo: Int? = null,
    val libros: List<InventarioItemDto>? = null,
    val items: List<InventarioItemDto>? = null,
)

// --- Ingresos (mercancía que entra al almacén) ---

// Registro de un ingreso: unidades de un libro que llegaron
@Serializable
data class IngresoDto(
    val id: Int? = null,
    val libroId: Int? = null,
    val nombreLibro: String? = null,
    val unidades: Int? = null,
    val lote: Int? = null,
    val fecha: String? = null,
    val valorCompra: Double? = null,
    val valorVentaPublico: Double? = null,
)

// Lo que envías para registrar un ingreso nuevo de stock
@Serializable
data class CrearIngresoDto(
    val libroId: Int,
    val unidades: Int,
    val lote: Int,
    val valorCompra: Double? = null,
    val valorVentaPublico: Double? = null,
)

// --- Dashboard (pantalla principal con resumen) ---

// Venta resumida para el dashboard: cliente y total
@Serializable
data class VentaResumenDto(
    val cliente: String? = null,
    val nombre: String? = null,
    val total: String? = null,
    val valor: String? = null,
)

// Estadísticas generales y ventas recientes de la librería
@Serializable
data class DashboardDto(
    val totalClientes: Int? = null,
    val totalLibros: Int? = null,
    val ventasMes: Int? = null,
    val ingresosMes: String? = null,
    val ventasHoy: List<VentaResumenDto>? = null,
    val ultimasVentas: List<VentaResumenDto>? = null,
    val datos: DashboardDto? = null,
    val data: DashboardDto? = null,
)

// --- Ayudas para listas (RecyclerView / LazyColumn) ---
// Texto amigable cuando el API usa nombres distintos o valores null

// ID del cliente para mostrar en listas
fun ClienteDto.idVisible(): String? = identificacion?.takeIf { it.isNotBlank() }

// Título principal del cliente en una fila de lista
fun ClienteDto.tituloVisible(): String =
    nombres?.takeIf { it.isNotBlank() }
        ?: email?.takeIf { it.isNotBlank() }
        ?: correo?.takeIf { it.isNotBlank() }
        ?: "—" // Guión si no hay dato

fun LibroDto.idVisible(): String? = id?.toString()

fun LibroDto.tituloVisible(): String =
    nombre?.takeIf { it.isNotBlank() } ?: "—"

// Subtítulo con nivel, tipo, lote y unidades
fun LibroDto.subtituloVisible(): String =
    listOfNotNull(
        nivel?.takeIf { it.isNotBlank() }?.let { "Nivel $it" },
        tipo?.takeIf { it.isNotBlank() },
        lote?.let { "Lote $it" },
        unidades?.let { "$it uds" },
    ).joinToString(" · ").ifBlank { idVisible()?.let { "ID $it" } ?: "" }

fun VentaDto.idVisible(): String? = id?.toString()

fun VentaDto.tituloVisible(): String =
    cliente?.takeIf { it.isNotBlank() }
        ?: numeroComprobante?.takeIf { it.isNotBlank() }
        ?: idVisible()
        ?: "—"

fun UsuarioDto.idVisible(): String? = id?.toString()

fun UsuarioDto.tituloVisible(): String =
    nombres?.takeIf { it.isNotBlank() }
        ?: email?.takeIf { it.isNotBlank() }
        ?: "—"

fun UsuarioDto.subtituloVisible(): String =
    listOfNotNull(
        email?.takeIf { it.isNotBlank() },
        rol?.takeIf { it.isNotBlank() },
        activo?.let { if (it) "Activo" else "Inactivo" },
    ).joinToString(" · ")

fun LoteDto.tituloVisible(): String =
    lote?.let { "Lote $it" }
        ?: codigo?.takeIf { it.isNotBlank() }
        ?: "—"

fun LoteDto.subtituloVisible(): String? =
    descripcion?.takeIf { it.isNotBlank() } ?: "Código de entrada de mercancía"

fun LoteDto.idVisible(): String? = lote?.toString() ?: codigo

fun IngresoDto.tituloVisible(): String =
    nombreLibro?.takeIf { it.isNotBlank() }
        ?: libroId?.let { "Libro #$it" }
        ?: lote?.let { "Lote $it" }
        ?: idVisible()
        ?: "Ingreso"

fun IngresoDto.subtituloVisible(): String =
    listOfNotNull(
        lote?.let { "Lote $it" },
        unidades?.let { "$it unidades" },
        fecha?.takeIf { it.isNotBlank() },
    ).joinToString(" · ")

fun IngresoDto.idVisible(): String? = id?.toString()

fun InventarioItemDto.tituloVisible(): String =
    nombreLibro?.takeIf { it.isNotBlank() }
        ?: nombre?.takeIf { it.isNotBlank() }
        ?: libroId?.let { "Libro #$it" }
        ?: "—"

fun InventarioItemDto.subtituloVisible(): String =
    listOfNotNull(
        nivelLibro?.let { "Nivel $it" },
        (unidades ?: cantidad ?: stock)?.let { "$it uds" },
        lote?.let { "Lote $it" },
        if (stockBajo == true || bajoStock == true) "Stock bajo" else null,
    ).joinToString(" · ")

// True si el API marcó este ítem con alerta de poco stock
fun InventarioItemDto.esStockBajo(): Boolean = stockBajo == true || bajoStock == true

fun InventarioItemDto.idVisible(): String? =
    "${libroId ?: nombreLibro ?: nombre}-$lote"

// --- Ayudas de negocio ---

// Busca el JWT en la respuesta de login (puede venir con nombres distintos o anidado)
fun RespuestaLoginDto.extraerToken(): String? {
    listOf(token, jwt, accessToken).forEach {
        if (!it.isNullOrBlank() && it.length > 10) return it // Token encontrado
    }
    return datos?.extraerToken() ?: data?.extraerToken() // Busca dentro de datos/data
}

// A veces el dashboard viene dentro de "datos" o "data"; saca el objeto útil
fun DashboardDto.datosReales(): DashboardDto = datos ?: data ?: this

// --- Ayudas para pantallas de detalle ---
// Arman pares (etiqueta, valor) para mostrar todos los campos en una ficha

fun ClienteDto.camposDetalle(): List<Pair<String, String>> = buildList {
    idVisible()?.let { add("Identificación" to it) }
    nombres?.takeIf { it.isNotBlank() }?.let { add("Nombres" to it) }
    apellidos?.takeIf { it.isNotBlank() }?.let { add("Apellidos" to it) }
    (email ?: correo)?.takeIf { it.isNotBlank() }?.let { add("Correo" to it) }
    (celular ?: telefono)?.takeIf { it.isNotBlank() }?.let { add("Celular" to it) }
    fechaNacimiento?.takeIf { it.isNotBlank() }?.let { add("Fecha nacimiento" to it) }
}

fun LibroDto.camposDetalle(): List<Pair<String, String>> = buildList {
    idVisible()?.let { add("ID" to it) }
    nombre?.takeIf { it.isNotBlank() }?.let { add("Nombre" to it) }
    nivel?.let { add("Nivel" to it) }
    tipo?.let { add("Tipo" to it.toString()) }
    edicion?.takeIf { it.isNotBlank() }?.let { add("Edición" to it) }
    unidades?.let { add("Unidades en stock" to it.toString()) }
    lote?.let { add("Lote" to it.toString()) }
    valorCompra?.let { add("Valor compra" to it.toString()) }
    valorVentaPublico?.let { add("Valor venta público" to it.toString()) }
}

fun VentaDto.camposDetalle(): List<Pair<String, String>> = buildList {
    idVisible()?.let { add("ID" to it) }
    fecha?.takeIf { it.isNotBlank() }?.let { add("Fecha" to it) }
    cliente?.takeIf { it.isNotBlank() }?.let { add("Cliente" to it) }
    numeroComprobante?.takeIf { it.isNotBlank() }?.let { add("Comprobante" to it) }
    total?.let { add("Total" to it.toString()) }
    observaciones?.takeIf { it.isNotBlank() }?.let { add("Observaciones" to it) }
    items?.forEachIndexed { i, item ->
        val detalle = listOfNotNull(
            item.libro?.takeIf { it.isNotBlank() },
            item.libroId?.let { "Libro $it" },
            item.cantidad?.let { "x$it" },
            item.lote?.let { "Lote $it" },
        ).joinToString(" · ")
        if (detalle.isNotBlank()) add("Ítem ${i + 1}" to detalle)
    }
}

fun IngresoDto.camposDetalle(): List<Pair<String, String>> = buildList {
    idVisible()?.let { add("ID" to it) }
    nombreLibro?.takeIf { it.isNotBlank() }?.let { add("Libro" to it) }
    libroId?.let { add("ID libro" to it.toString()) }
    lote?.let { add("Lote" to it.toString()) }
    unidades?.let { add("Unidades" to it.toString()) }
    fecha?.takeIf { it.isNotBlank() }?.let { add("Fecha" to it) }
    valorCompra?.let { add("Valor compra" to it.toString()) }
    valorVentaPublico?.let { add("Valor venta" to it.toString()) }
}

fun LoteDto.camposDetalle(): List<Pair<String, String>> = buildList {
    lote?.let { add("Número de lote" to it.toString()) }
    codigo?.takeIf { it.isNotBlank() }?.let { add("Código" to it) }
    descripcion?.takeIf { it.isNotBlank() }?.let { add("Descripción" to it) }
}

fun InventarioItemDto.camposDetalle(): List<Pair<String, String>> = buildList {
    tituloVisible().takeIf { it != "—" }?.let { add("Libro" to it) }
    nivelLibro?.let { add("Nivel" to it) }
    (unidades ?: cantidad ?: stock)?.let { add("Unidades" to it.toString()) }
    lote?.let { add("Lote" to it.toString()) }
    if (esStockBajo()) add("Estado" to "Stock bajo")
}

fun UsuarioDto.camposDetalle(): List<Pair<String, String>> = buildList {
    idVisible()?.let { add("ID" to it) }
    identificacion?.takeIf { it.isNotBlank() }?.let { add("Identificación" to it) }
    nombres?.takeIf { it.isNotBlank() }?.let { add("Nombres" to it) }
    email?.takeIf { it.isNotBlank() }?.let { add("Correo" to it) }
    rol?.let { add("Rol" to it.toString()) }
    activo?.let { add("Activo" to if (it) "Sí" else "No") }
}

fun InventarioDto.camposDetalle(): List<Pair<String, String>> = buildList {
    totalLibros?.let { add("Total libros" to it.toString()) }
    totalUnidades?.let { add("Total unidades" to it.toString()) }
    stockBajo?.let { add("Stock bajo" to it.toString()) }
}
