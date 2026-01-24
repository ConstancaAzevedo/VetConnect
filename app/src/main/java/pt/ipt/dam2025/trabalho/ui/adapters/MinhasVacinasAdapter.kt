package pt.ipt.dam2025.trabalho.ui.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pt.ipt.dam2025.trabalho.databinding.ItemMinhaVacinaBinding
import pt.ipt.dam2025.trabalho.model.Vacina
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// Adapter para a lista de vacinas
class MinhasVacinasAdapter(
    private val onEditClick: (Vacina) -> Unit
) : ListAdapter<Vacina, MinhasVacinasAdapter.VacinaViewHolder>(VacinaDiffCallback()) {

    // Usar o DateTimeFormatter do Java 8 para maior robustez
    private val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VacinaViewHolder {
        val binding = ItemMinhaVacinaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VacinaViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: VacinaViewHolder, position: Int) {
        val vacina = getItem(position)
        holder.bind(vacina)
    }

    inner class VacinaViewHolder(private val binding: ItemMinhaVacinaBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onEditClick(getItem(adapterPosition))
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(vacina: Vacina) {
            binding.vacinaData.text = try {
                // Usar a propriedade 'data' que existe no modelo Vacina
                val dateTime = OffsetDateTime.parse(vacina.dataAgendada)
                dateTime.format(outputFormatter)
            } catch (e: Exception) {
                // Fallback em caso de erro de parsing
                vacina.dataAgendada?.substringBefore('T') ?: "Data inválida"
            }
            // Usar a propriedade 'nomeVacina'
            binding.vacinaTipo.text = vacina.tipo
        }
    }

    class VacinaDiffCallback : DiffUtil.ItemCallback<Vacina>() {
        override fun areItemsTheSame(oldItem: Vacina, newItem: Vacina): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Vacina, newItem: Vacina): Boolean {
            return oldItem == newItem
        }
    }
}
