package pt.ipt.dam2025.vetconnect.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import pt.ipt.dam2025.vetconnect.R
import pt.ipt.dam2025.vetconnect.databinding.FragmentCriarPinBinding
import pt.ipt.dam2025.vetconnect.viewmodel.UtilizadorViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.UtilizadorViewModelFactory

/**
 * Fragmetn para a página de criar um pin
 */

class CriarPinFragment : Fragment() {

    private var _binding: FragmentCriarPinBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: UtilizadorViewModel
    private val pin = StringBuilder()
    private lateinit var pinDots: List<ImageView>
    private lateinit var userEmail: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCriarPinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = UtilizadorViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory).get(UtilizadorViewModel::class.java)

        userEmail = arguments?.getString("email") ?: ""

        if (userEmail.isEmpty()) {
            Toast.makeText(context, "Erro: Email não encontrado", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        pinDots = listOf(
            binding.pin1, binding.pin2, binding.pin3,
            binding.pin4, binding.pin5, binding.pin6
        )
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        val numberButtonClickListener = View.OnClickListener { view ->
            if (pin.length < 6) {
                pin.append((view as Button).text)
                updatePinDots()
                if (pin.length == 6) {
                    viewModel.criarPin(userEmail, pin.toString())
                }
            }
        }

        val buttons = listOf<Button>(
            binding.button1, binding.button2, binding.button3,
            binding.button4, binding.button5, binding.button6,
            binding.button7, binding.button8, binding.button9, binding.button0
        )
        buttons.forEach { it.setOnClickListener(numberButtonClickListener) }

        binding.btnDelete.setOnClickListener {
            if (pin.isNotEmpty()) {
                pin.deleteCharAt(pin.length - 1)
                updatePinDots()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.createPinResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(context, "PIN criado com sucesso!", Toast.LENGTH_SHORT).show()
                // TODO: Adicionar o utilizador à lista de contas locais, se necessário
                findNavController().navigate(R.id.action_criarPinFragment_to_homeFragment)
            }.onFailure {
                Toast.makeText(context, "Erro ao criar PIN: ${it.message}", Toast.LENGTH_LONG).show()
                pin.clear()
                updatePinDots()
            }
        }
    }

    private fun updatePinDots() {
        for (i in pinDots.indices) {
            pinDots[i].setImageResource(
                if (i < pin.length) R.drawable.ic_pin_dot_depois
                else R.drawable.ic_pin_dot_antes
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
