// package: Palabra clave que define la carpeta/paquete donde se ubica este archivo dentro del proyecto
package co.edu.ue.data.local.entity

// import: Trae las anotaciones de Room para el mapeo a SQLite
import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity: Anotación de la librería Room que convierte esta clase en la tabla SQL "mantenimientos"
// tableName = "mantenimientos": Nombre de la tabla en SQLite
@Entity(tableName = "mantenimientos")
// data: Palabra clave de Kotlin que declara una clase de datos pura
// class: Palabra clave para definir la clase
// MantenimientoEntity: Nombre de la clase que representa un reporte de daño en un inmueble
data class MantenimientoEntity(
    // @PrimaryKey(autoGenerate = true): Identificador único de la incidencia generado automáticamente por SQLite
    // val id: Long = 0: Clave primaria entera de 64 bits inicializada en 0
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    // val idInmueble: ID del inmueble afectado
    val idInmueble: Long,

    // val descripcionDano: Texto con la explicación del problema o daño reportado
    val descripcionDano: String,

    // val fechaReporte: Fecha en que se registró la incidencia
    val fechaReporte: String,

    // val rutaFoto: Dirección URI de la imagen tomada con la cámara nativa de Android como evidencia
    val rutaFoto: String = "",

    // val estadoMantenimiento: Estado del trabajo de reparación ("Pendiente", "En Proceso", "Resuelto")
    val estadoMantenimiento: String,

    // val estaSincronizado: Indica si el reporte ya fue enviado a la base de datos del servidor backend Spring Boot
    val estaSincronizado: Boolean = false
)