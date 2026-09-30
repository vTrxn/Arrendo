// package: Adaptador de RecyclerView para inquilinos
package co.edu.ue.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.edu.ue.data.local.entity.ArrendatarioEntity
import co.edu.ue.databinding.ItemArrendatarioBinding

class ArrendatarioAdapter(
    private val alEliminar: (ArrendatarioEntity) -> Unit
) : RecyclerView.Adapter<ArrendatarioAdapter.ArrendatarioViewHolder>() {

    private var listaArrendatarios: List<ArrendatarioEntity> = emptyList()

    fun actualizarLista(nuevaLista: List<ArrendatarioEntity>) {
        this.listaArrendatarios = nuevaLista
        notifyDataSetChanged()
    }

    inner class ArrendatarioViewHolder(val binding: ItemArrendatarioBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArrendatarioViewHolder {
        val binding = ItemArrendatarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArrendatarioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArrendatarioViewHolder, position: Int) {
        val arrendatario = listaArrendatarios[position]
        holder.binding.tvNombre.text = "ID: ${arrendatario.id} - ${arrendatario.nombreCompleto}"
        holder.binding.tvContacto.text = "Tel: ${arrendatario.telefono} | Correo: ${arrendatario.correo}"
        holder.binding.tvCedula.text = "Cédula: ${arrendatario.cedula}"

        holder.binding.btnEliminar.setOnClickListener {
            alEliminar(arrendatario)
        }
    }

    override fun getItemCount(): Int = listaArrendatarios.size
}