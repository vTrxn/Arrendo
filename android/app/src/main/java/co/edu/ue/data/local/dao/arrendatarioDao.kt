// package: Paquete donde reside la interfaz de acceso a la tabla arrendatarios dentro de la capa local de datos
package co.edu.ue.data.local.dao

// import: Trae las anotaciones de Room para persistencia local
import androidx.room.*
// import: Importa la entidad ArrendatarioEntity que modela la tabla 'arrendatarios'
import co.edu.ue.data.local.entity.ArrendatarioEntity
// import: Trae Flow para emitir reactivamente cambios en la lista de arrendatarios a la interfaz de usuario
import kotlinx.coroutines.flow.Flow

// @Dao: Marca esta interfaz como un Data Access Object administrado por la librería Room
@Dao
interface ArrendatarioDao {

    // @Query("SELECT * FROM arrendatarios ORDER BY nombreCompleto ASC"): Consulta SQL para listar inquilinos alfabéticamente
    // Retorna Flow reactivo para redibujar el RecyclerView de inquilinos de manera automática
    @Query("SELECT * FROM arrendatarios ORDER BY nombreCompleto ASC")
    fun obtenerTodosLosArrendatarios(): Flow<List<ArrendatarioEntity>>

    // @Query("SELECT COUNT(*) FROM arrendatarios"): Cuenta el total de inquilinos registrados para el contador del Dashboard
    @Query("SELECT COUNT(*) FROM arrendatarios")
    suspend fun contarArrendatarios(): Int

    // @Insert: Inserta un nuevo inquilino con estrategia REPLACE si el ID ya existe
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarArrendatario(arrendatario: ArrendatarioEntity): Long

    // @Update: Actualiza la información de un inquilino (teléfono, correo, cédula)
    @Update
    suspend fun actualizarArrendatario(arrendatario: ArrendatarioEntity)

    // @Delete: Elimina de la base de datos SQLite la fila correspondiente al inquilino seleccionado
    @Delete
    suspend fun eliminarArrendatario(arrendatario: ArrendatarioEntity)
}