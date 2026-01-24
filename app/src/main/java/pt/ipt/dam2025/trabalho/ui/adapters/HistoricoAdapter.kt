package pt.ipt.dam2025.trabalho.ui.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import pt.ipt.dam2025.trabalho.databinding.ItemHistoricoBinding
import pt.ipt.dam2025.trabalho.model.Documento
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// Adapter para a lista de documentos
class HistoricoAdapter(
    private var documentos: List<Documento>,
    private val onItemClick: (Documento) -> Unit
) : RecyclerView.Adapter<HistoricoAdapter.HistoricoViewHolder>() {

    // Formatador de data movido para aqui para maior eficiência e robustez
    private val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoricoViewHolder {
        val binding = ItemHistoricoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoricoViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: HistoricoViewHolder, position: Int) {
        holder.bind(documentos[position])
    }

    override fun getItemCount(): Int = documentos.size

    fun updateData(newDocumentos: List<Documento>) {
        documentos = newDocumentos
        notifyDataSetChanged() // Para uma otimização futura, considere usar DiffUtil
    }

    inner class HistoricoViewHolder(private val binding: ItemHistoricoBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick(documentos[adapterPosition])
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(documento: Documento) {
            binding.historicoData.text = try {
                // Usar a API moderna java.time para maior segurança e flexibilidade
                val dateTime = OffsetDateTime.parse(documento.data)
                dateTime.format(outputFormatter)
            } catch (e: Exception) {
                // Fallback seguro em caso de erro na análise da data
                documento.data.substringBefore('T') ?: "Data inválida"
            }
            binding.historicoDescricao.text = "${documento.tipo}: ${documento.nome}"
        }
    }
}
