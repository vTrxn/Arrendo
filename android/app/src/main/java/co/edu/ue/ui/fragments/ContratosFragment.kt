// package: Ubicación del Fragmento de Contratos
package co.edu.ue.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import co.edu.ue.MainActivity
import co.edu.ue.data.local.entity.ContratoEntity
import co.edu.ue.data.remote.api.ClienteVolley
import co.edu.ue.databinding.FragmentContratosBinding
import co.edu.ue.ui.adapters.ContratoAdapter
import org.json.JSONObject

// class ContratosFragment: Fragmento para la gestión de contratos con peticiones directas a la API REST de Spring Boot / PostgreSQL (RF04)
class ContratosFragment : Fragment() {

    private var _binding: FragmentContratosBinding? = null
    private val binding get() = _binding!!
    private lateinit var adaptador: ContratoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContratosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val clienteVolley = ClienteVolley.obtenerInstancia(requireContext().applicationContext)

        adaptador = ContratoAdapter(
            alPagar = { contrato ->
                val cuerpoJson = JSONObject().apply {
                    put("idInmueble", contrato.idInmueble)
                    put("idArrendatario", contrato.idArrendatario)
                    put("fechaInicio", contrato.fechaInicio)
                    put("fechaFin", contrato.fechaFin)
                    put("canonMensual", contrato.canonMensual)
                    put("estadoPago", "Al dia")
                }

                // Petición HTTP PUT directa a la API REST
                clienteVolley.ejecutarPeticionPutJson(
                    urlRelativa = "v1/contracts/${contrato.id}",
                    cuerpoJson = cuerpoJson,
                    alRecibirRespuesta = { _ ->
                        Toast.makeText(context, "Estado de pago actualizado en PostgreSQL", Toast.LENGTH_SHORT).show()
                        cargarContratosDesdeApi(clienteVolley)
                    },
                    alOcurrirError = { msg ->
                        Toast.makeText(context, "Error al actualizar: $msg", Toast.LENGTH_SHORT).show()
                    }
                )
            },
            alEliminar = { contratoABorrar ->
                if (contratoABorrar.id > 0) {
                    clienteVolley.ejecutarPeticionDelete(
                        urlRelativa = "v1/contracts/${contratoABorrar.id}",
                        alRecibirRespuesta = { _ ->
                            Toast.makeText(context, "Contrato eliminado de PostgreSQL", Toast.LENGTH_SHORT).show()
                            cargarContratosDesdeApi(clienteVolley)
                        },
                        alOcurrirError = { msg ->
                            Toast.makeText(context, "Error al eliminar: $msg", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        )

        binding.rvContratos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvContratos.adapter = adaptador

        // Carga inicial directa desde la API REST
        cargarContratosDesdeApi(clienteVolley)

        binding.btnGuardarContrato.setOnClickListener {
            val idInm = binding.etIdInmueble.text.toString().toLongOrNull() ?: 1L
            val idArr = binding.etIdArrendatario.text.toString().toLongOrNull() ?: 1L
            val canon = binding.etCanon.text.toString().toDoubleOrNull() ?: 0.0
            val estado = binding.etEstadoPago.text.toString()

            val cuerpoJson = JSONObject().apply {
                put("idInmueble", idInm)
                put("idArrendatario", idArr)
                put("fechaInicio", "2026-01-01")
                put("fechaFin", "2026-12-31")
                put("canonMensual", canon)
                put("estadoPago", if (estado.isBlank()) "Pendiente" else estado)
            }

            clienteVolley.ejecutarPeticionPostJson(
                urlRelativa = "v1/contracts",
                cuerpoJson = cuerpoJson,
                alRecibirRespuesta = { _ ->
                    Toast.makeText(context, "Contrato guardado en PostgreSQL", Toast.LENGTH_SHORT).show()
                    binding.etIdInmueble.text.clear()
                    binding.etIdArrendatario.text.clear()
                    binding.etCanon.text.clear()
                    binding.etEstadoPago.text.clear()
                    cargarContratosDesdeApi(clienteVolley)
                },
                alOcurrirError = { msg ->
                    Toast.makeText(context, "Error al guardar contrato: $msg", Toast.LENGTH_SHORT).show()
                }
            )
        }

        binding.btnVolverInicio.setOnClickListener {
            (activity as? MainActivity)?.volverAlDashboard()
        }
    }

    private fun cargarContratosDesdeApi(clienteVolley: ClienteVolley) {
        clienteVolley.ejecutarPeticionGetJsonArray(
            urlRelativa = "v1/contracts",
            alRecibirLista = { arregloJson ->
                val listaContratos = mutableListOf<ContratoEntity>()
                for (i in 0 until arregloJson.length()) {
                    val obj = arregloJson.getJSONObject(i)
                    val idRemoto = obj.optLong("id", 0L)
                    val idInm = obj.optLong("idInmueble", 1L)
                    val idArr = obj.optLong("idArrendatario", 1L)
                    val fIni = obj.optString("fechaInicio", "2026-01-01")
                    val fFin = obj.optString("fechaFin", "2026-12-31")
                    val canon = obj.optDouble("canonMensual", 0.0)
                    val est = obj.optString("estadoPago", "Pendiente")

                    listaContratos.add(
                        ContratoEntity(
                            id = idRemoto,
                            idInmueble = idInm,
                            idArrendatario = idArr,
                            fechaInicio = fIni,
                            fechaFin = fFin,
                            canonMensual = canon,
                            estadoPago = est,
                            estaSincronizado = true
                        )
                    )
                }
                if (_binding != null) {
                    adaptador.actualizarLista(listaContratos)
                }
            },
            alOcurrirError = { _ -> }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}