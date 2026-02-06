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
import pt.ipt.dam2025.vetconnect.databinding.FragmentMarcarConsultaBinding
import pt.ipt.dam2025.vetconnect.model.Clinica
import pt.ipt.dam2025.vetconnect.model.NovaConsulta
import pt.ipt.dam2025.vetconnect.model.Veterinario
import pt.ipt.dam2025.vetconnect.viewmodel.ConsultaViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.ConsultaViewModelFactory
import java.util.Calendar
import java.util.Locale

/**
 * Fragment para a página de marcar consulta
 */
class MarcarConsultaFragment : Fragment() {

    private var _binding: FragmentMarcarConsultaBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ConsultaViewModel
    private var listaClinicas: List<Clinica> = emptyList()
    private var listaVeterinarios: List<Veterinario> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarcarConsultaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ConsultaViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory).get(ConsultaViewModel::class.java)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        setupDateTimePickers()
        setupSpinnerListeners()
        binding.btnConfirmar.setOnClickListener { marcarConsulta() }
    }

    private fun observeViewModel() {
        viewModel.clinicas.observe(viewLifecycleOwner) { clinicas ->
            listaClinicas = clinicas
            val nomesClinicas = listOf("Selecione uma clínica") + clinicas.map { it.nome }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nomesClinicas)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.clinicaSpinner.adapter = adapter
            binding.clinicaSpinner.isEnabled = true
        }

        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(context, "Consulta marcada com sucesso!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }.onFailure {
                Toast.makeText(context, "Erro ao marcar consulta: ${it.message}", Toast.LENGTH_LONG).show()
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
                        val nomesVeterinarios = listOf("Selecione um veterinário") + veterinarios.map { it.nome }
                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nomesVeterinarios)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.veterinarioSpinner.adapter = adapter
                        binding.veterinarioSpinner.isEnabled = true
                    }
                } else {
                    binding.veterinarioSpinner.adapter = null
                    binding.veterinarioSpinner.isEnabled = false
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }
    }

    private fun marcarConsulta() {
        // TODO: Obter o ID do animal e o token de forma segura
        val animalId = 1
        val token = "seu_token_aqui"

        val clinicaPosition = binding.clinicaSpinner.selectedItemPosition
        val vetPosition = binding.veterinarioSpinner.selectedItemPosition
        val data = binding.data.text.toString()
        val hora = binding.hora.text.toString()
        val motivo = binding.topico.text.toString()

        if (clinicaPosition <= 0 || vetPosition <= 0 || data.isBlank() || hora.isBlank() || motivo.isBlank()) {
            Toast.makeText(context, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val clinicaId = listaClinicas[clinicaPosition - 1].id
        val veterinarioId = listaVeterinarios[vetPosition - 1].id

        val novaConsulta = NovaConsulta(animalId, clinicaId, veterinarioId, data, hora, motivo)
        viewModel.marcarConsulta(token, novaConsulta)
    }

    private fun setupDateTimePickers() {
        val calendar = Calendar.getInstance()
        binding.data.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, day ->
                binding.data.setText(String.format(Locale.ROOT, "%04d-%02d-%02d", year, month + 1, day))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
        binding.hora.setOnClickListener {
            TimePickerDialog(requireContext(), { _, hour, minute ->
                binding.hora.setText(String.format(Locale.ROOT, "%02d:%02d:00", hour, minute))
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
