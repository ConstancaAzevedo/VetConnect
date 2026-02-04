package pt.ipt.dam2025.vetconnect.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pt.ipt.dam2025.vetconnect.databinding.ItemVacinasBinding
import pt.ipt.dam2025.vetconnect.model.Vacina

/**
 * Adapter para a lista de vacinas
 */
class VacinaAdapter(
    private var vacinas: MutableList<Vacina>,
    private val onApagarClick: (Vacina) -> Unit // função a ser chamada quando o botão de apagar é clicado
) : RecyclerView.Adapter<VacinaAdapter.VacinaViewHolder>() {

     // ViewHolder que contém as referências para as Views de cada item
    inner class VacinaViewHolder(val binding: ItemVacinasBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VacinaViewHolder {
        val binding = ItemVacinasBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VacinaViewHolder(binding)
    }

    // atualiza a view com os dados da vacina
    override fun onBindViewHolder(holder: VacinaViewHolder, position: Int) {
        val vacina = vacinas[position] // obtém a vacina na posição atual
        holder.binding.vacinaTipo.text = vacina.tipo // define o texto do tipo de vacina
        holder.binding.vacinaData.text = "Aplicada em: ${vacina.dataAplicacao}"

        // define o que acontece quando se clica no botão de apagar
        holder.binding.buttonApagarVacina.setOnClickListener {
            onApagarClick(vacina)
        }
    }

    // retorna o número de itens na lista
    override fun getItemCount() = vacinas.size


     // função para atualizar a lista de vacinas e notificar o adapter da mudança
    fun updateData(newVacinas: List<Vacina>) {
        this.vacinas = newVacinas.toMutableList()
        notifyDataSetChanged()
    }
}