package pt.ipt.dam2025.vetconnect.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import pt.ipt.dam2025.vetconnect.R
import pt.ipt.dam2025.vetconnect.databinding.FragmentDetalhesVacinaBinding
import pt.ipt.dam2025.vetconnect.model.Vacina
import pt.ipt.dam2025.vetconnect.viewmodel.VacinaViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.VacinaViewModelFactory

/**
 * Fragment para a página de detalhes de uma vacina
 */

class DetalhesVacinaFragment : Fragment() {

    private var _binding: FragmentDetalhesVacinaBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: VacinaViewModel
    private var vacina: Vacina? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalhesVacinaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = VacinaViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory).get(VacinaViewModel::class.java)

        vacina = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("vacina", Vacina::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable("vacina")
        }

        if (vacina == null) {
            Toast.makeText(context, "Erro ao carregar dados da vacina.", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        populateUi(vacina!!)
        setupListeners(vacina!!)
        observeViewModel()
    }

    private fun populateUi(vacina: Vacina) {
        binding.textViewNomeVacinaDetalhe.text = vacina.tipo
        binding.textViewEstadoVacinaDetalhe.text = vacina.estado
        binding.textViewDataAplicacaoDetalhe.text = vacina.dataAplicacao ?: "N/A"
        binding.textViewProximaDoseDetalhe.text = vacina.dataProxima ?: "N/A"
        binding.textViewVeterinarioVacinaDetalhe.text = vacina.veterinario ?: "N/A"
        binding.textViewLoteDetalhe.text = vacina.lote ?: "N/A"
    }

    private fun setupListeners(vacina: Vacina) {
        binding.buttonApagarVacina.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Apagar Vacina")
                .setMessage("Tem a certeza que deseja apagar esta vacina?")
                .setPositiveButton("Sim") { _, _ ->
                    // TODO: Obter o token de forma segura
                    val token = "seu_token_aqui"
                    viewModel.cancelarVacina(token, vacina.id)
                }
                .setNegativeButton("Não", null)
                .show()
        }

        binding.buttonEditarVacina.setOnClickListener {
            val bundle = Bundle().apply { putParcelable("vacina", vacina) }
            findNavController().navigate(R.id.action_detalhesVacinaFragment_to_editarVacinaFragment, bundle)
        }
    }

    private fun observeViewModel() {
        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(context, "Operação realizada com sucesso!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }.onFailure {
                Toast.makeText(context, "Falha na operação: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
