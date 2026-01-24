package pt.ipt.dam2025.trabalho.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.model.Vacina

// Adapter para a lista de vacinas
class VacinaAdapter(
    private var vacinas: MutableList<Vacina>,
    private val onItemClick: (Vacina) -> Unit
) : RecyclerView.Adapter<VacinaAdapter.VacinaViewHolder>() {

    inner class VacinaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val data: TextView = view.findViewById(R.id.item_data)
        val titulo: TextView = view.findViewById(R.id.item_titulo)
        val subtitulo: TextView = view.findViewById(R.id.item_subtitulo)
        val info: TextView = view.findViewById(R.id.item_info)

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick(vacinas[adapterPosition])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VacinaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_documento, parent, false)
        return VacinaViewHolder(view)
    }

    override fun onBindViewHolder(holder: VacinaViewHolder, position: Int) {
        val vacina = vacinas[position]
        holder.data.text = vacina.dataAplicacao
        holder.titulo.text = vacina.tipo
        holder.subtitulo.text = "Lote: ${vacina.lote}"
        holder.info.text = "Próxima dose: ${vacina.dataProxima}"
    }

    override fun getItemCount() = vacinas.size

    fun updateData(newVacinas: List<Vacina>) {
        this.vacinas = newVacinas.toMutableList()
        notifyDataSetChanged()
    }
}