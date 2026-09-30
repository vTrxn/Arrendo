// package: Nivel 2 de Almacenamiento Móvil - SharedPreferences (Clave-Valor en memoria Flash/XML)
package co.edu.ue.data.local.preferences

// import: Trae la clase Context para acceder al almacenamiento de preferencias del sistema Android
import android.content.Context
// import: Trae SharedPreferences para leer y escribir parejas clave-valor de acceso ultra rápido
import android.content.SharedPreferences

// class GestorPreferencias: Administrador de sesión y configuración en memoria local con tiempo de acceso O(1)
class GestorPreferencias(contexto: Context) {

    // PREF_NAME: Nombre del archivo físico XML privado ubicado en /data/data/co.edu.ue/shared_prefs/arrendo_pref_session.xml
    private val PREF_NAME = "arrendo_pref_session"
    // Clave de texto que identifica el nombre del usuario activo
    private val KEY_USUARIO = "usuario_activo"
    // Clave de texto que identifica el Token JWT de autenticación recibido del backend
    private val KEY_TOKEN_JWT = "jwt_token_sesion"
    // Clave numérica para guardar la marca temporal (timestamp en milisegundos) del último inicio de sesión
    private val KEY_ULTIMO_INGRESO = "ultimo_ingreso_timestamp"

    // preferencias: Instancia de SharedPreferences configurada en modo MODE_PRIVATE (acceso exclusivo para Arrendo)
    private val preferencias: SharedPreferences = contexto.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // fun guardarSesionUsuario: Guarda de forma atómica el nombre de usuario, el Token JWT y el timestamp actual
    fun guardarSesionUsuario(nombreUsuario: String, tokenJwt: String) {
        // editor: Abre el editor de SharedPreferences en modo transacción
        val editor = preferencias.edit()
        // Inserta la pareja clave-valor del nombre de usuario
        editor.putString(KEY_USUARIO, nombreUsuario)
        // Inserta la pareja clave-valor del Token JWT
        editor.putString(KEY_TOKEN_JWT, tokenJwt)
        // Inserta la marca de tiempo en milisegundos
        editor.putLong(KEY_ULTIMO_INGRESO, System.currentTimeMillis())
        // apply(): Aplica los cambios de forma asíncrona en segundo plano sin congelar la interfaz gráfica
        editor.apply()
    }

    // fun obtenerUsuarioActivo: Retorna el nombre del usuario guardado en disco o null si no hay sesión
    fun obtenerUsuarioActivo(): String? {
        return preferencias.getString(KEY_USUARIO, null)
    }

    // fun obtenerTokenJwt: Retorna la clave digital JWT para inyectarla en las cabeceras HTTP de Volley
    fun obtenerTokenJwt(): String? {
        return preferencias.getString(KEY_TOKEN_JWT, null)
    }

    // fun cerrarSesion: Limpia y borra todas las claves del archivo XML de preferencias al pulsar Salir
    fun cerrarSesion() {
        preferencias.edit().clear().apply()
    }
}