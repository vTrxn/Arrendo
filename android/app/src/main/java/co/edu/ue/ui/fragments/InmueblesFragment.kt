// package: Ubicación del Fragmento de Inmuebles
package co.edu.ue.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import co.edu.ue.MainActivity
import co.edu.ue.data.local.database.BaseDeDatosApp
import co.edu.ue.data.local.entity.InmuebleEntity
import co.edu.ue.data.remote.api.ClienteVolley
import co.edu.ue.databinding.FragmentInmueblesBinding
import co.edu.ue.ui.adapters.InmuebleAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

// class InmueblesFragment: Fragmento para la gestión de inmuebles con persistencia híbrida en Room SQLite y Cloud (Supabase / Render) (RF02)
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

        val contextoApp = requireContext().applicationContext
        val baseDatos = BaseDeDatosApp.obtenerBaseDatos(contextoApp)
        val inmuebleDao = baseDatos.inmuebleDao()
        val clienteVolley = ClienteVolley.obtenerInstancia(contextoApp)

        // Configuración del botón ELIMINAR: Elimina de SQLite y de PostgreSQL en la nube
        adaptador = InmuebleAdapter { inmuebleABorrar ->
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                inmuebleDao.eliminarInmueble(inmuebleABorrar)
            }
            if (inmuebleABorrar.id > 0) {
                clienteVolley.ejecutarPeticionDelete(
                    urlRelativa = "v1/properties/${inmuebleABorrar.id}",
                    alRecibirRespuesta = { _ ->
                        Toast.makeText(context, "Inmueble eliminado de SQLite y Cloud", Toast.LENGTH_SHORT).show()
                        cargarInmuebles(clienteVolley, inmuebleDao)
                    },
                    alOcurrirError = { msg ->
                        Toast.makeText(context, "Eliminado localmente de SQLite: $msg", Toast.LENGTH_SHORT).show()
                        cargarDesdeSqlite(inmuebleDao)
                    }
                )
            } else {
                cargarDesdeSqlite(inmuebleDao)
            }
        }

        binding.rvInmuebles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvInmuebles.adapter = adaptador

        // Carga inicial híbrida (intenta Cloud y respalda en SQLite)
        cargarInmuebles(clienteVolley, inmuebleDao)

        // Botón GUARDAR EN SQLITE: Guarda localmente en Room SQLite y sincroniza con Supabase
        binding.btnGuardarInmueble.setOnClickListener {
            val direccion = binding.etDireccion.text.toString().trim()
            val tipo = binding.etTipoInmueble.text.toString().trim()
            val precio = binding.etPrecio.text.toString().toDoubleOrNull() ?: 0.0
            val estado = binding.etEstado.text.toString().trim()

            if (direccion.isNotBlank()) {
                val entidadLocal = InmuebleEntity(
                    direccion = direccion,
                    tipoInmueble = if (tipo.isBlank()) "Apartamento" else tipo,
                    precioArriendo = precio,
                    estado = if (estado.isBlank()) "Disponible" else estado,
                    estaSincronizado = false
                )

                // 1. Guarda de inmediato en la base de datos local Room SQLite
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    val idInsertado = inmuebleDao.insertarInmueble(entidadLocal)

                    val cuerpoJson = JSONObject().apply {
                        put("direccion", entidadLocal.direccion)
                        put("tipoInmueble", entidadLocal.tipoInmueble)
                        put("precioArriendo", entidadLocal.precioArriendo)
                        put("estado", entidadLocal.estado)
                        put("uriImagen", "")
                    }

                    // 2. Envía la petición HTTP POST a Render / Supabase
                    withContext(Dispatchers.Main) {
                        clienteVolley.ejecutarPeticionPostJson(
                            urlRelativa = "v1/properties",
                            cuerpoJson = cuerpoJson,
                            alRecibirRespuesta = { objResp ->
                                val idRemoto = objResp.optLong("id", idInsertado)
                                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                    inmuebleDao.insertarInmueble(
                                        entidadLocal.copy(id = idRemoto, estaSincronizado = true)
                                    )
                                }
                                Toast.makeText(context, "Inmueble guardado en SQLite y Cloud", Toast.LENGTH_SHORT).show()
                                binding.etDireccion.text.clear()
                                binding.etTipoInmueble.text.clear()
                                binding.etPrecio.text.clear()
                                binding.etEstado.text.clear()
                                cargarInmuebles(clienteVolley, inmuebleDao)
                            },
                            alOcurrirError = { msg ->
                                Toast.makeText(context, "Guardado en SQLite local (Modo Offline)", Toast.LENGTH_SHORT).show()
                                binding.etDireccion.text.clear()
                                binding.etTipoInmueble.text.clear()
                                binding.etPrecio.text.clear()
                                binding.etEstado.text.clear()
                                cargarDesdeSqlite(inmuebleDao)
                            }
                        )
                    }
                }
            } else {
                Toast.makeText(context, "Ingrese al menos la dirección", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnVolverInicio.setOnClickListener {
            (activity as? MainActivity)?.volverAlDashboard()
        }
    }

    private fun cargarInmuebles(clienteVolley: ClienteVolley, inmuebleDao: co.edu.ue.data.local.dao.InmuebleDao) {
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

                // Cachea en SQLite local
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    for (inm in listaInmuebles) {
                        inmuebleDao.insertarInmueble(inm)
                    }
                }

                if (_binding != null) {
                    adaptador.actualizarLista(listaInmuebles)
                }
            },
            alOcurrirError = { _ ->
                cargarDesdeSqlite(inmuebleDao)
            }
        )
    }

    private fun cargarDesdeSqlite(inmuebleDao: co.edu.ue.data.local.dao.InmuebleDao) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val listaLocal = inmuebleDao.obtenerTodosLosInmuebles().first()
            withContext(Dispatchers.Main) {
                if (_binding != null) {
                    adaptador.actualizarLista(listaLocal)
                    Toast.makeText(context, "Datos cargados desde SQLite local", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}