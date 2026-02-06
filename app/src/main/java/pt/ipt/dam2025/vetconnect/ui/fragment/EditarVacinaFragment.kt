package pt.ipt.dam2025.vetconnect.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import pt.ipt.dam2025.vetconnect.databinding.FragmentEditarVacinaBinding
import pt.ipt.dam2025.vetconnect.model.UpdateVacinaRequest
import pt.ipt.dam2025.vetconnect.viewmodel.VacinaViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.VacinaViewModelFactory
import java.util.Calendar
import java.util.Locale

/**
 * Fragment da página para editar uma vacina
 */

class EditarVacinaFragment : Fragment() {

    private var _binding: FragmentEditarVacinaBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: VacinaViewModel
    private var vacinaId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditarVacinaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Corrige a criação do ViewModel
        val factory = VacinaViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory).get(VacinaViewModel::class.java)

        arguments?.let {
            vacinaId = it.getInt("vacinaId")
            // TODO: carregar os dados da vacina usando o ID e popular os campos
            // Por exemplo: viewModel.getVacinaById(vacinaId).observe... e depois popular
            // binding.nomeVacina.setText(vacina.nome) ... etc
        }

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.dataAplicacao.setOnClickListener { showDatePickerDialog(it as android.widget.EditText) }
        binding.proximaDose.setOnClickListener { showDatePickerDialog(it as android.widget.EditText) }

        binding.btnGuardar.setOnClickListener { guardarAlteracoes() }
        binding.btnApagar.setOnClickListener { confirmarApagar() }
    }

    private fun guardarAlteracoes() {
        val dataAplicacao = binding.dataAplicacao.text.toString()
        val proximaDose = binding.proximaDose.text.toString().takeIf { it.isNotBlank() }
        val lote = binding.lote.text.toString().takeIf { it.isNotBlank() }

        if (dataAplicacao.isBlank()) {
            Toast.makeText(context, "A data de aplicação é obrigatória.", Toast.LENGTH_SHORT).show()
            return
        }

        val request = UpdateVacinaRequest(dataAplicacao, proximaDose, lote)
        // TODO: Obter o token de forma segura
        val token = "seu_token_aqui"
        viewModel.updateVacina(token, vacinaId, request)
    }

    private fun confirmarApagar() {
        AlertDialog.Builder(requireContext())
            .setTitle("Apagar Vacina")
            .setMessage("Tem a certeza que deseja apagar esta vacina?")
            .setPositiveButton("Sim") { _, _ ->
                // TODO: Obter o token de forma segura
                val token = "seu_token_aqui"
                viewModel.cancelarVacina(token, vacinaId)
            }
            .setNegativeButton("Não", null)
            .show()
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

    private fun showDatePickerDialog(editText: android.widget.EditText) {
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val selectedDate = String.format(Locale.ROOT, "%04d-%02d-%02d", year, month + 1, dayOfMonth)
            editText.setText(selectedDate)
        }
        DatePickerDialog(
            requireContext(), dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
