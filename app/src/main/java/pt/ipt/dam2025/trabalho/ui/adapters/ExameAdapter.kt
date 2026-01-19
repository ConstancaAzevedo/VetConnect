package pt.ipt.dam2025.trabalho.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.model.Exame

class ExameAdapter(private var exames: List<Exame>) : RecyclerView.Adapter<ExameAdapter.ExameViewHolder>() {

    class ExameViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val data: TextView = view.findViewById(R.id.item_data)
        val titulo: TextView = view.findViewById(R.id.item_titulo)
        val subtitulo: TextView = view.findViewById(R.id.item_subtitulo)
        val info: TextView = view.findViewById(R.id.item_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExameViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_documento, parent, false)
        return ExameViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExameViewHolder, position: Int) {
        val exame = exames[position]
        holder.data.text = exame.data
        holder.titulo.text = exame.tipoExame
        holder.subtitulo.text = "Resultado: ${exame.resultado}"
        holder.info.text = "Laboratório: ${exame.laboratorio}"
    }

    override fun getItemCount() = exames.size

    fun updateData(newExames: List<Exame>) {
        this.exames = newExames
        notifyDataSetChanged()
    }
}
