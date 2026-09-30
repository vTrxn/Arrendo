// package: Define el paquete lógico dentro del proyecto Android donde reside la base de datos Room
package co.edu.ue.data.local.database

// import: Importa la clase Context de Android para acceder al contexto del sistema operativo y almacenamiento
import android.content.Context
// import: Importa la anotación @Database de Room para registrar tablas y versión
import androidx.room.Database
// import: Importa la clase Room para instanciar el constructor de base de datos SQLite
import androidx.room.Room
// import: Importa RoomDatabase, la clase base abstracta obligatoria de la biblioteca Room
import androidx.room.RoomDatabase
// import: Importa todos los interfaces DAO (Data Access Object) locales
import co.edu.ue.data.local.dao.*
// import: Importa todas las clases de entidades (@Entity) que representan las tablas en SQLite
import co.edu.ue.data.local.entity.*

// @Database: Anotación obligatoria de Room que marca la clase como la base de datos principal de la aplicación
// entities: Arreglo que lista exhaustivamente cada una de las 5 entidades que se convertirán en tablas SQLite
// version = 3: Número de versión del esquema de la base de datos para control de migraciones
// exportSchema = false: Deshabilita la exportación del esquema en formato JSON a carpetas externas
@Database(
    entities = [
        UsuarioEntity::class,       // Tabla 'usuarios': almacena sesión activa, username y token JWT local
        InmuebleEntity::class,      // Tabla 'inmuebles': almacena direcciones, tipos, precios y estados
        ArrendatarioEntity::class,  // Tabla 'arrendatarios': almacena datos de contacto de inquilinos
        ContratoEntity::class,      // Tabla 'contratos': almacena acuerdos de arriendo y estados de pago
        MantenimientoEntity::class  // Tabla 'mantenimientos': almacena reportes de daños, fotos y fechas
    ],
    version = 3,
    exportSchema = false
)
// abstract class: Declara una clase abstracta que extiende de RoomDatabase; Room generará la implementación real en tiempo de compilación
abstract class BaseDeDatosApp : RoomDatabase() {

    // Métodos abstractos que exponen cada uno de los 5 DAOs para realizar consultas SQL a la base de datos
    abstract fun usuarioDao(): UsuarioDao
    abstract fun inmuebleDao(): InmuebleDao
    abstract fun arrendatarioDao(): ArrendatarioDao
    abstract fun contratoDao(): ContratoDao
    abstract fun mantenimientoDao(): MantenimientoDao

    // companion object: Bloque de miembros estáticos para implementar el patrón de diseño Singleton
    companion object {
        // @Volatile: Garantiza que cualquier cambio en la variable INSTANCIA sea visible inmediatamente para todos los hilos del CPU
        @Volatile
        private var INSTANCIA: BaseDeDatosApp? = null

        // fun obtenerBaseDatos: Método estático seguro para hilos (Thread-Safe) que retorna la instancia única de Room
        fun obtenerBaseDatos(contexto: Context): BaseDeDatosApp {
            // El operador elvis (?:) verifica si INSTANCIA ya existe; si es null, entra al bloque sincronizado
            return INSTANCIA ?: synchronized(this) {
                // Room.databaseBuilder: Crea y configura la base de datos SQLite en el almacenamiento interno privado de la app
                val instancia = Room.databaseBuilder(
                    contexto.applicationContext,   // Usa el ApplicationContext para evitar fugas de memoria (Memory Leaks)
                    BaseDeDatosApp::class.java,    // Pasa la clase abstracta de la base de datos
                    "arrendo_sqlite_v3"            // Nombre físico del archivo SQLite en el dispositivo (/data/data/co.edu.ue/databases/)
                )
                // fallbackToDestructiveMigration: Si la versión cambia sin migración definida, destruye y recrea las tablas limpiamente
                .fallbackToDestructiveMigration()
                // build: Compila y construye la instancia de la base de datos Room
                .build()
                // Asigna la instancia a la variable estática Singleton
                INSTANCIA = instancia
                // Retorna la instancia construida
                instancia
            }
        }
    }
}