package pt.ipt.dam2025.vetconnect.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import pt.ipt.dam2025.vetconnect.databinding.FragmentDefinicoesBinding
import pt.ipt.dam2025.vetconnect.viewmodel.UsuarioViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.UsuarioViewModelFactory

class DefinicoesFragment : Fragment() {

    private var _binding: FragmentDefinicoesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: UsuarioViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDefinicoesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = UsuarioViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory).get(UsuarioViewModel::class.java)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnGuardar.setOnClickListener {
            alterarPin()
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

        // TODO: Obter o token de forma segura
        val token = "seu_token_aqui"
        viewModel.alterarPin(token, pinAtual, novoPin)
    }

    private fun observeViewModel() {
        viewModel.pinChangeResult.observe(viewLifecycleOwner) {
            it.onSuccess {
                Toast.makeText(context, "PIN alterado com sucesso!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }.onFailure {
                Toast.makeText(context, "Erro ao alterar PIN: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
