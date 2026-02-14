package pt.ipt.dam2025.vetconnect.ui.adapter

import android.util.Log // Importa a classe Log para registar mensagens de erro
import android.view.LayoutInflater // Importa para inflar layouts XML
import android.view.ViewGroup // Importa a classe base para layouts
import androidx.recyclerview.widget.DiffUtil // Importa para calcular diferenças entre listas
import androidx.recyclerview.widget.ListAdapter // Importa a classe base para adapters de lista eficientes
import androidx.recyclerview.widget.RecyclerView // Importa a classe base para o RecyclerView
import pt.ipt.dam2025.vetconnect.R // Importa os recursos da aplicação
import pt.ipt.dam2025.vetconnect.databinding.ItemConsultaBinding
import pt.ipt.dam2025.vetconnect.model.Consulta
import java.text.SimpleDateFormat // Importa para formatar datas
import java.util.Locale // Importa para definir a localização para formatação

/**
 * Adapter para a lista de consultas
 * Usa ListAdapter para listas dinâmicas porque gere as atualizações através do DiffUtil
 */
class ConsultasAdapter(
    // Lambda que será chamado quando um item da lista for clicado
    private val onItemClick: (Consulta) -> Unit
) : ListAdapter<Consulta, ConsultasAdapter.ConsultaViewHolder>(ConsultaDiffCallback()) {

    /**
     * Chamado pelo RecyclerView quando precisa de criar um ViewHolder
     * Infla o layout do item e retorna um novo ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultaViewHolder {
        // Infla o layout do item usando ViewBinding
        val binding = ItemConsultaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // Retorna uma nova instância do ViewHolder com o binding
        return ConsultaViewHolder(binding)
    }

    /**
     * Chamado pelo RecyclerView para exibir os dados na posição especificada
     * Obtém a consulta na posição e chama o métdo bind do ViewHolder
     */
    override fun onBindViewHolder(holder: ConsultaViewHolder, position: Int) {
        // Obtém a consulta da lista na posição atual
        val consulta = getItem(position)
        // Associa (bind) os dados da consulta ao ViewHolder
        holder.bind(consulta)
    }

    /**
     * ViewHolder para um item da lista de consultas
     * Contém a lógica para associar os dados de uma consulta às views do layout
     */
    inner class ConsultaViewHolder(private val binding: ItemConsultaBinding) : RecyclerView.ViewHolder(binding.root) {
        
        /**
         * Associa os dados da consulta às views e configura o listener de clique
         */
        fun bind(consulta: Consulta) {
            // Configura o clique no item para chamar o lambda passado ao adapter
            binding.root.setOnClickListener {
                onItemClick(consulta)
            }

            // Formatação da data
            // Define o formato de entrada (como a data chega da API)
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            // Define o formato de saída (como queremos mostrar na UI)
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            binding.textViewData.text = try {
                // Tenta fazer o parse da data
                val date = inputFormat.parse(consulta.data)
                // Se conseguir formata para o formato de saída
                outputFormat.format(date!!)
            } catch (e: Exception) {
                // Se falhar regista um erro e mostra a data original como fallback
                Log.e("ConsultasAdapter", "Erro ao formatar data: ${consulta.data}", e)
                consulta.data // Mostra a data sem formatação em caso de erro
            }

            // Preenchimento dos restantes dados usando os recursos de string
            binding.textViewClinica.text = binding.root.context.getString(R.string.clinica_label, consulta.clinicaNome ?: "N/A")
            binding.textViewVeterinario.text = binding.root.context.getString(R.string.veterinario_label, consulta.veterinarioNome ?: "N/A")
            binding.textViewStatus.text = consulta.estado
        }
    }

    /**
     * Callback para o ListAdapter calcular as diferenças entre duas listas de forma eficiente
     * Isto permite que o RecyclerView anime apenas os itens que mudaram
     */
    class ConsultaDiffCallback : DiffUtil.ItemCallback<Consulta>() {

         // Verifica se dois itens são os mesmos (ex: se têm o mesmo ID)
        override fun areItemsTheSame(oldItem: Consulta, newItem: Consulta): Boolean {
            return oldItem.id == newItem.id
        }

         // Verifica se o conteúdo de dois itens é o mesmo
         // O ListAdapter só redesenha o item se esta função retornar false
        override fun areContentsTheSame(oldItem: Consulta, newItem: Consulta): Boolean {
            return oldItem == newItem
        }
    }
}
