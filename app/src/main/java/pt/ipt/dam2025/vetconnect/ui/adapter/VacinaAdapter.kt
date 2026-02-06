package pt.ipt.dam2025.vetconnect.ui.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pt.ipt.dam2025.vetconnect.databinding.ItemVacinaBinding
import pt.ipt.dam2025.vetconnect.model.Vacina
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Adapter para a lista de vacinas, usando ListAdapter para atualizações eficientes
 */
class VacinaAdapter(
    private val onItemClick: (Vacina) -> Unit
) : ListAdapter<Vacina, VacinaAdapter.VacinaViewHolder>(VacinaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VacinaViewHolder {
        val binding = ItemVacinaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VacinaViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: VacinaViewHolder, position: Int) {
        val vacina = getItem(position)
        holder.bind(vacina)
    }

    inner class VacinaViewHolder(private val binding: ItemVacinaBinding) : RecyclerView.ViewHolder(binding.root) {
        /**
         * Associa os dados da vacina às views e configura o listener de clique
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(vacina: Vacina) {
            // Configura o clique no item
            binding.root.setOnClickListener { onItemClick(vacina) }

            // Preenche os dados da vacina
            binding.textViewNomeVacina.text = vacina.tipo
            binding.textViewEstadoVacina.text = vacina.estado

            // Formata a data de forma segura
            val dataParaMostrar = vacina.dataAplicacao ?: vacina.dataAgendada
            binding.textViewDataVacina.text = try {
                val dateTime = OffsetDateTime.parse(dataParaMostrar)
                "Aplicada em: ${dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault()))}"
            } catch (e: Exception) {
                dataParaMostrar?.substringBefore('T') ?: "Data inválida"
            }
        }
    }

    /**
     * Callback para calcular as diferenças entre duas listas de forma eficiente
     */
    class VacinaDiffCallback : DiffUtil.ItemCallback<Vacina>() {
        override fun areItemsTheSame(oldItem: Vacina, newItem: Vacina): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Vacina, newItem: Vacina): Boolean {
            return oldItem == newItem
        }
    }
}
