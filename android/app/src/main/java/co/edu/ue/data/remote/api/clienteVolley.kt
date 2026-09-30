// package: Palabra clave de Kotlin que declara la carpeta y paquete lógico donde se ubica este archivo de red Volley
package co.edu.ue.data.remote.api

// import: Trae las clases de la biblioteca oficial Volley de Google (com.android.volley) y Android Context
import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

// class ClienteVolley: Clase Singleton en Kotlin que administra la cola de peticiones HTTP Volley (RequestQueue) para toda la app
// constructor privado para implementar el patrón de diseño Singleton
class ClienteVolley private constructor(contexto: Context) {

    companion object {
        // Variable con la URL base del backend Spring Boot.
        // - Para Emulador: "http://10.0.2.2:8080/api/"
        // - Para Celular Físico por cable USB (adb reverse): "http://localhost:8080/api/"
        // - Para WiFi: Se puede sobreescribir con "http://<IP_PC>:8080/api/"
        var URL_BASE: String = if (android.os.Build.FINGERPRINT.contains("generic")
            || android.os.Build.MODEL.contains("google_sdk")
            || android.os.Build.MODEL.contains("Emulator")
            || android.os.Build.HARDWARE.contains("goldfish")
            || android.os.Build.HARDWARE.contains("ranchu")
        ) {
            "http://10.0.2.2:8080/api/"
        } else {
            "http://localhost:8080/api/"
        }

        // Variable estática para guardar el Token JWT cuando el usuario inicia sesión
        var tokenJwtGuardado: String? = null

        @Volatile
        private var INSTANCIA: ClienteVolley? = null

        fun obtenerInstancia(contexto: Context): ClienteVolley {
            return INSTANCIA ?: synchronized(this) {
                val nuevaInstancia = ClienteVolley(contexto)
                INSTANCIA = nuevaInstancia
                nuevaInstancia
            }
        }
    }

    val colaPeticiones: RequestQueue by lazy {
        Volley.newRequestQueue(contexto.applicationContext)
    }

    // fun ejecutarPeticionPostJson: Envía un objeto JSON (JSONObject) mediante petición HTTP POST para crear registros
    fun ejecutarPeticionPostJson(
        urlRelativa: String,
        cuerpoJson: JSONObject?,
        alRecibirRespuesta: (JSONObject) -> Unit,
        alOcurrirError: (String) -> Unit
    ) {
        val urlCompleta = URL_BASE + urlRelativa

        val solicitud = object : JsonObjectRequest(
            Request.Method.POST,
            urlCompleta,
            cuerpoJson,
            Response.Listener<JSONObject> { respuestaJson ->
                alRecibirRespuesta(respuestaJson)
            },
            Response.ErrorListener { errorVolley ->
                val mensaje = errorVolley.message ?: "Error al conectar con el servidor Spring Boot en $urlCompleta"
                alOcurrirError(mensaje)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val cabeceras = HashMap<String, String>()
                cabeceras["Content-Type"] = "application/json"
                tokenJwtGuardado?.let { token ->
                    if (token.isNotEmpty()) {
                        cabeceras["Authorization"] = "Bearer $token"
                    }
                }
                return cabeceras
            }
        }

        colaPeticiones.add(solicitud)
    }

    // fun ejecutarPeticionPutJson: Envía un objeto JSON (JSONObject) mediante petición HTTP PUT para actualizar registros
    fun ejecutarPeticionPutJson(
        urlRelativa: String,
        cuerpoJson: JSONObject?,
        alRecibirRespuesta: (JSONObject) -> Unit,
        alOcurrirError: (String) -> Unit
    ) {
        val urlCompleta = URL_BASE + urlRelativa

        val solicitud = object : JsonObjectRequest(
            Request.Method.PUT,
            urlCompleta,
            cuerpoJson,
            Response.Listener<JSONObject> { respuestaJson ->
                alRecibirRespuesta(respuestaJson)
            },
            Response.ErrorListener { errorVolley ->
                val mensaje = errorVolley.message ?: "Error al actualizar en el servidor Spring Boot"
                alOcurrirError(mensaje)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val cabeceras = HashMap<String, String>()
                cabeceras["Content-Type"] = "application/json"
                tokenJwtGuardado?.let { token ->
                    if (token.isNotEmpty()) {
                        cabeceras["Authorization"] = "Bearer $token"
                    }
                }
                return cabeceras
            }
        }

        colaPeticiones.add(solicitud)
    }

    // fun ejecutarPeticionDelete: Ejecuta una solicitud HTTP DELETE para borrar registros en Spring Boot / PostgreSQL
    fun ejecutarPeticionDelete(
        urlRelativa: String,
        alRecibirRespuesta: (String) -> Unit,
        alOcurrirError: (String) -> Unit
    ) {
        val urlCompleta = URL_BASE + urlRelativa

        val solicitud = object : StringRequest(
            Request.Method.DELETE,
            urlCompleta,
            Response.Listener<String> { respuestaTexto ->
                alRecibirRespuesta(respuestaTexto)
            },
            Response.ErrorListener { errorVolley ->
                val mensaje = errorVolley.message ?: "Error al borrar registro en el servidor Spring Boot"
                alOcurrirError(mensaje)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val cabeceras = HashMap<String, String>()
                cabeceras["Content-Type"] = "application/json"
                tokenJwtGuardado?.let { token ->
                    if (token.isNotEmpty()) {
                        cabeceras["Authorization"] = "Bearer $token"
                    }
                }
                return cabeceras
            }
        }

        colaPeticiones.add(solicitud)
    }

    // fun ejecutarPeticionGetJsonArray: Realiza solicitudes HTTP GET que devuelven un arreglo JSON [...]
    fun ejecutarPeticionGetJsonArray(
        urlRelativa: String,
        alRecibirLista: (JSONArray) -> Unit,
        alOcurrirError: (String) -> Unit
    ) {
        val urlCompleta = URL_BASE + urlRelativa

        val solicitud = object : JsonArrayRequest(
            Request.Method.GET,
            urlCompleta,
            null,
            Response.Listener<JSONArray> { arregloJson ->
                alRecibirLista(arregloJson)
            },
            Response.ErrorListener { errorVolley ->
                val mensaje = errorVolley.message ?: "No se pudo conectar con el servidor backend Spring Boot"
                alOcurrirError(mensaje)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val cabeceras = HashMap<String, String>()
                cabeceras["Content-Type"] = "application/json"
                tokenJwtGuardado?.let { token ->
                    if (token.isNotEmpty()) {
                        cabeceras["Authorization"] = "Bearer $token"
                    }
                }
                return cabeceras
            }
        }

        colaPeticiones.add(solicitud)
    }
}