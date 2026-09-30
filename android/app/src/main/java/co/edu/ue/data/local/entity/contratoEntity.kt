// package: Palabra clave que define el paquete de la entidad de contratos
package co.edu.ue.data.local.entity

// import: Anotaciones de la biblioteca Room
import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity: Anotación de Room que crea la tabla "contratos" en la base de datos local SQLite
@Entity(tableName = "contratos")
// data class: Clase de datos que vincula un inmueble con un arrendatario y sus condiciones financieras
data class ContratoEntity(
    // @PrimaryKey(autoGenerate = true): Identificador único del contrato generado automáticamente por SQLite
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    // val idInmueble: ID numérico de tipo Long que relaciona este contrato con la clave primaria de la tabla inmuebles
    val idInmueble: Long,

    // val idArrendatario: ID numérico de tipo Long que relaciona este contrato con la clave primaria de la tabla arrendatarios
    val idArrendatario: Long,

    // val fechaInicio: Cadena de texto de tipo String que contiene la fecha inicial del contrato (ejemplo: "2026-01-01")
    val fechaInicio: String,

    // val fechaFin: Cadena de texto de tipo String con la fecha de expiración del contrato
    val fechaFin: String,

    // val canonMensual: Variable numérica de tipo Double para el costo acordado del arriendo mensual
    val canonMensual: Double,

    // val estadoPago: Variable de tipo String que almacena el estado financiero ("Al dia", "Pendiente", "Moroso")
    val estadoPago: String,

    // val estaSincronizado: Marca lógica (Boolean) que indica si este contrato fue enviado a la base de datos del servidor
    val estaSincronizado: Boolean = false
)