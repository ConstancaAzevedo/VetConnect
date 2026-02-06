package pt.ipt.dam2025.vetconnect.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import pt.ipt.dam2025.vetconnect.databinding.FragmentPerfilBinding
import pt.ipt.dam2025.vetconnect.model.UpdateUserRequest
import pt.ipt.dam2025.vetconnect.model.Usuario
import pt.ipt.dam2025.vetconnect.viewmodel.UsuarioViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.UsuarioViewModelFactory

/**
 * Fragment para a página de perfil do usuário
 */

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: UsuarioViewModel
    private var isEditMode = false
    private var currentUser: Usuario? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = UsuarioViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory).get(UsuarioViewModel::class.java)

        setupUI()
        observeViewModel()

        // TODO: Obter o token e o userId de forma segura
        val token = "seu_token_aqui"
        val userId = 1
        viewModel.refreshUser(token, userId)
    }

    private fun setupUI() {
        binding.btnEditarGuardar.setOnClickListener { toggleEditMode() }
        setFieldsEditable(false)
    }

    private fun observeViewModel() {
        // TODO: obter userId de forma segura
        val userId = 1
        viewModel.getUser(userId).observe(viewLifecycleOwner) {
            it?.let {
                currentUser = it
                populateUI(it)
            }
        }

        viewModel.updateResult.observe(viewLifecycleOwner) {
            it.onSuccess {
                Toast.makeText(context, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                setFieldsEditable(false)
                binding.btnEditarGuardar.text = "Editar"
                isEditMode = false
            }.onFailure {
                Toast.makeText(context, "Erro ao atualizar o perfil: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun populateUI(user: Usuario) {
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
            binding.btnEditarGuardar.text = "Guardar"
        } else {
            saveProfileChanges()
        }
    }

    private fun setFieldsEditable(isEditable: Boolean) {
        val fields = listOf<EditText>(binding.nomeDado, binding.emailDado, binding.telemovelDado, binding.nacionalidadeDado, binding.sexoDado, binding.ccDado, binding.dataDado, binding.moradaDado)
        fields.forEach { it.isEnabled = isEditable }
    }

    private fun saveProfileChanges() {
        // TODO: Obter o token e o userId de forma segura
        val token = "seu_token_aqui"
        val userId = 1

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
