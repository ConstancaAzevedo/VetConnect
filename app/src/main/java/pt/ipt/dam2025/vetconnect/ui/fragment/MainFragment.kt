package pt.ipt.dam2025.vetconnect.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import pt.ipt.dam2025.vetconnect.R
import pt.ipt.dam2025.vetconnect.databinding.FragmentMainBinding
import pt.ipt.dam2025.vetconnect.ui.activity.AboutActivity
import pt.ipt.dam2025.vetconnect.ui.activity.EscolhaActivity

/**
 * Fragment para a página principal
 */

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // botão login -> leva diretamente para a página login
        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
        }

        // botão registar -> leva diretamente para a página de escolha de perfil
        binding.btnRegistar.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_registarFragment)        }

        // botão about -> leva diretamente para a página about
        binding.btnAbout.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_aboutFragment)        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
