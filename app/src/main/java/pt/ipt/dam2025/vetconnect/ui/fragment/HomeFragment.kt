package pt.ipt.dam2025.vetconnect.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pt.ipt.dam2025.vetconnect.R
import pt.ipt.dam2025.vetconnect.api.ApiClient
import pt.ipt.dam2025.vetconnect.databinding.FragmentHomeBinding
import pt.ipt.dam2025.vetconnect.ui.MainActivity
import pt.ipt.dam2025.vetconnect.util.SessionManager

/**
 * Fragment para a página home da aplicação
 */

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // inicializa o SessionManager
        sessionManager = SessionManager(requireContext())

        // le o ID do animal dos argumentos do fragmento e, se for válido, guarda na sessão
        val animalId = arguments?.getInt("ANIMAL_ID", -1) ?: -1
        if (animalId != -1) {
            sessionManager.saveAnimalId(animalId)
        }

        // configurar os listeners
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
        binding.btnLogout.setOnClickListener {
            performLogout()
        }

        // voltar à pagina de abertura quando se volta atrás
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })
    }

    // funçao de logout
    private fun performLogout() {
        val authToken = sessionManager.getAuthToken()
        lifecycleScope.launch {
            try {
                if (authToken != null) {
                    // garante que a chamada de logout não seja cancelada
                    withContext(NonCancellable) {
                        ApiClient.apiService.logout("Bearer $authToken")
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "Falha ao invalidar token no servidor", e)
            } finally {
                // limpa os dados da sessão
                sessionManager.clearAuth()
                // mostra a mensagem ao utilizador 
                Toast.makeText(requireContext(), "Sessão terminada", Toast.LENGTH_SHORT).show()
                // navega para o ecrã de login 
                findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}