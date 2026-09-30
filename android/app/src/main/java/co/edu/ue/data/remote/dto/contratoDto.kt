// package: DTO de contratos de arriendo
package co.edu.ue.data.remote.dto

// data class ContratoDto: Representación JSON de los contratos intercambiados con el backend Spring Boot
data class ContratoDto(
    val id: Long? = null,
    val idInmueble: Long,
    val idArrendatario: Long,
    val fechaInicio: String,
    val fechaFin: String,
    val canonMensual: Double,
    val estadoPago: String
)