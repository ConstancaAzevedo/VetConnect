package pt.ipt.dam2025.trabalho

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Data class para guardar os dados de cada evento
data class HistoricoItem(val data: String, val descricao: String)


// classe que gere a criação e a ligação dos dados aos itens da lista
class HistoricoAdapter(private val historicoList: List<HistoricoItem>) :
    RecyclerView.Adapter<HistoricoAdapter.HistoricoViewHolder>() {

    // classe interna para manter as referências às textviews em cada item
    class HistoricoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dataTextView: TextView = itemView.findViewById(R.id.historicoData)
        val descricaoTextView: TextView = itemView.findViewById(R.id.historicoDescricao)
    }

    // Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoricoViewHolder {
        // Create a new view, which defines the UI of the list item
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historico, parent, false)
        return HistoricoViewHolder(itemView)
    }

    // Called by RecyclerView to display the data at the specified position.
    override fun onBindViewHolder(holder: HistoricoViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val currentItem = historicoList[position]
        holder.dataTextView.text = currentItem.data
        holder.descricaoTextView.text = currentItem.descricao
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = historicoList.size
}
