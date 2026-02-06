package pt.ipt.dam2025.vetconnect.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import pt.ipt.dam2025.vetconnect.R
import pt.ipt.dam2025.vetconnect.databinding.FragmentHistoricoBinding
import pt.ipt.dam2025.vetconnect.model.Exame
import pt.ipt.dam2025.vetconnect.ui.adapter.HistoricoAdapter
import pt.ipt.dam2025.vetconnect.viewmodel.HistoricoViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.HistoricoViewModelFactory

/**
 * Fragment para exibir o histórico de exames de um animal
 */
class HistoricoFragment : Fragment() {

    private var _binding: FragmentHistoricoBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HistoricoViewModel
    private lateinit var historicoAdapter: HistoricoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoricoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializa o ViewModel
        val factory = HistoricoViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory).get(HistoricoViewModel::class.java)

        // Configura o RecyclerView e o Adapter
        setupRecyclerView()

        // Observa as alterações na lista de exames
        observeExames()

        // Configura o clique do botão para navegar para o ecrã de adicionar exame
        binding.fabAddExame.setOnClickListener {
            findNavController().navigate(R.id.action_historicoFragment_to_adicionarExameFragment)
        }
    }

    private fun setupRecyclerView() {
        historicoAdapter = HistoricoAdapter(emptyList()) { exame ->
            navigateToDetalhes(exame)
        }
        binding.recyclerViewHistorico.apply {
            adapter = historicoAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeExames() {
        // Obter o token e o ID do animal (assumindo que são passados para este fragmento)
        // TODO: Substituir pelos valores reais
        val token = "seu_token_aqui"
        val animalId = 1 // ID do animal de exemplo

        viewModel.getExames(token, animalId).observe(viewLifecycleOwner) {
            it?.let {
                historicoAdapter.updateData(it)
                binding.emptyView.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun navigateToDetalhes(exame: Exame) {
        val bundle = Bundle().apply {
            // O ideal é que a classe Exame seja Parcelable para passar o objeto inteiro
            // Como alternativa, passamos os IDs
            putInt("exameId", exame.id)
            putInt("animalId", exame.animalId)
        }
        findNavController().navigate(R.id.action_historicoFragment_to_detalhesExameFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
