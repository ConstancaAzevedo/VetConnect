package pt.ipt.dam2025.vetconnect.ui.adapter

import android.util.Log // Para registar logs de erro
import android.view.LayoutInflater // Para inflar (criar) views a partir do XML
import android.view.ViewGroup // Classe base para layouts
import androidx.recyclerview.widget.DiffUtil // Para calcular as diferenças entre duas listas de forma eficiente
import androidx.recyclerview.widget.ListAdapter // Adapter otimizado que usa DiffUtil
import androidx.recyclerview.widget.RecyclerView // Classe base do RecyclerView
import pt.ipt.dam2025.vetconnect.R // Para aceder aos recursos da aplicação (como strings)
import pt.ipt.dam2025.vetconnect.databinding.ItemVacinaBinding 
import pt.ipt.dam2025.vetconnect.model.Vacina 
import java.text.SimpleDateFormat // Para formatar e fazer parse de datas
import java.util.Locale // Para especificar a localidade (ex: língua, país) na formatação

/**
 * Adapter para a lista de vacinas de um animal
 * Usa ListAdapter em vez de RecyclerView.Adapter para uma performance
 * calcula automaticamente as diferenças na lista e anima apenas os itens que mudam
 */
class VacinaAdapter(
    // Uma função lambda que será chamada quando um item da lista for clicado
    // O Fragment que cria este adapter irá fornecer a implementação desta função
    private val onItemClick: (Vacina) -> Unit
) : ListAdapter<Vacina, VacinaAdapter.VacinaViewHolder>(VacinaDiffCallback()) {

    /**
     * Chamado pelo RecyclerView quando precisa de criar um ViewHolder
     * Este métdo infla o layout do item XML e retorna uma instância do ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VacinaViewHolder {
        // Usa o LayoutInflater do contexto pai para inflar o layout do item
        val binding = ItemVacinaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // Retorna um novo ViewHolder que contém o binding para o layout do item
        return VacinaViewHolder(binding)
    }

    /**
     * Chamado pelo RecyclerView para associar os dados de um item a um ViewHolder
     * Este métdo obtém o objeto 'Vacina' na posição 'position' e chama o métdo 'bind' do ViewHolder
     */
    override fun onBindViewHolder(holder: VacinaViewHolder, position: Int) {
        // Obtém o item de dados (Vacina) para a posição atual
        val vacina = getItem(position)
        // Chama o méodo 'bind' do ViewHolder para preencher a UI com os dados da vacina
        holder.bind(vacina)
    }

    /**
     * ViewHolder que representa um único item na lista do RecyclerView
     * 'inner' permite que esta classe aceda aos membros da classe externa (VacinaAdapter)
     */
    inner class VacinaViewHolder(private val binding: ItemVacinaBinding) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Associa (bind) os dados de um objeto 'Vacina' às Views dentro do layout do item
         * Este métdo é onde a magia acontece: os dados são colocados nos TextViews
         */
        fun bind(vacina: Vacina) {
            // Configura o listener de clique para a view raiz do item
            // Quando o utilizador clica no item, a função 'onItemClick' (definida no Fragment) é chamada
            binding.root.setOnClickListener { onItemClick(vacina) }

            // Preenche os TextViews com os dados da vacina
            binding.textViewNomeVacina.text = vacina.tipo // Mostra o nome/tipo da vacina
            binding.textViewEstadoVacina.text = vacina.estado // Mostra o estado (ex: 'agendada', 'realizada')

            // Obtém o contexto para aceder aos recursos de string
            val context = binding.root.context
            // Variável para guardar o texto final da data
            val dataTexto: String

            // Lógica para decidir qual data mostrar e como formatá-la
            // Se a data de aplicação não for nula, a vacina já foi dada
            if (vacina.dataAplicacao != null) {
                // Formata a data de aplicação (que vem como 'yyyy-MM-dd')
                val dataFormatada = formatarData(vacina.dataAplicacao, "yyyy-MM-dd")
                // Usa o recurso de string 'vacina_aplicada_em' para criar o texto
                dataTexto = context.getString(R.string.vacina_aplicada_em, dataFormatada)
            // Senão, se a data agendada não for nula, a vacina está por aplicar
            } else if (vacina.dataAgendada != null) {
                // A API envia a data agendada com a hora, mas a função formatarData mostra apenas a data
                val dataFormatada = formatarData(vacina.dataAgendada, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                // Usa o recurso de string 'vacina_agendada_para' para criar o texto
                dataTexto = context.getString(R.string.vacina_agendada_para, dataFormatada)
            // Se nenhuma data estiver disponível
            } else {
                // Usa o recurso de string 'data_invalida' como fallback
                dataTexto = context.getString(R.string.data_invalida)
            }
            // Define o texto final no TextView da data
            binding.textViewDataVacina.text = dataTexto
        }

        /**
         * Função auxiliar para formatar uma string de data para o formato 'dd/MM/yyyy'
         * Esta função é segura e lida com possíveis erros de formatação
         */
        private fun formatarData(dataString: String, formatoEntrada: String): String {
            return try {
                // Define o formato da data que está a chegar (formatoEntrada)
                val inputFormat = SimpleDateFormat(formatoEntrada, Locale.getDefault())
                // Define o formato da data que queremos mostrar
                val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                // Tenta fazer o parse (interpretar) da string de data
                val date = inputFormat.parse(dataString)
                // Se for bem sucedido, formata a data para o formato de saída e retorna
                outputFormat.format(date!!)
            } catch (e: Exception) {
                // Se ocorrer um erro durante o parse (ex: formato inesperado)
                // Regista o erro no Logcat para depuração
                Log.e("VacinaAdapter", "Erro ao formatar data: $dataString", e)
                // Retorna um texto de erro a partir dos recursos de string
                binding.root.context.getString(R.string.data_invalida)
            }
        }
    }

    /**
     * Classe de Callback que o ListAdapter usa para determinar as mudanças na lista
     * É o que torna o ListAdapter tão eficiente
     */
    class VacinaDiffCallback : DiffUtil.ItemCallback<Vacina>() {
        /**
         * Chamado para verificar se dois itens representam a mesma entidade
         * Geralmente, isto é feito comparando os IDs únicos dos itens
         */
        override fun areItemsTheSame(oldItem: Vacina, newItem: Vacina): Boolean {
            return oldItem.id == newItem.id
        }

        /**
         * Chamado para verificar se os dados de dois itens são os mesmos
         * Se 'areItemsTheSame' for true esta função é chamada para ver se o item precisa de ser redesenhado
         * A comparação de data class já faz esta verificação de conteúdo por nós
         */
        override fun areContentsTheSame(oldItem: Vacina, newItem: Vacina): Boolean {
            return oldItem == newItem
        }
    }
}
