package pt.ipt.dam2025.vetconnect.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import pt.ipt.dam2025.vetconnect.R
import pt.ipt.dam2025.vetconnect.databinding.FragmentEscolhaBinding

/**
 * Fragment para a página em que o utilizador qur tipo de perfil irá registar
 * O perfil de veterinário é apenas ilustrativo e não será implementado
 */
class EscolhaFragment : Fragment() {

    private var _binding: FragmentEscolhaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEscolhaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // botão tutor -> leva diretamente para a página de registo de tutor
        binding.btnTutor.setOnClickListener {
            findNavController().navigate(R.id.action_escolhaFragment_to_registarFragment)
        }

        // botão veterinário -> mostra uma mensagem de aviso
        binding.btnVeterinario.setOnClickListener { v ->
            Snackbar.make(v, "Funcionalidade em desenvolvimento", Snackbar.LENGTH_LONG).show()
        }

        //  botão login -> leva diretamente para a página de login
        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_escolhaFragment_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}