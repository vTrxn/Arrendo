// package: Adaptador de RecyclerView para contratos
package co.edu.ue.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.edu.ue.data.local.entity.ContratoEntity
import co.edu.ue.databinding.ItemContratoBinding

class ContratoAdapter(
    private val alPagar: (ContratoEntity) -> Unit,
    private val alEliminar: (ContratoEntity) -> Unit
) : RecyclerView.Adapter<ContratoAdapter.ContratoViewHolder>() {

    private var listaContratos: List<ContratoEntity> = emptyList()

    fun actualizarLista(nuevaLista: List<ContratoEntity>) {
        this.listaContratos = nuevaLista
        notifyDataSetChanged()
    }

    inner class ContratoViewHolder(val binding: ItemContratoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContratoViewHolder {
        val binding = ItemContratoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContratoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContratoViewHolder, position: Int) {
        val contrato = listaContratos[position]
        holder.binding.tvContratoInfo.text = "Contrato ID: ${contrato.id}"
        holder.binding.tvRelaciones.text = "Inmueble ID: ${contrato.idInmueble} | Inquilino ID: ${contrato.idArrendatario}"
        holder.binding.tvFechasCanon.text = "Fechas: ${contrato.fechaInicio} a ${contrato.fechaFin} | Canon: $${contrato.canonMensual}"
        holder.binding.tvEstadoPago.text = "Estado: ${contrato.estadoPago}"

        holder.binding.btnPagar.setOnClickListener {
            alPagar(contrato)
        }

        holder.binding.btnEliminar.setOnClickListener {
            alEliminar(contrato)
        }
    }

    override fun getItemCount(): Int = listaContratos.size
}