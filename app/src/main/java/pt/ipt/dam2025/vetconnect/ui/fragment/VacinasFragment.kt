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
import pt.ipt.dam2025.vetconnect.databinding.FragmentVacinasBinding
import pt.ipt.dam2025.vetconnect.model.Vacina
import pt.ipt.dam2025.vetconnect.ui.adapter.VacinaAdapter
import pt.ipt.dam2025.vetconnect.util.SessionManager
import pt.ipt.dam2025.vetconnect.viewmodel.VacinaViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.VacinaViewModelFactory

/**
 * Fragment para a página das vacinas de um animal
 */
class VacinasFragment : Fragment() {

    private var _binding: FragmentVacinasBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: VacinaViewModel
    private lateinit var vacinaAdapter: VacinaAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVacinasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        // Inicializa o ViewModel usando a Factory
        val factory = VacinaViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[VacinaViewModel::class.java]

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        // O adapter agora é um ListAdapter, não precisa de uma lista inicial
        vacinaAdapter = VacinaAdapter { vacina ->
            // Navega para os detalhes da vacina quando um item é clicado
            navigateToDetalhes(vacina)
        }
        binding.recyclerViewVacinas.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = vacinaAdapter
        }
    }

    private fun observeViewModel() {
        val token = sessionManager.getAuthToken()
        val animalId = sessionManager.getAnimalId()

        if (token == null || animalId == -1) {
            Toast.makeText(context, "Sessão inválida. Por favor, reinicie a aplicação.", Toast.LENGTH_LONG).show()
            binding.emptyView.visibility = View.VISIBLE
            return
        }

        // Observa o LiveData retornado pelo novo métdo getVacinas
        viewModel.getVacinas(token, animalId).observe(viewLifecycleOwner) { vacinas ->
            // Usamos submitList() para passar a nova lista ao ListAdapter
            vacinaAdapter.submitList(vacinas)
            binding.emptyView.visibility = if (vacinas.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun navigateToDetalhes(vacina: Vacina) {
        val bundle = Bundle().apply {
            putParcelable("vacina", vacina)
        }
        findNavController().navigate(R.id.action_vacinasFragment_to_detalhesVacinaFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}