package pt.ipt.dam2025.trabalho.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.model.NovoUsuario
import pt.ipt.dam2025.trabalho.model.Usuario
import pt.ipt.dam2025.trabalho.repository.UsuarioRepository

class UsuarioViewModel : ViewModel() {

    private val repository = UsuarioRepository()

    // LiveData para a lista de usuários
    private val _usuarios = MutableLiveData<List<Usuario>>()
    val usuarios: LiveData<List<Usuario>> = _usuarios

    // LiveData para o estado de carregamento
    private val _carregando = MutableLiveData<Boolean>()
    val carregando: LiveData<Boolean> = _carregando

    // LiveData para mensagens de erro
    private val _erro = MutableLiveData<String>()
    val erro: LiveData<String> = _erro

    // LiveData para mensagens de sucesso
    private val _mensagem = MutableLiveData<String>()
    val mensagem: LiveData<String> = _mensagem

    /**
     * Obtém os usuários da API.
     */
    fun carregarUsuarios() {
        viewModelScope.launch {
            _carregando.value = true
            try {
                val usuariosList = repository.getUsuarios()
                _usuarios.value = usuariosList
                _mensagem.value = "Usuários carregados com sucesso!"
            } catch (e: Exception) {
                _erro.value = "Erro ao carregar usuários: ${e.message}"
            } finally {
                _carregando.value = false
            }
        }
    }

    /**
     * Cria um novo usuário e atualiza a lista.
     */
    fun adicionarUsuario(nome: String, email: String, telefone: String? = null) {
        viewModelScope.launch {
            _carregando.value = true
            try {
                val novoUsuario = NovoUsuario(nome, email, telefone)
                val result = repository.criarUsuario(novoUsuario)

                result.onSuccess { 
                    _mensagem.value = "Usuário criado com sucesso!"
                    carregarUsuarios() // Recarrega a lista para mostrar o novo usuário
                }.onFailure { exception ->
                    _erro.value = "Erro ao criar usuário: ${exception.message}"
                }

            } catch (e: Exception) {
                _erro.value = "Erro: ${e.message}"
            } finally {
                _carregando.value = false
            }
        }
    }
}
