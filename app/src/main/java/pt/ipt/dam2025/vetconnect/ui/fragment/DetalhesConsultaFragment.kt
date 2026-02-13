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
import pt.ipt.dam2025.vetconnect.R
import pt.ipt.dam2025.vetconnect.databinding.FragmentDetalhesConsultaBinding
import pt.ipt.dam2025.vetconnect.model.Consulta
import pt.ipt.dam2025.vetconnect.viewmodel.ConsultaViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.ConsultaViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Fragment para a página de detalhes de uma consulta
 */

class DetalhesConsultaFragment : Fragment() {

    private var _binding: FragmentDetalhesConsultaBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ConsultaViewModel
    private var consulta: Consulta? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalhesConsultaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ConsultaViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[ConsultaViewModel::class.java]

        @Suppress("DEPRECATION")
        consulta = arguments?.getParcelable("consulta")

        if (consulta == null) {
            Toast.makeText(context, "Erro ao carregar dados da consulta.", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        populateUi(consulta!!)
        setupListeners()
        observeViewModel()
    }

    private fun populateUi(consulta: Consulta) {
        binding.motivo.text = consulta.motivo
        binding.dataConsulta.text = formatDateForDisplay(consulta.data)
        binding.clinicaConsulta.text = consulta.clinicaNome
        binding.veterinarioConsulta.text = consulta.veterinarioNome
        binding.observacoesConsulta.text = consulta.observacoes

        binding.estadoConsulta.text = consulta.estado.uppercase(Locale.ROOT)
        val backgroundRes = when (consulta.estado.lowercase(Locale.ROOT)) {
            "marcada" -> R.drawable.background_estado_agendada
            "realizada" -> R.drawable.background_estado_administrada
            "cancelada" -> R.drawable.background_estado_atrasada
            else -> android.R.color.transparent
        }
        binding.estadoConsulta.setBackgroundResource(backgroundRes)
    }

    private fun setupListeners() {
        binding.btnEditar.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelable("consulta", consulta)
            }
            findNavController().navigate(R.id.action_detalhesConsultaFragment_to_editarConsultaFragment, bundle)
        }

        binding.buttonApagarVacina.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Cancelar Consulta")
                .setMessage("Tem a certeza que deseja cancelar esta consulta?")
                .setPositiveButton("Sim") { _, _ ->
                    val token = "seu_token_aqui" // TODO: Obter o token de forma segura
                    consulta?.let { viewModel.cancelarConsulta(token, it.id) }
                }
                .setNegativeButton("Não", null)
                .show()
        }
    }

    private fun observeViewModel() {
        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                 Toast.makeText(context, "Consulta cancelada com sucesso!", Toast.LENGTH_SHORT).show()
                 findNavController().popBackStack()
            }.onFailure { throwable ->
                Toast.makeText(context, "Erro ao cancelar consulta: ${throwable.message}", Toast.LENGTH_LONG).show()
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