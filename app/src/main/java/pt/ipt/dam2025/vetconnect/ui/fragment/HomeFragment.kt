package pt.ipt.dam2025.vetconnect.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import pt.ipt.dam2025.vetconnect.R
import pt.ipt.dam2025.vetconnect.databinding.FragmentHomeBinding
import pt.ipt.dam2025.vetconnect.util.SessionManager
import pt.ipt.dam2025.vetconnect.viewmodel.UtilizadorViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.UtilizadorViewModelFactory

/**
 * Fragment para a página home da aplicação
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: UtilizadorViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // inicializa o SessionManager e o ViewModel
        sessionManager = SessionManager(requireContext())
        val factory = UtilizadorViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[UtilizadorViewModel::class.java]

        // le o ID do animal dos argumentos do fragmento e, se for válido, guarda na sessão
        val animalId = arguments?.getInt("ANIMAL_ID", -1) ?: -1
        if (animalId != -1) {
            sessionManager.saveAnimalId(animalId)
        }

        setupListeners()

        // voltar à página de abertura quando se volta atrás
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish() // fecha a aplicação
            }
        })
    }

    private fun setupListeners() {
        binding.cardMarcarConsulta.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_marcarConsultaFragment)
        }
        binding.cardAgendarVacina.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_agendarVacinaFragment)
        }
        binding.cardConsultas.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_consultasFragment)
        }
        binding.cardMinhasVacinas.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_vacinasFragment)
        }
        binding.cardAnimal.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_animalFragment)
        }
        binding.cardHistorico.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_historicoFragment)
        }
        binding.cardPerfil.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_perfilFragment)
        }
        binding.btnDefinicoes.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_definicoesFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}