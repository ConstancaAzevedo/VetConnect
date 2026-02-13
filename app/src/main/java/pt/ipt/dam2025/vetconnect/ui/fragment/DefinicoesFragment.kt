package pt.ipt.dam2025.vetconnect.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import pt.ipt.dam2025.vetconnect.R
import pt.ipt.dam2025.vetconnect.databinding.FragmentDefinicoesBinding
import pt.ipt.dam2025.vetconnect.util.SessionManager
import pt.ipt.dam2025.vetconnect.viewmodel.UtilizadorViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.UtilizadorViewModelFactory

class DefinicoesFragment : Fragment() {

    private var _binding: FragmentDefinicoesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: UtilizadorViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDefinicoesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        val factory = UtilizadorViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[UtilizadorViewModel::class.java]

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnGuardar.setOnClickListener {
            alterarPin()
        }
        binding.btnLogout.setOnClickListener {
            confirmarLogout()
        }
    }

    private fun alterarPin() {
        val pinAtual = binding.atualPin.text.toString()
        val novoPin = binding.novoPin.text.toString()
        val confirmarPin = binding.confirmarPin.text.toString()

        // Validações
        if (pinAtual.length != 6) {
            binding.atualPin.error = "O PIN atual deve ter 6 dígitos."
            return
        }
        if (novoPin.length != 6) {
            binding.novoPin.error = "O novo PIN deve ter 6 dígitos."
            return
        }
        if (novoPin != confirmarPin) {
            binding.confirmarPin.error = "Os novos PINs não coincidem."
            return
        }

        val token = sessionManager.getAuthToken()
        if (token == null) {
            Toast.makeText(context, "Sessão inválida. Não é possível alterar o PIN.", Toast.LENGTH_LONG).show()
            return
        }
        viewModel.alterarPin(token, pinAtual, novoPin)
    }

    private fun confirmarLogout() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Tem a certeza que deseja terminar a sessão?")
            .setPositiveButton("Sim") { _, _ ->
                fazerLogout()
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun fazerLogout(){
        val token = sessionManager.getAuthToken()
        if (token == null) {
            Toast.makeText(context, "Sessão inválida. Não é possível fazer logout.", Toast.LENGTH_LONG).show()
            return
        }
        viewModel.logout(token)
    }

    private fun observeViewModel() {
        viewModel.pinChangeResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(context, "PIN alterado com sucesso!", Toast.LENGTH_SHORT).show()
                binding.atualPin.text.clear()
                binding.novoPin.text.clear()
                binding.confirmarPin.text.clear()
            }.onFailure { throwable ->
                Toast.makeText(context, "Erro ao alterar PIN: ${throwable.message}", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.logoutResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(context, "Sessão terminada", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_definicoesFragment_to_loginFragment)
            }.onFailure { throwable ->
                Toast.makeText(context, "Erro ao fazer logout: ${throwable.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}