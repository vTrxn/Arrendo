// package: Paquete donde se ubican los objetos DTO (Data Transfer Object) de autenticación
package co.edu.ue.data.remote.dto

// DTO (Data Transfer Object): Patrón de diseño para representar la estructura exacta del cuerpo JSON intercambiado por red mediante la API REST

// data class SolicitudLoginDto: Estructura de datos en formato JSON enviada al servidor backend Spring Boot en el endpoint /api/v1/auth/login
data class SolicitudLoginDto(
    // val usuario: String: Cadena de texto con el nombre de usuario
    val usuario: String,
    // val clave: String: Cadena de texto con la contraseña
    val clave: String
)

// data class RespuestaLoginDto: Estructura del objeto JSON recibido como respuesta desde el servidor backend
data class RespuestaLoginDto(
    // val token: String: Clave de acceso criptográfica (Token JWT) generada por el servidor
    val token: String,
    // val usuario: String: Nombre del usuario autenticado
    val usuario: String
)