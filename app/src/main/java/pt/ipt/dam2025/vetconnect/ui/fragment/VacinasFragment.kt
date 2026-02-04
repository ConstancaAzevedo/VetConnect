package pt.ipt.dam2025.vetconnect.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import pt.ipt.dam2025.vetconnect.databinding.FragmentVacinasBinding
import pt.ipt.dam2025.vetconnect.ui.adapter.VacinaAdapter
import pt.ipt.dam2025.vetconnect.viewmodel.VacinaViewModel
import pt.ipt.dam2025.vetconnect.util.SessionManager

/**
 * Fragment para a página das vacinas já registadas
 */
class VacinasFragment : Fragment() {

    private var _binding: FragmentVacinasBinding? = null
    private val binding get() = _binding!!

    // obtém uma instância do ViewModel partilhada com a Activity
    private val viewModel: VacinaViewModel by activityViewModels()
    private lateinit var vacinaAdapter: VacinaAdapter


    // cria a view do fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVacinasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        vacinaAdapter = VacinaAdapter(mutableListOf()) { vacina ->
            Toast.makeText(context, "Apagar vacina: ${vacina.tipo}", Toast.LENGTH_SHORT).show()
            // TODO: Chamar a função do ViewModel para apagar a vacina
        }

        // configura o RecyclerView
        binding.recyclerViewVacinas.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = vacinaAdapter
        }

        // observa o LiveData de vacinas no ViewModel
        viewModel.vacinas.observe(viewLifecycleOwner) { vacinas ->
            vacinas?.let { vacinaAdapter.updateData(it) }
        }

        // observa o LiveData de erros no ViewModel
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearErrorMessage() // limpa o erro depois de o mostrar
            }
        }

        // A lógica do token e ID é tratada pelo ApiClient e SessionManager
        // Apenas chamamos o método do ViewModel
        val animalId = sessionManager.getAnimalId()
        if (animalId != -1) {
            viewModel.fetchVacinasAgendadas(animalId)
        } else {
            Toast.makeText(context, "ID do animal não encontrado.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}