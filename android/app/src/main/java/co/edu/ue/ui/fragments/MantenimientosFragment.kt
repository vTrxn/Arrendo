// package: Ubicación del Fragmento de Mantenimientos
package co.edu.ue.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import co.edu.ue.MainActivity
import co.edu.ue.data.local.entity.MantenimientoEntity
import co.edu.ue.data.remote.api.ClienteVolley
import co.edu.ue.databinding.FragmentMantenimientosBinding
import co.edu.ue.ui.adapters.MantenimientoAdapter
import org.json.JSONObject
import java.io.File

// class MantenimientosFragment: Fragmento para gestión de incidencias con peticiones directas a la API REST de Spring Boot / PostgreSQL (RF05)
class MantenimientosFragment : Fragment() {

    private var _binding: FragmentMantenimientosBinding? = null
    private val binding get() = _binding!!
    private lateinit var adaptador: MantenimientoAdapter
    private var uriFotoTemporal: Uri? = null
    private var rutaFotoGuardada: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMantenimientosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val clienteVolley = ClienteVolley.obtenerInstancia(requireContext().applicationContext)

        val lanzadorCamara = registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { esExitoso ->
            if (esExitoso && uriFotoTemporal != null) {
                rutaFotoGuardada = uriFotoTemporal.toString()
                binding.tvRutaFoto.text = "Foto Capturada: $rutaFotoGuardada"
            }
        }

        val lanzadorPermisoCamara = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { esConcedido ->
            if (esConcedido && uriFotoTemporal != null) {
                lanzadorCamara.launch(uriFotoTemporal)
            }
        }

        adaptador = MantenimientoAdapter { mantenimientoABorrar ->
            if (mantenimientoABorrar.id > 0) {
                clienteVolley.ejecutarPeticionDelete(
                    urlRelativa = "v1/maintenances/${mantenimientoABorrar.id}",
                    alRecibirRespuesta = { _ ->
                        Toast.makeText(context, "Mantenimiento eliminado de PostgreSQL", Toast.LENGTH_SHORT).show()
                        cargarMantenimientosDesdeApi(clienteVolley)
                    },
                    alOcurrirError = { msg ->
                        Toast.makeText(context, "Error al eliminar: $msg", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        binding.rvMantenimientos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMantenimientos.adapter = adaptador

        // Carga directa desde la API REST
        cargarMantenimientosDesdeApi(clienteVolley)

        binding.btnTomarFoto.setOnClickListener {
            val contexto = requireContext()
            val archivoFoto = File(contexto.cacheDir, "dano_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(
                contexto,
                "${contexto.packageName}.fileprovider",
                archivoFoto
            )
            uriFotoTemporal = uri
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    contexto,
                    android.Manifest.permission.CAMERA
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                lanzadorCamara.launch(uriFotoTemporal)
            } else {
                lanzadorPermisoCamara.launch(android.Manifest.permission.CAMERA)
            }
        }

        binding.btnGuardarMantenimiento.setOnClickListener {
            val idInm = binding.etIdInmueble.text.toString().toLongOrNull() ?: 1L
            val descripcion = binding.etDescripcionDano.text.toString()

            if (descripcion.isNotBlank()) {
                val cuerpoJson = JSONObject().apply {
                    put("idInmueble", idInm)
                    put("descripcionDano", descripcion)
                    put("fechaReporte", "2026-09-27")
                    put("rutaFoto", rutaFotoGuardada)
                    put("estadoMantenimiento", "Pendiente")
                }

                clienteVolley.ejecutarPeticionPostJson(
                    urlRelativa = "v1/maintenances",
                    cuerpoJson = cuerpoJson,
                    alRecibirRespuesta = { _ ->
                        Toast.makeText(context, "Mantenimiento guardado en PostgreSQL", Toast.LENGTH_SHORT).show()
                        binding.etIdInmueble.text.clear()
                        binding.etDescripcionDano.text.clear()
                        binding.tvRutaFoto.text = "Foto no capturada"
                        rutaFotoGuardada = ""
                        cargarMantenimientosDesdeApi(clienteVolley)
                    },
                    alOcurrirError = { msg ->
                        Toast.makeText(context, "Error al guardar: $msg", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        binding.btnVolverInicio.setOnClickListener {
            (activity as? MainActivity)?.volverAlDashboard()
        }
    }

    private fun cargarMantenimientosDesdeApi(clienteVolley: ClienteVolley) {
        clienteVolley.ejecutarPeticionGetJsonArray(
            urlRelativa = "v1/maintenances",
            alRecibirLista = { arregloJson ->
                val listaMantenimientos = mutableListOf<MantenimientoEntity>()
                for (i in 0 until arregloJson.length()) {
                    val obj = arregloJson.getJSONObject(i)
                    val idRemoto = obj.optLong("id", 0L)
                    val idInm = obj.optLong("idInmueble", 1L)
                    val desc = obj.optString("descripcionDano", "")
                    val fRep = obj.optString("fechaReporte", "2026-09-27")
                    val foto = obj.optString("rutaFoto", "")
                    val est = obj.optString("estadoMantenimiento", "Pendiente")

                    if (desc.isNotEmpty()) {
                        listaMantenimientos.add(
                            MantenimientoEntity(
                                id = idRemoto,
                                idInmueble = idInm,
                                descripcionDano = desc,
                                fechaReporte = fRep,
                                rutaFoto = foto,
                                estadoMantenimiento = est,
                                estaSincronizado = true
                            )
                        )
                    }
                }
                if (_binding != null) {
                    adaptador.actualizarLista(listaMantenimientos)
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