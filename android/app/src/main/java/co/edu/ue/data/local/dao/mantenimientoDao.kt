// package: Paquete del DAO de mantenimientos e incidencias en la capa de persistencia Room
package co.edu.ue.data.local.dao

// import: Anotaciones de Room para operaciones CRUD
import androidx.room.*
// import: Importa la clase de entidad MantenimientoEntity que modela la tabla 'mantenimientos'
import co.edu.ue.data.local.entity.MantenimientoEntity
// import: Trae Flow para emitir la lista de reportes en tiempo real
import kotlinx.coroutines.flow.Flow

// @Dao: Interfaz que expone las consultas SQL para los reportes de daños y evidencias fotográficas
@Dao
interface MantenimientoDao {

    // @Query("SELECT * FROM mantenimientos ORDER BY id DESC"): Retorna todos los reportes ordenados del más reciente al más antiguo
    // Flow<List<MantenimientoEntity>>: Emisión reactiva que redibuja la lista al tomar una foto o registrar un daño
    @Query("SELECT * FROM mantenimientos ORDER BY id DESC")
    fun obtenerTodosLosMantenimientos(): Flow<List<MantenimientoEntity>>

    // @Query("SELECT COUNT(*) FROM mantenimientos"): Cuenta el total de reportes para el contador del Dashboard
    @Query("SELECT COUNT(*) FROM mantenimientos")
    suspend fun contarMantenimientos(): Int

    // @Insert(onConflict = OnConflictStrategy.REPLACE): Inserta un nuevo reporte de daño con su respectiva ruta de foto
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarMantenimiento(mantenimiento: MantenimientoEntity): Long

    // @Update: Actualiza el estado de la reparación ("Pendiente", "En Proceso", "Resuelto") o la sincronización
    @Update
    suspend fun actualizarMantenimiento(mantenimiento: MantenimientoEntity)

    // @Delete: Elimina una incidencia resuelta de la base de datos SQLite
    @Delete
    suspend fun eliminarMantenimiento(mantenimiento: MantenimientoEntity)
}