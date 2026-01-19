package pt.ipt.dam2025.trabalho.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.model.Receita

class ReceitaAdapter(private var receitas: List<Receita>) : RecyclerView.Adapter<ReceitaAdapter.ReceitaViewHolder>() {

    class ReceitaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val data: TextView = view.findViewById(R.id.item_data)
        val titulo: TextView = view.findViewById(R.id.item_titulo)
        val subtitulo: TextView = view.findViewById(R.id.item_subtitulo)
        val info: TextView = view.findViewById(R.id.item_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceitaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_documento, parent, false)
        return ReceitaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReceitaViewHolder, position: Int) {
        val receita = receitas[position]
        holder.data.text = receita.data
        holder.titulo.text = receita.medicamento
        holder.subtitulo.text = "Posologia: ${receita.posologia}"
        holder.info.text = "Médico: ${receita.medico}"
    }

    override fun getItemCount() = receitas.size

    fun updateData(newReceitas: List<Receita>) {
        this.receitas = newReceitas
        notifyDataSetChanged()
    }
}
