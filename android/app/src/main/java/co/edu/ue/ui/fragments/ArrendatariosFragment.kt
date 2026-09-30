// package: Ubicación del Fragmento de Arrendatarios y Contactos
package co.edu.ue.ui.fragments

import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import co.edu.ue.MainActivity
import co.edu.ue.data.local.entity.ArrendatarioEntity
import co.edu.ue.data.remote.api.ClienteVolley
import co.edu.ue.databinding.FragmentArrendatariosBinding
import co.edu.ue.ui.adapters.ArrendatarioAdapter
import org.json.JSONObject

// class ArrendatariosFragment: Fragmento para la gestión de inquilinos con peticiones directas a la API REST de Spring Boot / PostgreSQL (RF03)
class ArrendatariosFragment : Fragment() {

    private var _binding: FragmentArrendatariosBinding? = null
    private val binding get() = _binding!!
    private lateinit var adaptador: ArrendatarioAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArrendatariosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val clienteVolley = ClienteVolley.obtenerInstancia(requireContext().applicationContext)

        // ActivityResultContracts.PickContact(): Abre el selector nativo de la agenda de contactos de Android
        val lanzadorSelectorContactos = registerForActivityResult(
            ActivityResultContracts.PickContact()
        ) { uriContacto ->
            uriContacto?.let { uri ->
                try {
                    val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
                    cursor?.use { c ->
                        if (c.moveToFirst()) {
                            val idIndice = c.getColumnIndex(ContactsContract.Contacts._ID)
                            val idContacto = c.getString(idIndice)
                            val nombreIndice = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                            val nombreContacto = c.getString(nombreIndice) ?: ""

                            binding.etNombreCompleto.setText(nombreContacto)

                            val cursorTelefono = requireContext().contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                                arrayOf(idContacto),
                                null
                            )
                            cursorTelefono?.use { ct ->
                                if (ct.moveToFirst()) {
                                    val telIndice = ct.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                    val numeroTelefono = ct.getString(telIndice) ?: ""
                                    binding.etTelefono.setText(numeroTelefono)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        val lanzadorPermisos = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { esConcedido ->
            if (esConcedido) {
                lanzadorSelectorContactos.launch(null)
            }
        }

        // Eliminación directa vía Volley DELETE /v1/tenants/{id}
        adaptador = ArrendatarioAdapter { arrendatarioABorrar ->
            if (arrendatarioABorrar.id > 0) {
                clienteVolley.ejecutarPeticionDelete(
                    urlRelativa = "v1/tenants/${arrendatarioABorrar.id}",
                    alRecibirRespuesta = { _ ->
                        Toast.makeText(context, "Inquilino eliminado de PostgreSQL", Toast.LENGTH_SHORT).show()
                        cargarArrendatariosDesdeApi(clienteVolley)
                    },
                    alOcurrirError = { msg ->
                        Toast.makeText(context, "Error al eliminar: $msg", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        binding.rvArrendatarios.layoutManager = LinearLayoutManager(requireContext())
        binding.rvArrendatarios.adapter = adaptador

        // Carga directa de la lista remota desde la API REST
        cargarArrendatariosDesdeApi(clienteVolley)

        // Botón Guardar: Petición POST directa a /api/v1/tenants
        binding.btnGuardarArrendatario.setOnClickListener {
            val nombre = binding.etNombreCompleto.text.toString()
            val tel = binding.etTelefono.text.toString()
            val correo = binding.etCorreo.text.toString()
            val cedula = binding.etCedula.text.toString()

            if (nombre.isNotBlank()) {
                val cuerpoJson = JSONObject().apply {
                    put("nombreCompleto", nombre)
                    put("telefono", tel)
                    put("correo", correo)
                    put("cedula", cedula)
                }

                clienteVolley.ejecutarPeticionPostJson(
                    urlRelativa = "v1/tenants",
                    cuerpoJson = cuerpoJson,
                    alRecibirRespuesta = { _ ->
                        Toast.makeText(context, "Arrendatario guardado en PostgreSQL", Toast.LENGTH_SHORT).show()
                        binding.etNombreCompleto.text.clear()
                        binding.etTelefono.text.clear()
                        binding.etCorreo.text.clear()
                        binding.etCedula.text.clear()
                        cargarArrendatariosDesdeApi(clienteVolley)
                    },
                    alOcurrirError = { msg ->
                        Toast.makeText(context, "Error al guardar: $msg", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        binding.btnImportarContacto.setOnClickListener {
            lanzadorPermisos.launch(android.Manifest.permission.READ_CONTACTS)
        }

        binding.btnVolverInicio.setOnClickListener {
            (activity as? MainActivity)?.volverAlDashboard()
        }
    }

    private fun cargarArrendatariosDesdeApi(clienteVolley: ClienteVolley) {
        clienteVolley.ejecutarPeticionGetJsonArray(
            urlRelativa = "v1/tenants",
            alRecibirLista = { arregloJson ->
                val listaArrendatarios = mutableListOf<ArrendatarioEntity>()
                for (i in 0 until arregloJson.length()) {
                    val obj = arregloJson.getJSONObject(i)
                    val idRemoto = obj.optLong("id", 0L)
                    val nom = obj.optString("nombreCompleto", "")
                    val tel = obj.optString("telefono", "")
                    val correo = obj.optString("correo", "")
                    val cedula = obj.optString("cedula", "")

                    if (nom.isNotEmpty()) {
                        listaArrendatarios.add(
                            ArrendatarioEntity(
                                id = idRemoto,
                                nombreCompleto = nom,
                                telefono = tel,
                                correo = correo,
                                cedula = cedula,
                                estaSincronizado = true
                            )
                        )
                    }
                }
                if (_binding != null) {
                    adaptador.actualizarLista(listaArrendatarios)
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