package pt.ipt.dam2025.trabalho

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

// agora usa ListAdapter para lidar com atualizações de lista de forma eficiente.
class HistoricoAdapter : ListAdapter<HistoricoItem, HistoricoAdapter.HistoricoViewHolder>(HistoricoDiffCallback()) {

    // a classe ViewHolder permanece a mesma.
    class HistoricoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dataTextView: TextView = itemView.findViewById(R.id.historicoData)
        private val descricaoTextView: TextView = itemView.findViewById(R.id.historicoDescricao)

        fun bind(item: HistoricoItem) {
            dataTextView.text = item.data
            descricaoTextView.text = item.descricao
        }
    }

    // o onCreateViewHolder permanece o mesmo.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoricoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historico, parent, false)
        return HistoricoViewHolder(itemView)
    }

    // onBindViewHolder é simplificado.
    override fun onBindViewHolder(holder: HistoricoViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }
}

// DiffUtil.ItemCallback para calcular as diferenças na lista.
// isto permite que o ListAdapter faça animações e atualizações eficientes.
class HistoricoDiffCallback : DiffUtil.ItemCallback<HistoricoItem>() {
    override fun areItemsTheSame(oldItem: HistoricoItem, newItem: HistoricoItem): Boolean {
        // o ID é único para cada item.
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: HistoricoItem, newItem: HistoricoItem): Boolean {
        // verifica se os conteúdos são os mesmos.
        return oldItem == newItem
    }
}
