package pt.ipt.dam2025.vetconnect.ui.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import pt.ipt.dam2025.vetconnect.databinding.FragmentAgendarVacinaBinding
import pt.ipt.dam2025.vetconnect.model.AgendarVacinaRequest
import pt.ipt.dam2025.vetconnect.model.Clinica
import pt.ipt.dam2025.vetconnect.model.TipoVacina
import pt.ipt.dam2025.vetconnect.model.Veterinario
import pt.ipt.dam2025.vetconnect.viewmodel.VacinaViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.VacinaViewModelFactory
import java.util.Calendar
import java.util.Locale

/**
 * Fragment para a página de agendar vacinas
 */

class AgendarVacinaFragment : Fragment() {

    private var _binding: FragmentAgendarVacinaBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: VacinaViewModel

    private var listaTiposVacina: List<TipoVacina> = emptyList()
    private var listaClinicas: List<Clinica> = emptyList()
    private var listaVeterinarios: List<Veterinario> = emptyList()
    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAgendarVacinaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = VacinaViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory).get(VacinaViewModel::class.java)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.dataHora.setOnClickListener { showDateTimePicker() }
        setupSpinnerListeners()
        binding.btnConfirmar.setOnClickListener { agendarVacina() }
    }

    private fun observeViewModel() {
        viewModel.getTiposVacina().observe(viewLifecycleOwner) { tipos ->
            listaTiposVacina = tipos
            val nomes = listOf("Selecione o tipo de vacina") + tipos.map { it.nome }
            binding.vacinaSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nomes)
            binding.vacinaSpinner.isEnabled = true
        }

        viewModel.getClinicas().observe(viewLifecycleOwner) { clinicas ->
            listaClinicas = clinicas
            val nomes = listOf("Selecione uma clínica") + clinicas.map { it.nome }
            binding.clinicaSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nomes)
            binding.clinicaSpinner.isEnabled = true
        }

        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(context, "Vacina agendada com sucesso!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }.onFailure {
                Toast.makeText(context, "Erro ao agendar vacina: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupSpinnerListeners() {
        binding.clinicaSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    val clinicaId = listaClinicas[position - 1].id
                    viewModel.getVeterinariosPorClinica(clinicaId).observe(viewLifecycleOwner) { veterinarios ->
                        listaVeterinarios = veterinarios
                        val nomes = listOf("Selecione um veterinário") + veterinarios.map { it.nome }
                        binding.veterinarioSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nomes)
                        binding.veterinarioSpinner.isEnabled = true
                    }
                } else {
                    binding.veterinarioSpinner.adapter = null
                    binding.veterinarioSpinner.isEnabled = false
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun agendarVacina() {
        // TODO: Obter o ID do animal e o token de forma segura
        val animalId = 1
        val token = "seu_token_aqui"

        val vacinaPos = binding.vacinaSpinner.selectedItemPosition
        val clinicaPos = binding.clinicaSpinner.selectedItemPosition
        val vetPos = binding.veterinarioSpinner.selectedItemPosition
        val dataHora = binding.dataHora.text.toString()
        val observacoes = binding.observacoes.text.toString().takeIf { it.isNotBlank() }

        if (vacinaPos <= 0 || clinicaPos <= 0 || vetPos <= 0 || dataHora.isBlank()) {
            Toast.makeText(context, "Por favor, preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
            return
        }

        val tipoVacinaId = listaTiposVacina[vacinaPos - 1].id
        val clinicaId = listaClinicas[clinicaPos - 1].id
        val veterinarioId = listaVeterinarios[vetPos - 1].id

        val request = AgendarVacinaRequest(animalId, tipoVacinaId, clinicaId, veterinarioId, dataHora, observacoes)

        viewModel.agendarVacina(token, request)
    }

    private fun showDateTimePicker() {
        DatePickerDialog(requireContext(), { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            TimePickerDialog(requireContext(), { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                val format = String.format(Locale.ROOT, "%04d-%02d-%02dT%02d:%02d:00", year, month + 1, day, hour, minute)
                binding.dataHora.setText(format)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
