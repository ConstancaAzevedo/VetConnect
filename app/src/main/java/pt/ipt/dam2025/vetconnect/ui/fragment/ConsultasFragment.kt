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
import pt.ipt.dam2025.vetconnect.databinding.FragmentConsultasBinding
import pt.ipt.dam2025.vetconnect.model.Consulta
import pt.ipt.dam2025.vetconnect.ui.adapter.ConsultasAdapter
import pt.ipt.dam2025.vetconnect.util.SessionManager
import pt.ipt.dam2025.vetconnect.viewmodel.ConsultaViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.ConsultaViewModelFactory

/**
 * Fragment para a página de consultas
 */
class ConsultasFragment : Fragment() {

    private var _binding: FragmentConsultasBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ConsultaViewModel
    private lateinit var consultaAdapter: ConsultasAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConsultasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        val factory = ConsultaViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[ConsultaViewModel::class.java]

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        consultaAdapter = ConsultasAdapter {
            navigateToDetalhes(it)
        }
        binding.recyclerViewConsultas.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = consultaAdapter
        }
    }

    private fun observeViewModel() {
        val token = sessionManager.getAuthToken()
        val userId = sessionManager.getUserId()

        if (token == null || userId == -1) {
            Toast.makeText(context, "Sessão inválida. Por favor, reinicie a aplicação.", Toast.LENGTH_LONG).show()
            binding.emptyView.visibility = View.VISIBLE
            return
        }

        viewModel.getConsultas(token, userId).observe(viewLifecycleOwner) { consultas ->
            consultaAdapter.submitList(consultas)
            binding.emptyView.visibility = if (consultas.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun navigateToDetalhes(consulta: Consulta) {
        val bundle = Bundle().apply {
            putParcelable("consulta", consulta)
        }
        findNavController().navigate(R.id.action_consultasFragment_to_detalhesConsultaFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}