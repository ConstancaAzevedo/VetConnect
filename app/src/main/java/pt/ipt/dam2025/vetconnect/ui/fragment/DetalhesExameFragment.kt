package pt.ipt.dam2025.vetconnect.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import coil.load
import pt.ipt.dam2025.vetconnect.R
import pt.ipt.dam2025.vetconnect.databinding.FragmentDetalhesExameBinding
import pt.ipt.dam2025.vetconnect.model.Exame
import pt.ipt.dam2025.vetconnect.util.SessionManager
import pt.ipt.dam2025.vetconnect.viewmodel.HistoricoViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.HistoricoViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Fragment para a página de detalhes de um exame
 */

class DetalhesExameFragment : Fragment() {

    private var _binding: FragmentDetalhesExameBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HistoricoViewModel
    private lateinit var sessionManager: SessionManager
    private var exame: Exame? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalhesExameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        val factory = HistoricoViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[HistoricoViewModel::class.java]

        @Suppress("DEPRECATION")
        exame = arguments?.getParcelable("exame")

        if (exame == null) {
            Toast.makeText(context, "Erro ao carregar dados do exame.", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        populateUi(exame!!)
        setupListeners()
        observeViewModel()
    }

    private fun populateUi(exame: Exame) {
        binding.textViewTipoExameDetalhe.text = exame.tipo
        binding.textViewDataExameDetalhe.text = formatDateForDisplay(exame.dataExame)
        binding.textViewClinicaDetalhe.text = exame.clinicaNome
        binding.textViewVeterinarioDetalhe.text = exame.veterinarioNome

        // Mostra o resultado se existir
        if (!exame.resultado.isNullOrBlank()) {
            binding.layoutResultadoDetalhe.visibility = View.VISIBLE
            binding.textViewResultadoDetalhe.text = exame.resultado
        }

        // Mostra as observações se existirem
        if (!exame.observacoes.isNullOrBlank()) {
            binding.layoutObservacoesDetalhe.visibility = View.VISIBLE
            binding.textViewObservacoesDetalhe.text = exame.observacoes
        }

        // Mostra a foto se existir
        if (!exame.ficheiroUrl.isNullOrBlank()) {
            binding.layoutFotoDetalhe.visibility = View.VISIBLE
            binding.imageViewRelatorioDetalhe.load(exame.ficheiroUrl) {
                placeholder(R.drawable.vetconnectfundo)
                error(R.drawable.vetconnectfundo)
            }
        }
    }

    private fun setupListeners() {
        // lógica para o botão Editar
        binding.btnEditar.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelable("exame", exame)
            }
            findNavController().navigate(R.id.action_detalhesExameFragment_to_editarExameFragment, bundle)
        }

        // lógica para o botão Apagar
        binding.btnApagar.setOnClickListener { // ID do XML
            AlertDialog.Builder(requireContext())
                .setTitle("Apagar Exame")
                .setMessage("Tem a certeza que deseja apagar este exame? Esta ação é irreversível.")
                .setPositiveButton("Sim") { _, _ ->
                    val token = sessionManager.getAuthToken()
                    if (token == null) {
                        Toast.makeText(context, "Sessão inválida. Não é possível apagar.", Toast.LENGTH_LONG).show()
                        return@setPositiveButton
                    }
                    exame?.let {
                        viewModel.deleteExame(token, it.animalId, it.id.toLong())
                    }
                }
                .setNegativeButton("Não", null)
                .show()
        }
    }

    private fun observeViewModel() {
        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(context, "Exame apagado com sucesso!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }.onFailure { throwable ->
                Toast.makeText(context, "Erro ao apagar exame: ${throwable.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun formatDateForDisplay(dateString: String?): String {
        if (dateString.isNullOrBlank()) return ""
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = parser.parse(dateString.substring(0, 10))
            val formatter = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("pt", "PT"))
            formatter.format(date!!)
        } catch (e: Exception) {
            dateString
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}