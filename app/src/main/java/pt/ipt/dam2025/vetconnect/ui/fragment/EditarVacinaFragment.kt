package pt.ipt.dam2025.vetconnect.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import pt.ipt.dam2025.vetconnect.databinding.FragmentEditarVacinaBinding
import pt.ipt.dam2025.vetconnect.model.UpdateVacinaRequest
import pt.ipt.dam2025.vetconnect.model.Vacina
import pt.ipt.dam2025.vetconnect.viewmodel.VacinaViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditarVacinaFragment : Fragment() {

    private var _binding: FragmentEditarVacinaBinding? = null
    private val binding get() = _binding!!

    private lateinit var vacinaViewModel: VacinaViewModel
    private var vacina: Vacina? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditarVacinaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vacinaViewModel = ViewModelProvider(this).get(VacinaViewModel::class.java)

        arguments?.let {
            vacina = it.getParcelable("vacina")
        }

        vacina?.let {
            binding.nomeVacina.setText(it.nome)
            binding.dataAplicacao.setText(it.data)
            binding.proximaDose.setText(it.proxima_dose)
            binding.veterinario.setText(it.veterinario)
            binding.lote.setText(it.lote)
        }

        binding.dataAplicacao.setOnClickListener { showDatePickerDialog(binding.dataAplicacao) }
        binding.proximaDose.setOnClickListener { showDatePickerDialog(binding.proximaDose) }

        binding.btnGuardar.setOnClickListener {
            val nome = binding.nomeVacina.text.toString()
            val data = binding.dataAplicacao.text.toString()
            val proximaDose = binding.proximaDose.text.toString()
            val veterinario = binding.veterinario.text.toString()
            val lote = binding.lote.text.toString()

            if (nome.isNotEmpty() && data.isNotEmpty()) {
                vacina?.let {
                    val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", 0)
                    val token = sharedPreferences.getString("token", null)
                    if (token != null) {
                        val request = UpdateVacinaRequest(nome, data, proximaDose, veterinario, lote)
                        vacinaViewModel.updateVacina(token, it.id, request)
                    }
                }
            } else {
                Toast.makeText(context, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
            }
        }

        vacinaViewModel.operationStatus.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(context, "Vacina atualizada com sucesso", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(context, "Falha ao atualizar vacina", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePickerDialog(editText: android.widget.EditText) {
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val format = "yyyy-MM-dd"
            val sdf = SimpleDateFormat(format, Locale.UK)
            editText.setText(sdf.format(calendar.time))
        }
        DatePickerDialog(requireContext(), dateSetListener, 
            calendar.get(Calendar.YEAR), 
            calendar.get(Calendar.MONTH), 
            calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}