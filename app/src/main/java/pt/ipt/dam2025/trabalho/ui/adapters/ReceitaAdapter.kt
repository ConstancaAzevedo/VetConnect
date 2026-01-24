package pt.ipt.dam2025.trabalho.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.model.Receita

// Adapter para a lista de receitas
class ReceitaAdapter(
    private var receitas: MutableList<Receita>,
    private val onItemClick: (Receita) -> Unit
) : RecyclerView.Adapter<ReceitaAdapter.ReceitaViewHolder>() {

    inner class ReceitaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val data: TextView = view.findViewById(R.id.item_data)
        val titulo: TextView = view.findViewById(R.id.item_titulo)
        val subtitulo: TextView = view.findViewById(R.id.item_subtitulo)
        val info: TextView = view.findViewById(R.id.item_info)

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick(receitas[adapterPosition])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceitaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_documento, parent, false)
        return ReceitaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReceitaViewHolder, position: Int) {
        val receita = receitas[position]
        holder.data.text = receita.dataPrescricao
        holder.titulo.text = receita.medicamento
        holder.subtitulo.text = "Posologia: ${receita.dosagem ?: ""} ${receita.frequencia ?: ""} ${receita.duracao ?: ""}"
        holder.info.text = "Médico: ${receita.veterinario}"
    }

    override fun getItemCount() = receitas.size

    fun updateData(newReceitas: List<Receita>) {
        this.receitas = newReceitas.toMutableList()
        notifyDataSetChanged()
    }
}
