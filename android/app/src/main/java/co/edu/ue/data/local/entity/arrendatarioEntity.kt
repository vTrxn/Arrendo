// package: Palabra clave que define la estructura de paquetes para organizar este archivo
package co.edu.ue.data.local.entity

// import: Trae las clases de anotación de la librería Room
import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity: Anotación de Room que mapea esta clase a la tabla "arrendatarios" de la base de datos local SQLite
@Entity(tableName = "arrendatarios")
// data class: Clase de datos de Kotlin para almacenar la información de los inquilinos
data class ArrendatarioEntity(
    // @PrimaryKey(autoGenerate = true): Identificador único automático en la tabla SQLite
    // val id: Long = 0: Clave primaria entera de 64 bits con valor inicial 0
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    // val nombreCompleto: Variable inmutable de tipo String que almacena los nombres y apellidos del inquilino
    val nombreCompleto: String,

    // val telefono: Variable inmutable de tipo String que guarda el número de teléfono celular o fijo del arrendatario
    val telefono: String,

    // val correo: Variable de tipo String para la dirección de correo electrónico del inquilino
    val correo: String,

    // val cedula: Variable de tipo String para el número de documento de identidad o Cédula
    val cedula: String,

    // val estaSincronizado: Marca lógica de tipo Boolean que indica si los datos del inquilino ya se enviaron al servidor
    val estaSincronizado: Boolean = false
)