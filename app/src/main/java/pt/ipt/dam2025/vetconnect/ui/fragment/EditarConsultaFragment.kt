package pt.ipt.dam2025.vetconnect.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import pt.ipt.dam2025.vetconnect.databinding.FragmentEditarConsultaBinding
import pt.ipt.dam2025.vetconnect.model.Clinica
import pt.ipt.dam2025.vetconnect.model.Consulta
import pt.ipt.dam2025.vetconnect.model.Veterinario
import pt.ipt.dam2025.vetconnect.viewmodel.ConsultaViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.ConsultaViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Fragment para a página de editar uma consulta
 */

class EditarConsultaFragment : Fragment() {

    private var _binding: FragmentEditarConsultaBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ConsultaViewModel
    private var consulta: Consulta? = null

    private val clinicasList = mutableListOf<Clinica>()
    private val veterinariosList = mutableListOf<Veterinario>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditarConsultaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ConsultaViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[ConsultaViewModel::class.java]

        // Obtém a consulta passada como argumento
        @Suppress("DEPRECATION")
        consulta = arguments?.getParcelable("consulta")

        if (consulta == null) {
            Toast.makeText(context, "Erro ao carregar dados da consulta.", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        setupListeners()
        observeLists()
        observeViewModel()
        populateUi(consulta!!)
    }

    private fun populateUi(consulta: Consulta) {
        binding.motivo.setText(consulta.motivo)
        binding.dataConsulta.setText(formatDateForDisplay(consulta.data))
        binding.observacoes.setText(consulta.observacoes) // Supondo que 'observacoes' existe no modelo Consulta
    }

    private fun setupListeners() {
        binding.dataConsulta.setOnClickListener { showDatePicker() }
        binding.btnGuardar.setOnClickListener { guardarAlteracoes() }

        binding.clinicaSpinner.setOnItemClickListener { _, _, position, _ ->
            clinicasList.getOrNull(position)?.let {
                viewModel.carregaVeterinarios(it.id)
            }
        }
    }

    private fun observeLists() {
        viewModel.clinicas.observe(viewLifecycleOwner) { clinicas ->
            clinicasList.clear()
            clinicasList.addAll(clinicas)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, clinicas.map { it.nome })
            binding.clinicaSpinner.setAdapter(adapter)

            // Pré-seleciona a clínica
            val clinicaAtual = clinicas.find { it.id == consulta?.clinicaId }
            if (clinicaAtual != null) {
                binding.clinicaSpinner.setText(clinicaAtual.nome, false)
                viewModel.carregaVeterinarios(clinicaAtual.id)
            }
        }

        viewModel.veterinarios.observe(viewLifecycleOwner) { veterinarios ->
            veterinariosList.clear()
            veterinariosList.addAll(veterinarios)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, veterinarios.map { it.nome })
            binding.veterinarioSpinner.setAdapter(adapter)

            // Pré-seleciona o veterinário
            val vetAtual = veterinarios.find { it.id == consulta?.veterinarioId }
            vetAtual?.let { binding.veterinarioSpinner.setText(it.nome, false) }
        }
    }

    private fun guardarAlteracoes() {
        val token = "seu_token_aqui" // TODO: Obter o token de forma segura

        val clinicaNome = binding.clinicaSpinner.text.toString()
        val veterinarioNome = binding.veterinarioSpinner.text.toString()

        val clinicaId = clinicasList.find { it.nome == clinicaNome }?.id
        val veterinarioId = veterinariosList.find { it.nome == veterinarioNome }?.id

        val data = binding.dataConsulta.text.toString()
        val motivo = binding.motivo.text.toString()
        val observacoes = binding.observacoes.text.toString()

        if (motivo.isBlank() || clinicaId == null || veterinarioId == null || data.isBlank()) {
            Toast.makeText(context, "Por favor, preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
            return
        }

        val apiDate = reformatDateForApi(data)
        if (apiDate == null) {
            Toast.makeText(context, "Formato de data inválido. Use dd-MM-yyyy.", Toast.LENGTH_SHORT).show()
            return
        }

        // TODO: Criar um UpdateConsultaRequest e uma função de update no ViewModel/Repository.
        Toast.makeText(context, "Função de atualizar ainda não implementada.", Toast.LENGTH_SHORT).show()
    }

    private fun observeViewModel() {
        // TODO: Observar o resultado da operação de update quando for implementada.
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val displayDate = String.format(Locale.ROOT, "%02d-%02d-%04d", dayOfMonth, month + 1, year)
                binding.dataConsulta.setText(displayDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun formatDateForDisplay(dateString: String?): String {
        if (dateString.isNullOrBlank()) return ""
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = parser.parse(dateString.substring(0, 10))
            val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            formatter.format(date!!)
        } catch (_: Exception) {
            dateString
        }
    }

    private fun reformatDateForApi(displayDate: String?): String? {
        if (displayDate.isNullOrBlank()) return null
        return try {
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(displayDate)?.let {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
            }
        } catch (_: Exception) {
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}