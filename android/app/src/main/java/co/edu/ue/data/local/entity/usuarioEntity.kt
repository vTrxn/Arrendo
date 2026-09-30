// package: Palabra clave de Kotlin que declara el espacio de nombres o directorio lógico donde se ubica este archivo dentro del proyecto Android
package co.edu.ue.data.local.entity

// import: Palabra clave que importa clases y anotaciones externas de la librería oficial Room de Android Jetpack
import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity: Anotación de la biblioteca Room que le indica a la base de datos SQLite que esta clase representa una tabla de datos física
// tableName = "usuarios": Parámetro que especifica el nombre exacto de la tabla dentro de la base de datos SQL
@Entity(tableName = "usuarios")
// data: Palabra clave de Kotlin que convierte esta clase en un contenedor de datos puro, generando automáticamente métodos para copiar, comparar e imprimir información
// class: Palabra clave de Kotlin que se utiliza para declarar una nueva clase
// UsuarioEntity: Nombre de la clase que representa la estructura de las filas de la tabla de usuarios
data class UsuarioEntity(
    // @PrimaryKey: Anotación de Room que marca este campo como la clave primaria (Primary Key) única para identificar cada fila en la base de datos SQL
    // autoGenerate = true: Parámetro que ordena a SQLite que genere de forma automática e incremental el número identificador (1, 2, 3...) para cada nuevo registro
    // val: Palabra clave de Kotlin que declara una variable inmutable (su valor de solo lectura no se puede modificar después de asignado)
    // id: Nombre de la variable identificadora de la entidad
    // Long: Tipo de dato numérico entero de 64 bits para manejar números grandes
    // = 0: Valor inicial por defecto de la clave primaria
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    // val: Variable inmutable que almacena el nombre de usuario
    // nombreUsuario: Nombre del campo
    // String: Tipo de dato de Kotlin que almacena una cadena de texto
    val nombreUsuario: String,

    // val tokenAcceso: Variable inmutable de tipo String que guarda la clave o Token de seguridad JWT generado por el servidor Spring Boot
    val tokenAcceso: String,

    // val estaLogueado: Variable inmutable que guarda el estado de la sesión activa del usuario
    // Boolean: Tipo de dato lógico que solo puede almacenar valores verdadero (true) o falso (false)
    // = true: Valor inicial asignado por defecto en verdadero al registrar el inicio de sesión
    val estaLogueado: Boolean = true
)