// package: Adaptador de RecyclerView para reportes de mantenimiento
package co.edu.ue.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.edu.ue.data.local.entity.MantenimientoEntity
import co.edu.ue.databinding.ItemMantenimientoBinding

class MantenimientoAdapter(
    private val alEliminar: (MantenimientoEntity) -> Unit
) : RecyclerView.Adapter<MantenimientoAdapter.MantenimientoViewHolder>() {

    private var listaMantenimientos: List<MantenimientoEntity> = emptyList()

    fun actualizarLista(nuevaLista: List<MantenimientoEntity>) {
        this.listaMantenimientos = nuevaLista
        notifyDataSetChanged()
    }

    inner class MantenimientoViewHolder(val binding: ItemMantenimientoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MantenimientoViewHolder {
        val binding = ItemMantenimientoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MantenimientoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MantenimientoViewHolder, position: Int) {
        val mantenimiento = listaMantenimientos[position]
        holder.binding.tvMantenimientoInfo.text = "ID: ${mantenimiento.id} - Inmueble ID: ${mantenimiento.idInmueble}"
        holder.binding.tvDescripcion.text = "Descripción: ${mantenimiento.descripcionDano}"
        holder.binding.tvRutaFoto.text = "Foto URI: ${if (mantenimiento.rutaFoto.isNotEmpty()) mantenimiento.rutaFoto else "Sin foto"}"

        holder.binding.btnEliminar.setOnClickListener {
            alEliminar(mantenimiento)
        }
    }

    override fun getItemCount(): Int = listaMantenimientos.size
}