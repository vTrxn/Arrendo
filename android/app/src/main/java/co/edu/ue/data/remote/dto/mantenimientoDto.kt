// package: DTO de reportes de mantenimiento para la API REST
package co.edu.ue.data.remote.dto

// data class MantenimientoDto: Estructura JSON para transferir reportes de incidencias con el servidor Spring Boot
data class MantenimientoDto(
    val id: Long? = null,
    val idInmueble: Long,
    val descripcionDano: String,
    val fechaReporte: String,
    val rutaFoto: String? = null,
    val estadoMantenimiento: String
)