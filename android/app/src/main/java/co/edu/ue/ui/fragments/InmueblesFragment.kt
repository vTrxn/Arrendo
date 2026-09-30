// package: Ubicación del Fragmento de Inmuebles
package co.edu.ue.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import co.edu.ue.MainActivity
import co.edu.ue.data.local.entity.InmuebleEntity
import co.edu.ue.data.remote.api.ClienteVolley
import co.edu.ue.databinding.FragmentInmueblesBinding
import co.edu.ue.ui.adapters.InmuebleAdapter
import org.json.JSONObject

// class InmueblesFragment: Fragmento para la gestión de inmuebles con peticiones directas a la API REST de Spring Boot / PostgreSQL (RF02)
class InmueblesFragment : Fragment() {

    private var _binding: FragmentInmueblesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adaptador: InmuebleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInmueblesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val clienteVolley = ClienteVolley.obtenerInstancia(requireContext().applicationContext)

        // Configuración del botón ELIMINAR directamente mediante petición HTTP DELETE con Volley
        adaptador = InmuebleAdapter { inmuebleABorrar ->
            if (inmuebleABorrar.id > 0) {
                clienteVolley.ejecutarPeticionDelete(
                    urlRelativa = "v1/properties/${inmuebleABorrar.id}",
                    alRecibirRespuesta = { _ ->
                        Toast.makeText(context, "Inmueble eliminado de PostgreSQL", Toast.LENGTH_SHORT).show()
                        cargarInmueblesDesdeApi(clienteVolley) // Refresca la lista desde la API REST
                    },
                    alOcurrirError = { msg ->
                        Toast.makeText(context, "Error al eliminar: $msg", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        binding.rvInmuebles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvInmuebles.adapter = adaptador

        // Carga inicial directa de propiedades desde la API REST al abrir el módulo
        cargarInmueblesDesdeApi(clienteVolley)

        // Botón GUARDAR: Envía una petición HTTP POST directa a Spring Boot / PostgreSQL
        binding.btnGuardarInmueble.setOnClickListener {
            val direccion = binding.etDireccion.text.toString()
            val tipo = binding.etTipoInmueble.text.toString()
            val precio = binding.etPrecio.text.toString().toDoubleOrNull() ?: 0.0
            val estado = binding.etEstado.text.toString()

            if (direccion.isNotBlank()) {
                val cuerpoJson = JSONObject().apply {
                    put("direccion", direccion)
                    put("tipoInmueble", if (tipo.isBlank()) "Apartamento" else tipo)
                    put("precioArriendo", precio)
                    put("estado", if (estado.isBlank()) "Disponible" else estado)
                    put("uriImagen", "")
                }

                clienteVolley.ejecutarPeticionPostJson(
                    urlRelativa = "v1/properties",
                    cuerpoJson = cuerpoJson,
                    alRecibirRespuesta = { _ ->
                        Toast.makeText(context, "Inmueble guardado en PostgreSQL", Toast.LENGTH_SHORT).show()
                        binding.etDireccion.text.clear()
                        binding.etTipoInmueble.text.clear()
                        binding.etPrecio.text.clear()
                        binding.etEstado.text.clear()
                        cargarInmueblesDesdeApi(clienteVolley) // Refresca la lista desde la API
                    },
                    alOcurrirError = { msg ->
                        Toast.makeText(context, "Error al guardar en el servidor: $msg", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        binding.btnVolverInicio.setOnClickListener {
            (activity as? MainActivity)?.volverAlDashboard()
        }
    }

    // fun cargarInmueblesDesdeApi: Realiza una petición GET directa con Volley a http://10.0.2.2:8080/api/v1/properties
    private fun cargarInmueblesDesdeApi(clienteVolley: ClienteVolley) {
        clienteVolley.ejecutarPeticionGetJsonArray(
            urlRelativa = "v1/properties",
            alRecibirLista = { arregloJson ->
                val listaInmuebles = mutableListOf<InmuebleEntity>()
                for (i in 0 until arregloJson.length()) {
                    val obj = arregloJson.getJSONObject(i)
                    val idRemoto = obj.optLong("id", 0L)
                    val dir = obj.optString("direccion", "")
                    val tipo = obj.optString("tipoInmueble", "Apartamento")
                    val precio = obj.optDouble("precioArriendo", 0.0)
                    val est = obj.optString("estado", "Disponible")

                    if (dir.isNotEmpty()) {
                        listaInmuebles.add(
                            InmuebleEntity(
                                id = idRemoto,
                                direccion = dir,
                                tipoInmueble = tipo,
                                precioArriendo = precio,
                                estado = est,
                                estaSincronizado = true
                            )
                        )
                    }
                }
                if (_binding != null) {
                    adaptador.actualizarLista(listaInmuebles)
                }
            },
            alOcurrirError = { msg ->
                if (_binding != null) {
                    Toast.makeText(context, "Servidor offline: $msg", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}