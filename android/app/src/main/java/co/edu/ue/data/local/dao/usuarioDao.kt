// package: Palabra clave que declara el paquete lógico para las interfaces DAO de Room
package co.edu.ue.data.local.dao

// import: Trae las anotaciones de consulta e inserción de la librería Room y la entidad UsuarioEntity
import androidx.room.*
import co.edu.ue.data.local.entity.UsuarioEntity

// @Dao: Anotación de Room que significa "Data Access Object". Indica que esta interfaz contiene las consultas de base de datos
// interface: Palabra clave de Kotlin para declarar una interfaz (un contrato de métodos sin cuerpo que Room implementará automáticamente en código ejecutables)
@Dao
interface UsuarioDao {

    // @Query: Anotación de Room que ejecuta una consulta SQL escrita manualmente en SQLite
    // "SELECT * FROM usuarios WHERE estaLogueado = 1 LIMIT 1": Instrucción SQL que selecciona la primera fila de la tabla usuarios cuyo campo estaLogueado sea igual a 1 (verdadero)
    // suspend: Palabra clave de Kotlin que indica que esta función se ejecuta de forma asíncrona mediante una Corrutina, evitando congelar la interfaz de la app
    // fun: Palabra clave de Kotlin para definir o declarar una función (método)
    // obtenerUsuarioLogueado(): Nombre de la función declarada
    // : UsuarioEntity?: Tipo de dato que retorna la función (devuelve el objeto UsuarioEntity o 'null' si no hay ningún usuario con sesión activa)
    @Query("SELECT * FROM usuarios WHERE estaLogueado = 1 LIMIT 1")
    suspend fun obtenerUsuarioLogueado(): UsuarioEntity?

    // @Insert: Anotación de Room que genera una consulta SQL de inserción 'INSERT INTO'
    // onConflict: Parámetro que especifica qué hacer en caso de conflicto de clave primaria
    // OnConflictStrategy.REPLACE: Estrategia que indica "Reemplazar" los datos existentes si la clave primaria ya se encuentra guardada
    // (usuario: UsuarioEntity): Parámetro de entrada que recibe el objeto UsuarioEntity con los datos a insertar
    // : Long: Tipo de retorno que devuelve el ID numérico asignado a la fila recién insertada
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: UsuarioEntity): Long

    // @Query: Anotación para ejecutar la instrucción SQL de actualización 'UPDATE usuarios SET estaLogueado = 0'
    // Esta consulta cambia la columna 'estaLogueado' a 0 (falso) para todos los usuarios, cerrando cualquier sesión activa
    @Query("UPDATE usuarios SET estaLogueado = 0")
    suspend fun cerrarTodasLasSesiones()
}