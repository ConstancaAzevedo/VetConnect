package pt.ipt.dam2025.trabalho.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.databinding.ItemConsultaBinding
import pt.ipt.dam2025.trabalho.model.Consulta
import java.text.SimpleDateFormat
import java.util.Locale

// Adapter para a lista de consultas
class ConsultasAdapter(
    private val onCancelClick: (Consulta) -> Unit
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
        fun bind(consulta: Consulta) {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

            binding.textViewData.text = try {
                val date = inputFormat.parse(consulta.data)
                outputFormat.format(date!!)
            } catch (e: Exception) {
                Log.e("ConsultasAdapter", "Error parsing date", e)
                consulta.data // fallback
            }

            binding.textViewAnimal.text = itemView.context.getString(R.string.animal_label, consulta.animalNome ?: "N/A")
            binding.textViewClinica.text = itemView.context.getString(R.string.clinica_label, consulta.clinicaNome ?: "N/A")
            binding.textViewVeterinario.text = itemView.context.getString(R.string.veterinario_label, consulta.veterinarioNome ?: "N/A")
            binding.textViewStatus.text = itemView.context.getString(R.string.status_label, consulta.estado)

            binding.buttonCancelar.setOnClickListener {
                onCancelClick(consulta)
            }
        }
    }

    class ConsultaDiffCallback : DiffUtil.ItemCallback<Consulta>() {
        override fun areItemsTheSame(oldItem: Consulta, newItem: Consulta): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Consulta, newItem: Consulta): Boolean {
            return oldItem == newItem
        }
    }
}