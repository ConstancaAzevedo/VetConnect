package pt.ipt.dam2025.vetconnect.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pt.ipt.dam2025.vetconnect.databinding.ItemHistoricoBinding
import pt.ipt.dam2025.vetconnect.model.Exame

/**
 * Adapter para a lista de exames no histórico de um animal
 */
class HistoricoAdapter(
    private var exames: List<Exame>,
    private val onItemClick: (Exame) -> Unit
) : RecyclerView.Adapter<HistoricoAdapter.HistoricoViewHolder>() {

    /**
     * Cria e retorna um ViewHolder, inflando o layout do item
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoricoViewHolder {
        val binding = ItemHistoricoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoricoViewHolder(binding)
    }

    /**
     * Associa os dados de um exame específico à view de um ViewHolder
     */
    override fun onBindViewHolder(holder: HistoricoViewHolder, position: Int) {
        holder.bind(exames[position])
    }

    /**
     * Retorna o número total de itens na lista
     */
    override fun getItemCount(): Int = exames.size

    /**
     * Atualiza a lista de exames e notifica o adapter para redesenhar a lista
     */
    fun updateData(newExames: List<Exame>) {
        exames = newExames
        notifyDataSetChanged() // Para otimização futura, considere usar DiffUtil
    }

    /**
     * ViewHolder para cada item da lista de exames
     * Usa View Binding para aceder às views
     */
    inner class HistoricoViewHolder(private val binding: ItemHistoricoBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            // Configura o listener de clique para o item inteiro
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick(exames[adapterPosition])
                }
            }
        }

        // Associa os dados do exame às views do layout
        fun bind(exame: Exame) {
            binding.itemData.text = exame.dataExame ?: "Sem data"
            binding.itemTitulo.text = exame.tipo ?: "Tipo de exame desconhecido"
            binding.itemSubtitulo.text = "Resultado: ${exame.resultado ?: "N/A"}"
            binding.itemInfo.text = "Clínica: ${exame.clinicaNome ?: "Não especificada"}"

            // Mostra ou esconde o ícone da foto
            binding.iconPhoto.visibility = if (exame.ficheiroUrl != null) View.VISIBLE else View.GONE
        }
    }
}
