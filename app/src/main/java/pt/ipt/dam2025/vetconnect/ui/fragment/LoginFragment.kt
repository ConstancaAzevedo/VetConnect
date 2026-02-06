package pt.ipt.dam2025.vetconnect.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import pt.ipt.dam2025.vetconnect.R
import pt.ipt.dam2025.vetconnect.databinding.FragmentLoginBinding
import pt.ipt.dam2025.vetconnect.util.SessionManager
import pt.ipt.dam2025.vetconnect.viewmodel.UsuarioViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.UsuarioViewModelFactory

/**
 * Fragment para a página de login
 */
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: UsuarioViewModel
    private lateinit var sessionManager: SessionManager

    private val pin = StringBuilder()
    private lateinit var pinDots: List<ImageView>
    private var registeredAccounts: MutableMap<String, String> = mutableMapOf()
    private var selectedEmail: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        val factory = UsuarioViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory).get(UsuarioViewModel::class.java)

        pinDots = listOf(binding.pin1, binding.pin2, binding.pin3, binding.pin4, binding.pin5, binding.pin6)

        loadRegisteredAccounts()
        setupListeners()
        observeViewModel()
    }

    private fun loadRegisteredAccounts() {
        val sharedPrefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val accountsSet = sharedPrefs.getStringSet("REGISTERED_ACCOUNTS", setOf()) ?: setOf()
        accountsSet.forEach {
            val parts = it.split(":::")
            if (parts.size == 2) registeredAccounts[parts[1]] = parts[0] // email -> nome
        }

        if (registeredAccounts.isEmpty()) {
            binding.contaSpinner.visibility = View.GONE
            binding.welcomeText.text = "Bem-vindo!"
        } else {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, registeredAccounts.values.toList())
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.contaSpinner.adapter = adapter
        }
    }

    private fun setupListeners() {
        binding.contaSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedEmail = registeredAccounts.keys.toList()[position]
                pin.clear()
                updatePinDots()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) { selectedEmail = null }
        }

        val numberButtonClickListener = View.OnClickListener { view ->
            if (pin.length < 6) {
                pin.append((view as Button).text)
                updatePinDots()
                if (pin.length == 6) attemptLogin()
            }
        }

        listOf(binding.button1, binding.button2, binding.button3, binding.button4, binding.button5, binding.button6, binding.button7, binding.button8, binding.button9, binding.pin0)
            .forEach { it.setOnClickListener(numberButtonClickListener) }

        binding.btnDelete.setOnClickListener {
            if (pin.isNotEmpty()) {
                pin.deleteCharAt(pin.length - 1)
                updatePinDots()
            }
        }

        binding.btnEsqueci.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_recuperarPinFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { loginResponse ->
                Toast.makeText(context, loginResponse.message, Toast.LENGTH_SHORT).show()
                sessionManager.saveAuthToken(loginResponse.token)
                sessionManager.saveUserId(loginResponse.user.id)
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }.onFailure {
                Toast.makeText(context, "PIN incorreto", Toast.LENGTH_SHORT).show()
                pin.clear()
                updatePinDots()
            }
        }
    }

    private fun attemptLogin() {
        if (selectedEmail != null) {
            viewModel.login(selectedEmail!!, pin.toString())
        } else {
            Toast.makeText(context, "Selecione uma conta", Toast.LENGTH_SHORT).show()
            pin.clear()
            updatePinDots()
        }
    }

    private fun updatePinDots() {
        for (i in pinDots.indices) {
            pinDots[i].setImageResource(if (i < pin.length) R.drawable.ic_pin_dot_depois else R.drawable.ic_pin_dot_antes)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
