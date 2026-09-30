// package: Paquete de adaptadores para componentes RecyclerView de Android
package co.edu.ue.ui.adapters

// import: Trae LayoutInflater, RecyclerView y ViewBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.edu.ue.data.local.entity.InmuebleEntity
import co.edu.ue.databinding.ItemInmuebleBinding

// class InmuebleAdapter: Adaptador de RecyclerView que conecta la lista de InmuebleEntity con la vista XML item_inmueble.xml
// extends RecyclerView.Adapter<InmuebleAdapter.InmuebleViewHolder>: Hereda del adaptador base de Android
class InmuebleAdapter(
    private val alEliminar: (InmuebleEntity) -> Unit // Callback ejecutado al pulsar el botón Eliminar
) : RecyclerView.Adapter<InmuebleAdapter.InmuebleViewHolder>() {

    // Lista en memoria de inmuebles
    private var listaInmuebles: List<InmuebleEntity> = emptyList()

    // fun actualizarLista: Recibe la nueva lista de Room y redibuja la lista en pantalla
    fun actualizarLista(nuevaLista: List<InmuebleEntity>) {
        this.listaInmuebles = nuevaLista
        notifyDataSetChanged() // Notifica al RecyclerView que los datos cambiaron
    }

    // inner class InmuebleViewHolder: Clase contenedora que sostiene las referencias a los controles XML de la tarjeta
    inner class InmuebleViewHolder(val binding: ItemInmuebleBinding) : RecyclerView.ViewHolder(binding.root)

    // onCreateViewHolder: Infla el archivo XML item_inmueble.xml mediante ViewBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InmuebleViewHolder {
        val binding = ItemInmuebleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InmuebleViewHolder(binding)
    }

    // onBindViewHolder: Asigna los datos del objeto InmuebleEntity a las etiquetas TextView del XML
    override fun onBindViewHolder(holder: InmuebleViewHolder, position: Int) {
        val inmueble = listaInmuebles[position]
        holder.binding.tvDireccion.text = "ID: ${inmueble.id} - ${inmueble.direccion}"
        holder.binding.tvDetalles.text = "Tipo: ${inmueble.tipoInmueble} | Precio: $${inmueble.precioArriendo}"
        holder.binding.tvEstado.text = "Estado: ${inmueble.estado}"

        holder.binding.btnEliminar.setOnClickListener {
            alEliminar(inmueble)
        }
    }

    // getItemCount: Devuelve el número total de elementos en la lista
    override fun getItemCount(): Int = listaInmuebles.size
}