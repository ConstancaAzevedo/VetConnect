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
import pt.ipt.dam2025.vetconnect.databinding.FragmentRegistarBinding
import pt.ipt.dam2025.vetconnect.viewmodel.UtilizadorViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.UtilizadorViewModelFactory

/**
 * Fragment para a página de registo do utilizador
 */

class RegistarFragment : Fragment() {

    private var _binding: FragmentRegistarBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: UtilizadorViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = UtilizadorViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory).get(UtilizadorViewModel::class.java)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnRegistar.setOnClickListener {
            val nome = binding.nameInput.text.toString().trim()
            val email = binding.emailInput.text.toString().trim()
            val telemovel = binding.phoneInput.text.toString().trim()

            if (nome.isEmpty() || email.isEmpty() || telemovel.isEmpty()) {
                Toast.makeText(context, "Todos os campos são obrigatórios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.adicionarUtilizador(nome, email, telemovel)
        }
    }

    private fun observeViewModel() {
        viewModel.registrationResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(context, "Código enviado parao seu telemóvel", Toast.LENGTH_LONG).show()
                val bundle = Bundle().apply {
                    putString("email", it.user.email)
                    putString("verificationCode", it.verificationCode)
                }
                findNavController().navigate(R.id.action_registarFragment_to_verificacaoFragment, bundle)
            }.onFailure {
                Toast.makeText(context, "Erro no registo: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
