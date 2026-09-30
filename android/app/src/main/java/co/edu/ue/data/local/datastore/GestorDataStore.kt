// package: Nivel 3 de Almacenamiento Móvil - Jetpack DataStore Preferences (Asíncrono y Reactivo)
package co.edu.ue.data.local.datastore

// import: Trae el Contexto de Android para acoplar la instancia de almacenamiento a la aplicación
import android.content.Context
// import: Trae la interfaz base DataStore de Jetpack
import androidx.datastore.core.DataStore
// import: Trae la clase Preferences para tipado de propiedades clave-valor
import androidx.datastore.preferences.core.Preferences
// import: Constructor de claves booleanas en DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
// import: Función de extensión edit para realizar escrituras transaccionales asíncronas
import androidx.datastore.preferences.core.edit
// import: Constructor de claves de cadena de texto en DataStore
import androidx.datastore.preferences.core.stringPreferencesKey
// import: Delegado de Kotlin preferencesDataStore para instanciación segura y lazy
import androidx.datastore.preferences.preferencesDataStore
// import: Trae Flow reactivo para observar cambios de estado en tiempo real
import kotlinx.coroutines.flow.Flow
// import: Operador map para transformar los Preferences en tipos de datos primitivos (Boolean, String)
import kotlinx.coroutines.flow.map

// Extensión sobre Context para crear un Singleton thread-safe de DataStore llamado "arrendo_datastore_settings"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "arrendo_datastore_settings")

// class GestorDataStore: Capa de persistencia moderna para banderas de configuración y fechas de sincronización
class GestorDataStore(private val contexto: Context) {

    // companion object: Define las claves estáticas fuertemente tipadas de DataStore
    companion object {
        // Clave booleana para recordar la preferencia de Modo Oscuro de la app
        val CLAVE_MODO_OSCURO = booleanPreferencesKey("modo_oscuro_activo")
        // Clave booleana para recordar el estado de notificaciones
        val CLAVE_NOTIFICACIONES = booleanPreferencesKey("notificaciones_activas")
        // Clave de texto para almacenar la fecha ISO o timestamp de la última sincronización con Spring Boot
        val CLAVE_ULTIMA_SINCRONIZACION = stringPreferencesKey("fecha_ultima_sincronizacion")
    }

    // modoOscuroFlujo: Flujo Flow reactivo que emite 'true' o 'false' cada vez que el usuario cambia la configuración
    val modoOscuroFlujo: Flow<Boolean> = contexto.dataStore.data.map { preferencias ->
        preferencias[CLAVE_MODO_OSCURO] ?: false
    }

    // fun guardarModoOscuro: Función de suspensión que escribe la preferencia en disco de forma no bloqueante
    suspend fun guardarModoOscuro(activo: Boolean) {
        contexto.dataStore.edit { preferencias ->
            preferencias[CLAVE_MODO_OSCURO] = activo
        }
    }

    // fun registrarFechaSincronizacion: Guarda la fecha y hora exacta del último intercambio de datos exitoso
    suspend fun registrarFechaSincronizacion(fecha: String) {
        contexto.dataStore.edit { preferencias ->
            preferencias[CLAVE_ULTIMA_SINCRONIZACION] = fecha
        }
    }
}