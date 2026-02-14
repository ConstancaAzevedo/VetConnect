package pt.ipt.dam2025.vetconnect.ui.adapter

import android.util.Log // Importa a classe Log para registar mensagens de erro
import android.view.LayoutInflater // Importa para inflar layouts XML
import android.view.View // Importa a classe View
import android.view.ViewGroup // Importa a classe base para layouts
import androidx.recyclerview.widget.DiffUtil // Importa para calcular diferenças entre listas
import androidx.recyclerview.widget.ListAdapter // Importa a classe base para adapters de lista eficientes
import androidx.recyclerview.widget.RecyclerView // Importa a classe base para o RecyclerView
import pt.ipt.dam2025.vetconnect.databinding.ItemHistoricoBinding // Importa a classe de ViewBinding gerada para o item
import pt.ipt.dam2025.vetconnect.model.Exame // Importa o modelo de dados do exame
import java.text.SimpleDateFormat // Importa para formatar datas
import java.util.Locale // Importa para definir a localização para formatação
import pt.ipt.dam2025.vetconnect.R // Importa a classe R para aceder aos recursos

/**
 * Adapter para a lista de exames no histórico de um animal
 * Usa ListAdapter para uma performance otimizada
 */
class HistoricoAdapter(
    // Lambda que será chamado quando um item da lista for clicado
    private val onItemClick: (Exame) -> Unit
) : ListAdapter<Exame, HistoricoAdapter.HistoricoViewHolder>(ExameDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoricoViewHolder {
        // Infla o layout do item usando ViewBinding
        val binding = ItemHistoricoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // Retorna uma nova instância do ViewHolder com o binding
        return HistoricoViewHolder(binding)
    }

    /**
     * Associa os dados de um exame específico à view de um ViewHolder
     */
    override fun onBindViewHolder(holder: HistoricoViewHolder, position: Int) {
        // Obtém o exame da lista na posição atual
        val exame = getItem(position)
        // Associa (bind) os dados do exame ao ViewHolder
        holder.bind(exame)
    }

    /**
     * ViewHolder para cada item da lista de exames
     * Usa View Binding para aceder às views
     */
    inner class HistoricoViewHolder(private val binding: ItemHistoricoBinding) : RecyclerView.ViewHolder(binding.root) {
        
        // Bloco de inicialização para configurar o listener de clique uma única vez
        init {
            // Configura o listener de clique para o item inteiro
            itemView.setOnClickListener {
                // Obtém a posição do item clicado
                val position = bindingAdapterPosition
                // Garante que a posição é válida antes de aceder à lista
                if (position != RecyclerView.NO_POSITION) {
                    // Chama o lambda de clique com o exame correspondente
                    onItemClick(getItem(position))
                }
            }
        }

        // Associa os dados do exame às views do layout
        fun bind(exame: Exame) {
            // Obtém o contexto a partir da view raiz para aceder aos recursos de string
            val context = binding.root.context
            // Formatação da data
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            
            // Tenta formatar a data, se falhar mostra a data original
            binding.itemData.text = try {
                val date = inputFormat.parse(exame.dataExame ?: "")
                outputFormat.format(date!!)
            } catch (e: Exception) {
                Log.e("HistoricoAdapter", "Erro ao formatar data: ${exame.dataExame}", e)
                exame.dataExame ?: context.getString(R.string.sem_data)
            }

            // Preenche os restantes campos usando os recursos de string
            binding.itemTitulo.text = exame.tipo ?: context.getString(R.string.tipo_exame_desconhecido)
            binding.itemSubtitulo.text = context.getString(R.string.resultado_label, exame.resultado ?: context.getString(R.string.na))
            binding.itemInfo.text = context.getString(R.string.clinica_label, exame.clinicaNome ?: context.getString(R.string.nao_especificada))

            // Mostra ou esconde o ícone da foto com base na existência de uma URL
            binding.iconPhoto.visibility = if (exame.ficheiroUrl != null) View.VISIBLE else View.GONE
        }
    }

    /**
     * Callback para o ListAdapter calcular as diferenças entre duas listas
     * Essencial para a performance e animações corretas
     */
    class ExameDiffCallback : DiffUtil.ItemCallback<Exame>() {
        /**
         * Verifica se dois itens representam a mesma entidade (ex: mesmo ID)
         */
        override fun areItemsTheSame(oldItem: Exame, newItem: Exame): Boolean {
            return oldItem.id == newItem.id
        }

        /**
         * Verifica se o conteúdo de dois itens é o mesmo
         * Se esta função retornar false o item será redesenhado
         */
        override fun areContentsTheSame(oldItem: Exame, newItem: Exame): Boolean {
            return oldItem == newItem
        }
    }
}
