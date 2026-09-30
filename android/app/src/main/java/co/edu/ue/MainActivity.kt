// package: Paquete principal de la aplicación Android
package co.edu.ue

// import: Trae componentes de actividad de Android, ViewBinding, FragmentManager, SharedPreferences y Volley
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import co.edu.ue.data.local.preferences.GestorPreferencias
import co.edu.ue.data.remote.api.ClienteVolley
import co.edu.ue.databinding.ActivityMainBinding
import co.edu.ue.ui.fragments.*

// class MainActivity: Actividad principal que gestiona el ciclo de vida, verificación de sesión y navegación
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verificación de sesión en SharedPreferences al abrir la aplicación
        verificarSesionActiva()

        binding.btnIrInmuebles.setOnClickListener {
            cargarModuloFragment(InmueblesFragment())
        }

        binding.btnIrArrendatarios.setOnClickListener {
            cargarModuloFragment(ArrendatariosFragment())
        }

        binding.btnIrContratos.setOnClickListener {
            cargarModuloFragment(ContratosFragment())
        }

        binding.btnIrMantenimientos.setOnClickListener {
            cargarModuloFragment(MantenimientosFragment())
        }

        binding.btnCerrarSesion.setOnClickListener {
            cerrarSesionUsuario()
        }

        // Listener de la pila de retroceso nativa de Android
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0 && ClienteVolley.tokenJwtGuardado != null) {
                binding.llDashboardMain.visibility = View.VISIBLE
                binding.fragmentContainer.visibility = View.GONE
                actualizarContadoresDashboard()
            }
        }
    }

    // fun volverAlDashboard: Restaura la vista del Dashboard principal y actualiza los contadores desde la API
    fun volverAlDashboard() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        binding.llDashboardMain.visibility = View.VISIBLE
        binding.fragmentContainer.visibility = View.GONE
        actualizarContadoresDashboard()
    }

    // fun verificarSesionActiva: Consulta si existe un Token JWT persistido en SharedPreferences
    private fun verificarSesionActiva() {
        val gestorPreferencias = GestorPreferencias(applicationContext)
        val tokenPref = gestorPreferencias.obtenerTokenJwt()

        if (!tokenPref.isNullOrEmpty()) {
            ClienteVolley.tokenJwtGuardado = tokenPref
            volverAlDashboard()
        } else {
            mostrarPantallaLogin()
        }
    }

    // fun mostrarPantallaLogin: Carga el fragmento de Login como pantalla principal sin agregarlo a la pila
    fun mostrarPantallaLogin() {
        binding.llDashboardMain.visibility = View.GONE
        binding.fragmentContainer.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, LoginFragment())
            .commit()
    }

    // fun cerrarSesionUsuario: Limpia credenciales locales y redirige a LoginFragment
    private fun cerrarSesionUsuario() {
        val gestorPreferencias = GestorPreferencias(applicationContext)
        gestorPreferencias.cerrarSesion()
        ClienteVolley.tokenJwtGuardado = null
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        mostrarPantallaLogin()
    }

    // fun cargarModuloFragment: Infla un fragmento modular dentro del contenedor a pantalla completa
    fun cargarModuloFragment(fragmento: Fragment) {
        binding.llDashboardMain.visibility = View.GONE
        binding.fragmentContainer.visibility = View.VISIBLE

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragmento)
            .addToBackStack(null)
            .commit()
    }

    // fun actualizarContadoresDashboard: Realiza peticiones GET a la API REST de Spring Boot para actualizar las estadísticas
    fun actualizarContadoresDashboard() {
        val clienteVolley = ClienteVolley.obtenerInstancia(applicationContext)

        clienteVolley.ejecutarPeticionGetJsonArray("v1/properties", { json ->
            binding.tvInmueblesCount.text = "- Inmuebles registrados: ${json.length()}"
        }, {})

        clienteVolley.ejecutarPeticionGetJsonArray("v1/tenants", { json ->
            binding.tvArrendatariosCount.text = "- Inquilinos registrados: ${json.length()}"
        }, {})

        clienteVolley.ejecutarPeticionGetJsonArray("v1/contracts", { json ->
            binding.tvContratosCount.text = "- Contratos activos: ${json.length()}"
        }, {})

        clienteVolley.ejecutarPeticionGetJsonArray("v1/maintenances", { json ->
            binding.tvMantenimientosCount.text = "- Mantenimientos/Daños: ${json.length()}"
        }, {})
    }
}