// package: DTO para la transferencia de arrendatarios
package co.edu.ue.data.remote.dto

// data class ArrendatarioDto: Representación JSON de inquilinos en las peticiones REST HTTP
data class ArrendatarioDto(
    val id: Long? = null,
    val nombreCompleto: String,
    val telefono: String,
    val correo: String,
    val cedula: String
)