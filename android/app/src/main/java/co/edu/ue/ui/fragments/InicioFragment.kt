// package: Ubicación de los Fragmentos de pantalla en Kotlin
package co.edu.ue.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import co.edu.ue.MainActivity
import co.edu.ue.data.remote.api.ClienteVolley
import co.edu.ue.databinding.FragmentInicioBinding

// class InicioFragment: Fragmento que representa la pantalla principal Dashboard (RF08)
class InicioFragment : Fragment() {

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val clienteVolley = ClienteVolley.obtenerInstancia(requireContext().applicationContext)

        // Carga de contadores directa desde la API REST de Spring Boot / PostgreSQL
        actualizarContadoresDesdeApi(clienteVolley)

        binding.btnIrInmuebles.setOnClickListener {
            (activity as? MainActivity)?.cargarModuloFragment(InmueblesFragment())
        }

        binding.btnIrArrendatarios.setOnClickListener {
            (activity as? MainActivity)?.cargarModuloFragment(ArrendatariosFragment())
        }

        binding.btnIrContratos.setOnClickListener {
            (activity as? MainActivity)?.cargarModuloFragment(ContratosFragment())
        }

        binding.btnIrMantenimientos.setOnClickListener {
            (activity as? MainActivity)?.cargarModuloFragment(MantenimientosFragment())
        }
    }

    private fun actualizarContadoresDesdeApi(clienteVolley: ClienteVolley) {
        clienteVolley.ejecutarPeticionGetJsonArray("v1/properties", { json ->
            if (_binding != null) binding.tvInmueblesCount.text = "- Inmuebles registrados: ${json.length()}"
        }, {})

        clienteVolley.ejecutarPeticionGetJsonArray("v1/tenants", { json ->
            if (_binding != null) binding.tvArrendatariosCount.text = "- Inquilinos registrados: ${json.length()}"
        }, {})

        clienteVolley.ejecutarPeticionGetJsonArray("v1/contracts", { json ->
            if (_binding != null) binding.tvContratosCount.text = "- Contratos activos: ${json.length()}"
        }, {})

        clienteVolley.ejecutarPeticionGetJsonArray("v1/maintenances", { json ->
            if (_binding != null) binding.tvMantenimientosCount.text = "- Mantenimientos/Daños: ${json.length()}"
        }, {})
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}