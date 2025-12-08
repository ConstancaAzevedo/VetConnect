package pt.ipt.dam2025.trabalho.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pt.ipt.dam2025.trabalho.databinding.ItemUsuarioBinding
import pt.ipt.dam2025.trabalho.model.Usuario

// Adiciona um listener como parâmetro do construtor
class UsuarioAdapter(private val onDeleteClickListener: (Usuario) -> Unit) : 
    ListAdapter<Usuario, UsuarioAdapter.UsuarioViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val binding = ItemUsuarioBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UsuarioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = getItem(position)
        // Passa o listener para o ViewHolder
        holder.bind(usuario, onDeleteClickListener)
    }

    class UsuarioViewHolder(private val binding: ItemUsuarioBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // O bind agora recebe o usuário e o listener
        fun bind(usuario: Usuario, onDeleteClickListener: (Usuario) -> Unit) {
            binding.textViewNome.text = usuario.nome
            binding.textViewEmail.text = usuario.email
            binding.textViewTelefone.text = usuario.telemovel ?: "Não informado"

            // Configura o clique do botão de apagar
            binding.btnApagar.setOnClickListener {
                onDeleteClickListener(usuario)
            }
        }
    }

    companion object DiffCallback :
        DiffUtil.ItemCallback<Usuario>() {
        override fun areItemsTheSame(oldItem: Usuario, newItem: Usuario): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Usuario, newItem: Usuario): Boolean {
            return oldItem == newItem
        }
    }
}
