package pt.ipt.dam2025.vetconnect.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import pt.ipt.dam2025.vetconnect.R
import pt.ipt.dam2025.vetconnect.api.ApiClient
import pt.ipt.dam2025.vetconnect.databinding.FragmentVerificacaoBinding
import pt.ipt.dam2025.vetconnect.model.VerificationRequest
import java.io.IOException

class VerificacaoFragment : Fragment() {

    private var _binding: FragmentVerificacaoBinding? = null
    private val binding get() = _binding!!

    private lateinit var userEmail: String
    private lateinit var userName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerificacaoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userName = arguments?.getString("USER_NAME") ?: ""
        userEmail = arguments?.getString("USER_EMAIL") ?: ""
        val verificationCode = arguments?.getString("VERIFICATION_CODE")

        if (!verificationCode.isNullOrEmpty()) {
            val snackbar = Snackbar.make(binding.root, "O seu código de verificação é: $verificationCode", Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction("OK") { snackbar.dismiss() }
            snackbar.show()
        }

        if (userName.isEmpty() || userEmail.isEmpty()) {
            Snackbar.make(binding.root, "Ocorreu um erro. Tente registar-se novamente.", Snackbar.LENGTH_LONG).show()
            findNavController().popBackStack()
            return
        }

        binding.btnVerificar.setOnClickListener {
            val enteredCode = binding.verificationCodeInput.text.toString().trim()
            if (enteredCode.isBlank() || enteredCode.length != 6) {
                binding.verificationCodeInput.error = "Insira um código de 6 dígitos"
                return@setOnClickListener
            }
            verificarCodigoComAPI(enteredCode)
        }
    }

    private fun verificarCodigoComAPI(codigo: String) {
        lifecycleScope.launch {
            try {
                val request = VerificationRequest(email = userEmail, codigo = codigo)
                val response = ApiClient.apiService.verificarCodigo(request)

                if (response.isSuccessful) {
                    val userId = response.body()?.userId
                    val successMessage = response.body()?.message ?: "Verificado com sucesso!"

                    Snackbar.make(binding.root, successMessage, Snackbar.LENGTH_SHORT).addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            val bundle = bundleOf(
                                "USER_NAME" to userName,
                                "USER_EMAIL" to userEmail,
                                "USER_ID" to userId
                            )
                            findNavController().navigate(R.id.action_verificacaoFragment_to_criarPinFragment, bundle)
                        }
                    }).show()
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Código de verificação inválido. Tente novamente."
                        404 -> "Utilizador não encontrado."
                        else -> "Ocorreu um erro. Tente novamente."
                    }
                    Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
                    binding.verificationCodeInput.text.clear()
                }
            } catch (e: IOException) {
                Log.e("VerificacaoFragment", "Erro de rede na verificação", e)
                Snackbar.make(binding.root, "Falha na ligação. Verifique a sua internet.", Snackbar.LENGTH_LONG).show()
            } catch (e: Exception) {
                Log.e("VerificacaoFragment", "Erro inesperado na verificação", e)
                Snackbar.make(binding.root, "Ocorreu um erro inesperado.", Snackbar.LENGTH_LONG).show()
                binding.verificationCodeInput.text.clear()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
