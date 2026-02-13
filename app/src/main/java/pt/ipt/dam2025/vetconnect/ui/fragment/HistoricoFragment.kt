package pt.ipt.dam2025.vetconnect.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import pt.ipt.dam2025.vetconnect.R
import pt.ipt.dam2025.vetconnect.databinding.FragmentHistoricoBinding
import pt.ipt.dam2025.vetconnect.model.Exame
import pt.ipt.dam2025.vetconnect.ui.adapter.HistoricoAdapter
import pt.ipt.dam2025.vetconnect.util.SessionManager
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
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoricoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        // Inicializa o ViewModel
        val factory = HistoricoViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[HistoricoViewModel::class.java]

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
        val token = sessionManager.getAuthToken()
        val animalId = sessionManager.getAnimalId()

        if (token == null || animalId == -1) {
            Toast.makeText(context, "Sessão inválida. Por favor reinicie a aplicação", Toast.LENGTH_LONG).show()
            binding.emptyView.visibility = View.VISIBLE
            return
        }

        viewModel.getExames(token, animalId).observe(viewLifecycleOwner) {
            it?.let {
                historicoAdapter.updateData(it)
                binding.emptyView.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun navigateToDetalhes(exame: Exame) {
        val bundle = Bundle().apply {
            putParcelable("exame", exame)
        }
        findNavController().navigate(R.id.action_historicoFragment_to_detalhesExameFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}