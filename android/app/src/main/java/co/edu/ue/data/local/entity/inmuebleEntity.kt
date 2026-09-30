// package: Palabra clave de Kotlin que especifica el paquete o ubicación lógica de este archivo dentro del proyecto
package co.edu.ue.data.local.entity

// import: Trae las anotaciones @Entity y @PrimaryKey de la biblioteca Room para la integración con SQLite
import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity: Anotación de la librería Room que convierte esta clase Kotlin en una tabla de la base de datos local SQLite
// tableName = "inmuebles": Define el nombre de la tabla de la base de datos donde se almacenarán las propiedades
@Entity(tableName = "inmuebles")
// data: Palabra clave de Kotlin que indica que es una clase especial para almacenar y estructurar datos
// class: Palabra clave para declarar la clase
// InmuebleEntity: Nombre de la clase que mapea la información de cada inmueble o propiedad
data class InmuebleEntity(
    // @PrimaryKey: Anotación que indica la clave primaria única para cada propiedad en la tabla SQL
    // autoGenerate = true: Le indica a la base de datos SQLite que autogenere el número de ID (1, 2, 3...)
    // val: Palabra clave que declara una propiedad de solo lectura (inmutable)
    // id: Nombre de la variable identificadora de la propiedad
    // Long: Tipo de dato numérico entero de 64 bits
    // = 0: Valor numérico inicial por defecto
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    // val direccion: Variable inmutable que guarda la dirección física de la propiedad
    // String: Tipo de dato de cadena de texto
    val direccion: String,

    // val tipoInmueble: Variable de tipo String que almacena la categoría de la propiedad (ejemplo: "Casa", "Apartamento", "Local")
    val tipoInmueble: String,

    // val precioArriendo: Variable inmutable que almacena el valor monetario mensual del arriendo
    // Double: Tipo de dato numérico con decimales flotantes de 64 bits
    val precioArriendo: Double,

    // val estado: Variable de tipo String que guarda la disponibilidad actual (ejemplo: "Disponible", "Arrendado")
    val estado: String,

    // val uriImagen: Variable de tipo String que guarda la ruta de archivo o URI de la imagen de la propiedad (vacía por defecto)
    val uriImagen: String = "",

    // val estaSincronizado: Variable lógica que indica si el registro ya fue transmitido al servidor remoto backend Spring Boot
    // Boolean: Tipo de dato lógico (true para sí sincronizado, false para pendiente)
    // = false: Valor inicial por defecto al crear una propiedad localmente en la app
    val estaSincronizado: Boolean = false
)