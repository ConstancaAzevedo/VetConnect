package pt.ipt.dam2025.vetconnect.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import pt.ipt.dam2025.vetconnect.R
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import pt.ipt.dam2025.vetconnect.databinding.FragmentPerfilBinding
import pt.ipt.dam2025.vetconnect.model.UpdateUserRequest
import pt.ipt.dam2025.vetconnect.model.Utilizador
import pt.ipt.dam2025.vetconnect.util.SessionManager
import pt.ipt.dam2025.vetconnect.viewmodel.UtilizadorViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.UtilizadorViewModelFactory

/**
 * Fragment para a página de perfil do utilizador
 */

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: UtilizadorViewModel
    private lateinit var sessionManager: SessionManager
    private var isEditMode = false
    private var currentUser: Utilizador? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        val factory = UtilizadorViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[UtilizadorViewModel::class.java]

        setupUI()
        observeViewModel()

        val token = sessionManager.getAuthToken()
        val userId = sessionManager.getUserId()

        if (token != null && userId != -1) {
            viewModel.refreshUser(token, userId)
        } else {
            Toast.makeText(context, "Sessão inválida. Por favor reinicie a aplicação", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupUI() {
        binding.btnEditarGuardar.setOnClickListener { toggleEditMode() }
        setFieldsEditable(false)
    }

    private fun observeViewModel() {
        val userId = sessionManager.getUserId()
        if (userId != -1) {
            viewModel.getUser(userId).observe(viewLifecycleOwner) { user ->
                user?.let {
                    currentUser = it
                    populateUI(it)
                }
            }
        }

        viewModel.updateResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(context, "Perfil atualizado com sucesso", Toast.LENGTH_SHORT).show()
                setFieldsEditable(false)
                binding.btnEditarGuardar.setText(R.string.guardar)
                isEditMode = false
            }.onFailure { throwable ->
                Toast.makeText(context, "Erro ao atualizar o perfil: ${throwable.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun populateUI(user: Utilizador) {
        binding.nomeDado.setText(user.nome)
        binding.emailDado.setText(user.email)
        binding.telemovelDado.setText(user.telemovel)
        binding.nacionalidadeDado.setText(user.nacionalidade)
        binding.sexoDado.setText(user.sexo)
        binding.ccDado.setText(user.cc)
        binding.dataDado.setText(user.dataNascimento)
        binding.moradaDado.setText(user.morada)
    }

    private fun toggleEditMode() {
        isEditMode = !isEditMode
        setFieldsEditable(isEditMode)
        if (isEditMode) {
            binding.btnEditarGuardar.setText(R.string.editar)
        } else {
            saveProfileChanges()
        }
    }

    private fun setFieldsEditable(isEditable: Boolean) {
        val fields = listOf(binding.nomeDado, binding.emailDado, binding.telemovelDado, binding.nacionalidadeDado, binding.sexoDado, binding.ccDado, binding.dataDado, binding.moradaDado)
        fields.forEach { it.isEnabled = isEditable }
    }

    private fun saveProfileChanges() {
        val token = sessionManager.getAuthToken()
        val userId = sessionManager.getUserId()

        if (token == null || userId == -1) {
            Toast.makeText(context, "Sessão inválida. Não é possível guardar alterações", Toast.LENGTH_LONG).show()
            return
        }

        val request = UpdateUserRequest(
            nome = binding.nomeDado.text.toString(),
            email = binding.emailDado.text.toString(),
            tipo = currentUser?.tipo ?: "tutor"
        )

        viewModel.updateUser(token, userId, request)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
