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
import pt.ipt.dam2025.vetconnect.viewmodel.MarcarConsultaViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.MarcarConsultaViewModelFactory
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

    private lateinit var vacinaViewModel: VacinaViewModel
    private lateinit var consultaViewModel: MarcarConsultaViewModel

    private var listaTiposVacina: List<TipoVacina> = emptyList()
    private var listaClinicas: List<Clinica> = emptyList()
    private var listaVeterinarios: List<Veterinario> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAgendarVacinaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vacinaFactory = VacinaViewModelFactory(requireActivity().application)
        vacinaViewModel = ViewModelProvider(this, vacinaFactory).get(VacinaViewModel::class.java)

        val consultaFactory = MarcarConsultaViewModelFactory(requireActivity().application)
        consultaViewModel = ViewModelProvider(this, consultaFactory).get(MarcarConsultaViewModel::class.java)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        setupDateTimePickers()
        setupSpinnerListeners()
        binding.btnConfirmar.setOnClickListener { agendarVacina() }
    }

    private fun observeViewModel() {
        vacinaViewModel.getTiposVacina().observe(viewLifecycleOwner) { tipos ->
            listaTiposVacina = tipos
            val nomes = listOf("Selecione o tipo de vacina") + tipos.map { it.nome }
            binding.vacinaSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nomes)
            binding.vacinaSpinner.isEnabled = true
        }

        consultaViewModel.clinicas.observe(viewLifecycleOwner) { clinicas ->
            listaClinicas = clinicas
            val nomes = listOf("Selecione uma clínica") + clinicas.map { it.nome }
            binding.clinicaSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nomes)
            binding.clinicaSpinner.isEnabled = true
        }
    }

    private fun setupSpinnerListeners() {
        binding.clinicaSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    val clinicaId = listaClinicas[position - 1].id
                    consultaViewModel.getVeterinariosPorClinica(clinicaId).observe(viewLifecycleOwner) { veterinarios ->
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
        val data = binding.data.text.toString()
        val hora = binding.hora.text.toString()
        val observacoes = binding.observacoes.text.toString()

        if (vacinaPos <= 0 || clinicaPos <= 0 || vetPos <= 0 || data.isBlank() || hora.isBlank()) {
            Toast.makeText(context, "Por favor, preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
            return
        }

        val tipoVacinaId = listaTiposVacina[vacinaPos - 1].id
        val clinicaId = listaClinicas[clinicaPos - 1].id
        val veterinarioId = listaVeterinarios[vetPos - 1].id
        val dataHora = "${data}T${hora}"

        val request = AgendarVacinaRequest(animalId, tipoVacinaId, clinicaId, veterinarioId, dataHora, observacoes)

        vacinaViewModel.agendarVacina(token, request)
    }

    private fun setupDateTimePickers() {
        val calendar = Calendar.getInstance()
        binding.data.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, day ->
                binding.data.setText(String.format(Locale.ROOT, "%04d-%02d-%02d", year, month + 1, day))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
        binding.hora.setOnClickListener {
            TimePickerDialog(this.context, { _, hour, minute ->
                binding.hora.setText(String.format(Locale.ROOT, "%02d:%02d:00", hour, minute))
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
