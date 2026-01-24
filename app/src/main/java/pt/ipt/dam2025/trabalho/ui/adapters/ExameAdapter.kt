package pt.ipt.dam2025.trabalho.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.model.Exame

// Adapter para a lista de exames
class ExameAdapter(
    private var exames: MutableList<Exame>,
    private val onItemClick: (Exame) -> Unit
) : RecyclerView.Adapter<ExameAdapter.ExameViewHolder>() {

    inner class ExameViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val data: TextView = view.findViewById(R.id.item_data)
        val titulo: TextView = view.findViewById(R.id.item_titulo)
        val subtitulo: TextView = view.findViewById(R.id.item_subtitulo)
        val info: TextView = view.findViewById(R.id.item_info)

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick(exames[adapterPosition])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExameViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_documento, parent, false)
        return ExameViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExameViewHolder, position: Int) {
        val exame = exames[position]
        holder.data.text = exame.dataExame
        holder.titulo.text = exame.tipo
        holder.subtitulo.text = "Resultado: ${exame.resultado}"
        holder.info.text = "Laboratório: ${exame.laboratorio}"
    }

    override fun getItemCount() = exames.size

    fun updateData(newExames: List<Exame>) {
        this.exames = newExames.toMutableList()
        notifyDataSetChanged()
    }
}