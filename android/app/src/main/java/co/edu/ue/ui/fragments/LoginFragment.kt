// package: Ubicación de los Fragmentos de pantalla
package co.edu.ue.ui.fragments

// import: Trae componentes de Android, ViewBinding, Volley, Room, SharedPreferences, DataStore y Corrutinas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import co.edu.ue.MainActivity
import co.edu.ue.R
import co.edu.ue.data.local.database.BaseDeDatosApp
import co.edu.ue.data.local.datastore.GestorDataStore
import co.edu.ue.data.local.entity.UsuarioEntity
import co.edu.ue.data.local.preferences.GestorPreferencias
import co.edu.ue.data.remote.api.ClienteVolley
import co.edu.ue.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch
import org.json.JSONObject

// class LoginFragment: Fragmento para el inicio de sesión y autenticación con Tokens JWT (RF01)
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val contextoApp = requireContext().applicationContext
        val baseDatos = BaseDeDatosApp.obtenerBaseDatos(contextoApp)
        val usuarioDao = baseDatos.usuarioDao()
        val clienteVolley = ClienteVolley.obtenerInstancia(contextoApp)

        val gestorPreferencias = GestorPreferencias(contextoApp)
        val gestorDataStore = GestorDataStore(contextoApp)

        binding.btnIngresar.setOnClickListener {
            val usuarioText = binding.etUsuario.text.toString().trim()
            val claveText = binding.etClave.text.toString().trim()

            if (usuarioText.isEmpty() || claveText.isEmpty()) {
                binding.tvErrorLogin.visibility = View.VISIBLE
                binding.tvErrorLogin.text = "Ingrese usuario y contraseña"
                return@setOnClickListener
            }

            binding.btnIngresar.isEnabled = false
            binding.tvErrorLogin.visibility = View.GONE

            val cuerpoJson = JSONObject().apply {
                put("usuario", usuarioText)
                put("clave", claveText)
            }

            // Petición POST de autenticación JWT enviada al servidor Spring Boot usando Volley
            // URL completa invocada: http://10.0.2.2:8080/api/v1/auth/login
            clienteVolley.ejecutarPeticionPostJson(
                urlRelativa = "v1/auth/login",
                cuerpoJson = cuerpoJson,
                alRecibirRespuesta = { respuestaJson ->
                    val token = respuestaJson.optString("token", "")
                    if (token.isNotEmpty()) {
                        ClienteVolley.tokenJwtGuardado = token
                        gestorPreferencias.guardarSesionUsuario(usuarioText, token)

                        viewLifecycleOwner.lifecycleScope.launch {
                            gestorDataStore.registrarFechaSincronizacion(System.currentTimeMillis().toString())
                            usuarioDao.cerrarTodasLasSesiones()
                            usuarioDao.insertarUsuario(
                                UsuarioEntity(
                                    nombreUsuario = usuarioText,
                                    tokenAcceso = token,
                                    estaLogueado = true
                                )
                            )
                            completarInicioSesionExitoso()
                        }
                    } else {
                        mostrarErrorLogin("Credenciales incorrectas")
                    }
                },
                alOcurrirError = { _ ->
                    // Modo Offline Fallback si el servidor no está en línea
                    if (usuarioText.equals("admin", ignoreCase = true) && claveText == "admin123") {
                        val tokenOffline = "offline_jwt_token"
                        ClienteVolley.tokenJwtGuardado = tokenOffline
                        gestorPreferencias.guardarSesionUsuario(usuarioText, tokenOffline)

                        viewLifecycleOwner.lifecycleScope.launch {
                            gestorDataStore.registrarFechaSincronizacion(System.currentTimeMillis().toString())
                            usuarioDao.cerrarTodasLasSesiones()
                            usuarioDao.insertarUsuario(
                                UsuarioEntity(
                                    nombreUsuario = usuarioText,
                                    tokenAcceso = tokenOffline,
                                    estaLogueado = true
                                )
                            )
                            completarInicioSesionExitoso()
                        }
                    } else {
                        mostrarErrorLogin("Usuario o contraseña incorrectos")
                    }
                }
            )
        }
    }

    private fun mostrarErrorLogin(mensaje: String) {
        if (_binding != null) {
            binding.btnIngresar.isEnabled = true
            binding.tvErrorLogin.visibility = View.VISIBLE
            binding.tvErrorLogin.text = mensaje
        }
    }

    private fun completarInicioSesionExitoso() {
        if (_binding != null) {
            binding.btnIngresar.isEnabled = true
            (activity as? MainActivity)?.volverAlDashboard()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}