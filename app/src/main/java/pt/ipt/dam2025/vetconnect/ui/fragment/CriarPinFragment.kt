package pt.ipt.dam2025.vetconnect.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import pt.ipt.dam2025.vetconnect.R
import pt.ipt.dam2025.vetconnect.api.ApiClient
import pt.ipt.dam2025.vetconnect.api.CreatePinRequest
import pt.ipt.dam2025.vetconnect.databinding.FragmentCriarPinBinding

class CriarPinFragment : Fragment() {

    private var _binding: FragmentCriarPinBinding? = null
    private val binding get() = _binding!!

    private val pin = StringBuilder()
    private lateinit var pinDots: List<ImageView>
    private lateinit var userName: String
    private lateinit var userEmail: String
    private var userId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCriarPinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Lê os dados passados pelos argumentos
        userName = arguments?.getString("USER_NAME") ?: ""
        userEmail = arguments?.getString("USER_EMAIL") ?: ""
        userId = arguments?.getInt("USER_ID", -1) ?: -1

        if (userName.isEmpty() || userEmail.isEmpty() || userId == -1) {
            Snackbar.make(binding.root, "Ocorreu um erro. Tente novamente.", Snackbar.LENGTH_LONG).show()
            findNavController().popBackStack()
            return
        }

        pinDots = listOf(
            binding.pin1, binding.pin2, binding.pin3,
            binding.pin4, binding.pin5, binding.pin6
        )
        setupNumberButtons()

        binding.btnDelete.setOnClickListener {
            if (pin.isNotEmpty()) {
                pin.deleteCharAt(pin.length - 1)
                updatePinDots()
            }
        }
    }

    private fun setupNumberButtons() {
        val numberButtonClickListener = View.OnClickListener { view ->
            if (pin.length < 6) {
                val button = view as Button
                pin.append(button.text)
                updatePinDots()
                if (pin.length == 6) {
                    savePinWithApiAndNavigate()
                }
            }
        }

        val buttons = listOf<Button>(
            binding.button1, binding.button2, binding.button3,
            binding.button4, binding.button5, binding.button6,
            binding.button7, binding.button8, binding.button9,
            binding.button0
        )
        buttons.forEach { it.setOnClickListener(numberButtonClickListener) }
    }

    private fun updatePinDots() {
        for (i in pinDots.indices) {
            pinDots[i].setImageResource(
                if (i < pin.length) R.drawable.ic_pin_dot_depois
                else R.drawable.ic_pin_dot_antes
            )
        }
    }

    private fun savePinWithApiAndNavigate() {
        lifecycleScope.launch {
            try {
                val request = CreatePinRequest(email = userEmail, pin = pin.toString())
                val response = ApiClient.apiService.criarPin(request)

                if (response.isSuccessful) {
                    val sharedPrefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    val registeredAccounts = sharedPrefs.getStringSet("REGISTERED_ACCOUNTS", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
                    val accountString = "$userName:::$userEmail"
                    registeredAccounts.add(accountString)

                    with(sharedPrefs.edit()) {
                        putStringSet("REGISTERED_ACCOUNTS", registeredAccounts)
                        apply()
                    }

                    val successMessage = "PIN criado com sucesso"
                    Snackbar.make(binding.root, successMessage, Snackbar.LENGTH_SHORT).addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            // Navega para a Home, limpando o back stack
                            findNavController().navigate(R.id.action_criarPinFragment_to_homeFragment)
                        }
                    }).show()

                } else {
                    val errorMessage = "Ocorreu um erro, o PIN não foi guardado"
                    Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
                    pin.clear()
                    updatePinDots()
                }

            } catch (e: Exception) {
                Log.e("CriarPinFragment", "Erro ao criar o PIN", e)
                val errorMessage = "Falha na ligação. Verifique a sua internet e tente novamente"
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
                pin.clear()
                updatePinDots()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
