// package: Paquete de los objetos DTO para la transferencia de datos de inmuebles
package co.edu.ue.data.remote.dto

// data class InmuebleDto: Mapea la estructura JSON recibida y enviada a los endpoints de la API REST de Spring Boot para inmuebles
data class InmuebleDto(
    // val id: Long? = null: Identificador numérico que puede ser nulo (?) al crear un nuevo registro antes de ser procesado en el servidor
    val id: Long? = null,
    val direccion: String,
    val tipoInmueble: String,
    val precioArriendo: Double,
    val estado: String,
    val uriImagen: String? = null
)