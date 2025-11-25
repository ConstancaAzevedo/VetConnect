package pt.ipt.dam2025.trabalho.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.model.HistoricoItem

/*
 *adaptador da lista - liga os dados (lista de HsitoricoItem)
 * à interface (RecycleView)
 */
class HistoricoAdapter : ListAdapter<HistoricoItem, HistoricoAdapter.HistoricoViewHolder>(HistoricoDiffCallback()) {
    // classe principal
    // LisAdapter é específico para lista de itens que vão alterando


    // esta classe representa um único item na lista
    // segura (holds) as referências para as TextViews que estão dentro do layout item_historico
    class HistoricoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dataTextView: TextView = itemView.findViewById(R.id.historicoData)
        private val descricaoTextView: TextView = itemView.findViewById(R.id.historicoDescricao)

        // função que recebe um objeto HistoricoItem e é responsável por pegar nos dados do objeto (item.data e item.descricao)
        // e colocar na interface
        fun bind(item: HistoricoItem) {
            dataTextView.text = item.data
            descricaoTextView.text = item.descricao
        }
    }

    // cria a estretura visual vazia de um item da lista, nem sempre a Recycleview o chama, só quando
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoricoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historico, parent, false)
        return HistoricoViewHolder(itemView)
    }

    // pega numa estretura visual já existente e preenche-la com os dados corretos
    override fun onBindViewHolder(holder: HistoricoViewHolder, position: Int) {
        val currentItem = getItem(position) // vai buscar os dados do item na posição
        holder.bind(currentItem) // entrega os dados para a função holder bind
    }
}


// quando a lista muda, o DiffUtil usa este callback para perceber o que se passou
class HistoricoDiffCallback : DiffUtil.ItemCallback<HistoricoItem>() {
    // compara a chave primária(id) - se os itens são os mesmos
    override fun areItemsTheSame(oldItem: HistoricoItem, newItem: HistoricoItem): Boolean {
        // o ID é único para cada item
        return oldItem.id == newItem.id
    }

    // verifica se os conteúdos são os mesmos
    override fun areContentsTheSame(oldItem: HistoricoItem, newItem: HistoricoItem): Boolean {
        // verifica se os conteúdos dos campos são os mesmos
        return oldItem == newItem
    }
}
