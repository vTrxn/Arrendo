// package: Paquete del DAO de contratos y cobros en la base de datos local SQLite
package co.edu.ue.data.local.dao

// import: Trae anotaciones de persistencia Room (@Dao, @Query, @Insert, etc.)
import androidx.room.*
// import: Importa la clase de entidad ContratoEntity que modela la tabla 'contratos'
import co.edu.ue.data.local.entity.ContratoEntity
// import: Importa la interfaz reactiva Flow de Kotlin Coroutines
import kotlinx.coroutines.flow.Flow

// @Dao: Define el contrato de métodos SQL para la gestión de contratos de arriendo
@Dao
interface ContratoDao {

    // @Query("SELECT * FROM contratos ORDER BY id DESC"): Retorna todos los contratos ordenados del más reciente al más antiguo
    // Flow<List<ContratoEntity>>: Emite de forma reactiva cualquier cambio a la interfaz de usuario
    @Query("SELECT * FROM contratos ORDER BY id DESC")
    fun obtenerTodosLosContratos(): Flow<List<ContratoEntity>>

    // @Query("SELECT COUNT(*) FROM contratos"): Cuenta el total de contratos activos para visualización en el Dashboard
    @Query("SELECT COUNT(*) FROM contratos")
    suspend fun contarContratos(): Int

    // @Insert(onConflict = OnConflictStrategy.REPLACE): Inserta un contrato nuevo o reemplaza en caso de clave primaria duplicada
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarContrato(contrato: ContratoEntity): Long

    // @Update: Actualiza las condiciones o el estado de pago del contrato ("Al dia", "Pendiente", "Moroso")
    @Update
    suspend fun actualizarContrato(contrato: ContratoEntity)

    // @Delete: Elimina de forma permanente un contrato de la base de datos local
    @Delete
    suspend fun eliminarContrato(contrato: ContratoEntity)
}