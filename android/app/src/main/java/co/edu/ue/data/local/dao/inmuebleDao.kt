// package: Ubicación del archivo de interfaz DAO para los inmuebles dentro del paquete de persistencia local
package co.edu.ue.data.local.dao

// import: Trae las anotaciones de Room (@Dao, @Query, @Insert, @Update, @Delete, OnConflictStrategy)
import androidx.room.*
// import: Importa la clase de entidad InmuebleEntity que representa la tabla SQL 'inmuebles'
import co.edu.ue.data.local.entity.InmuebleEntity
// import: Trae la clase Flow de kotlinx.coroutines para soporte de flujos de datos reactivos en tiempo real
import kotlinx.coroutines.flow.Flow

// @Dao: Anotación de Room que define la interfaz como un Data Access Object (Objeto de Acceso a Datos)
@Dao
interface InmuebleDao {

    // @Query: Anotación de Room para ejecutar una consulta SQL SELECT en SQLite
    // "SELECT * FROM inmuebles ORDER BY id DESC": Obtiene todas las propiedades ordenadas de la más reciente a la más antigua
    // Flow<List<InmuebleEntity>>: Retorna un flujo reactivo asíncrono; la UI se actualiza automáticamente al haber cambios en la tabla
    @Query("SELECT * FROM inmuebles ORDER BY id DESC")
    fun obtenerTodosLosInmuebles(): Flow<List<InmuebleEntity>>

    // @Query("SELECT COUNT(*) FROM inmuebles"): Consulta SQL que cuenta el número total de propiedades registradas en la tabla
    // suspend: Palabra clave de Corrutinas en Kotlin para que la consulta se ejecute en segundo plano sin congelar la app
    // : Int: Retorna un número entero con el total de registros para mostrar en el Dashboard
    @Query("SELECT COUNT(*) FROM inmuebles")
    suspend fun contarInmuebles(): Int

    // @Insert: Anotación de Room que genera automáticamente la instrucción SQL 'INSERT INTO inmuebles ...'
    // onConflict = OnConflictStrategy.REPLACE: Si ya existe un registro con el mismo ID, lo sobrescribe en lugar de generar error
    // (inmueble: InmuebleEntity): Objeto con los datos de la propiedad a registrar
    // : Long: Retorna el ID numérico asignado por SQLite a la nueva fila insertada
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarInmueble(inmueble: InmuebleEntity): Long

    // @Update: Anotación de Room que genera automáticamente la instrucción SQL 'UPDATE inmuebles SET ... WHERE id = :id'
    // Permite actualizar campos de la propiedad (por ejemplo, marcar estaSincronizado = true tras sincronizar con PostgreSQL)
    @Update
    suspend fun actualizarInmueble(inmueble: InmuebleEntity)

    // @Delete: Anotación de Room que ejecuta una sentencia SQL 'DELETE FROM inmuebles WHERE id = :id'
    // Elimina de la base de datos local el registro que coincida con la clave primaria del objeto
    @Delete
    suspend fun eliminarInmueble(inmueble: InmuebleEntity)

    // @Query("SELECT * FROM inmuebles WHERE estaSincronizado = 0"): Consulta SQL para obtener propiedades creadas offline
    // Permite al motor de sincronización encontrar registros pendientes de enviar al servidor backend Spring Boot
    @Query("SELECT * FROM inmuebles WHERE estaSincronizado = 0")
    suspend fun obtenerInmueblesSinSincronizar(): List<InmuebleEntity>
}