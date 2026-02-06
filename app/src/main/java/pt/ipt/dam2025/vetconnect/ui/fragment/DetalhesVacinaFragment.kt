package pt.ipt.dam2025.vetconnect.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import pt.ipt.dam2025.vetconnect.R
import pt.ipt.dam2025.vetconnect.databinding.FragmentDetalhesVacinaBinding
import pt.ipt.dam2025.vetconnect.model.Vacina
import pt.ipt.dam2025.vetconnect.viewmodel.VacinaViewModel

class DetalhesVacinaFragment : Fragment() {

    private var _binding: FragmentDetalhesVacinaBinding? = null
    private val binding get() = _binding!!

    private lateinit var vacinaViewModel: VacinaViewModel
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

        vacinaViewModel = ViewModelProvider(this).get(VacinaViewModel::class.java)

        arguments?.let {
            vacina = it.getParcelable<Vacina>("vacina")
        }

        vacina?.let {
            binding.textViewNomeVacinaDetalhe.text = it.tipoVacina
            binding.textViewEstadoVacinaDetalhe.text = it.estado
            binding.textViewDataAplicacaoDetalhe.text = it.data
            binding.textViewProximaDoseDetalhe.text = it.proximaDose
            binding.textViewVeterinarioVacinaDetalhe.text = it.veterinario
            binding.textViewLoteDetalhe.text = it.lote
        }

        binding.buttonApagarVacina.setOnClickListener {
            vacina?.let {
                val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", 0)
                val token = sharedPreferences.getString("token", null)
                if (token != null) {
                    vacinaViewModel.cancelarVacina(token, it.id)
                }
            }
        }

        binding.buttonEditarVacina.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("vacina", vacina)
            findNavController().navigate(R.id.action_detalhesVacinaFragment_to_editarVacinaFragment, bundle)
        }

        vacinaViewModel.operationStatus.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(context, "Vacina apagada com sucesso", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(context, "Falha ao apagar vacina", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}