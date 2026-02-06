package pt.ipt.dam2025.vetconnect.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pt.ipt.dam2025.vetconnect.R
import pt.ipt.dam2025.vetconnect.databinding.ItemConsultaBinding
import pt.ipt.dam2025.vetconnect.model.Consulta
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Adapter para a lista de consultas, usando ListAdapter para atualizações eficientes
 */
class ConsultasAdapter(
    private val onItemClick: (Consulta) -> Unit
) : ListAdapter<Consulta, ConsultasAdapter.ConsultaViewHolder>(ConsultaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultaViewHolder {
        val binding = ItemConsultaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ConsultaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConsultaViewHolder, position: Int) {
        val consulta = getItem(position)
        holder.bind(consulta)
    }

    inner class ConsultaViewHolder(private val binding: ItemConsultaBinding) : RecyclerView.ViewHolder(binding.root) {
        /**
         * Associa os dados da consulta às views e configura o listener de clique
         */
        fun bind(consulta: Consulta) {
            // Configura o clique no item aqui, onde temos acesso direto ao objeto 'consulta'
            binding.root.setOnClickListener {
                onItemClick(consulta)
            }

            // Formatação da data
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

            binding.textViewData.text = try {
                val date = inputFormat.parse(consulta.data)
                outputFormat.format(date!!)
            } catch (e: Exception) {
                Log.e("ConsultasAdapter", "Error parsing date", e)
                consulta.data // fallback
            }

            // Preenchimento dos dados
            binding.textViewAnimal.text = itemView.context.getString(R.string.animal_label, consulta.animalNome ?: "N/A")
            binding.textViewClinica.text = itemView.context.getString(R.string.clinica_label, consulta.clinicaNome ?: "N/A")
            binding.textViewVeterinario.text = itemView.context.getString(R.string.veterinario_label, consulta.veterinarioNome ?: "N/A")
            binding.textViewStatus.text = consulta.estado
        }
    }

    /**
     * Callback para calcular as diferenças entre duas listas de forma eficiente
     */
    class ConsultaDiffCallback : DiffUtil.ItemCallback<Consulta>() {
        override fun areItemsTheSame(oldItem: Consulta, newItem: Consulta): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Consulta, newItem: Consulta): Boolean {
            return oldItem == newItem
        }
    }
}
