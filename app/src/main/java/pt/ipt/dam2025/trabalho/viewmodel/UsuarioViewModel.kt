package pt.ipt.dam2025.trabalho.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.data.AppDatabase
import pt.ipt.dam2025.trabalho.model.NovoUsuario
import pt.ipt.dam2025.trabalho.model.User
import pt.ipt.dam2025.trabalho.model.Usuario
import pt.ipt.dam2025.trabalho.repository.UsuarioRepository

class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UsuarioRepository

    // LiveData para o utilizador individual (Perfil)
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    // LiveData para a lista de utilizadores (Ecrã de Admin/Lista)
    private val _usuarios = MutableLiveData<List<Usuario>>()
    val usuarios: LiveData<List<Usuario>> = _usuarios

    // LiveData para feedback à UI (Carregamento, Mensagens, Erros)
    private val _carregando = MutableLiveData<Boolean>()
    val carregando: LiveData<Boolean> = _carregando

    private val _mensagem = MutableLiveData<String>()
    val mensagem: LiveData<String> = _mensagem

    private val _erro = MutableLiveData<String>()
    val erro: LiveData<String> = _erro

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = UsuarioRepository(userDao)
        loadCurrentUser()
    }

    /**
     * Obtém o token de autenticação das SharedPreferences.
     */
    private fun getToken(): String? {
        val sharedPrefs = getApplication<Application>().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("AUTH_TOKEN", null)
    }

    /**
     * Carrega o utilizador logado, observando a base de dados local.
     */
    private fun loadCurrentUser() {
        viewModelScope.launch {
            val sharedPrefs = getApplication<Application>().getSharedPreferences("prefs", Context.MODE_PRIVATE)
            val userId = sharedPrefs.getInt("USER_ID", -1)
            if (userId != -1) {
                // Começa a observar a base de dados para atualizações locais
                repository.getUser(userId).collect { userFromDb ->
                    _user.postValue(userFromDb)
                }
            } else {
                _user.postValue(null) // Limpa se não houver utilizador logado
            }
        }
    }

    /**
     * Obtém a lista completa de utilizadores da API (requer autenticação).
     */
    fun carregarUsuarios() {
        val token = getToken()
        if (token == null) {
            _erro.postValue("Sessão inválida. Por favor, faça login novamente.")
            return
        }

        _carregando.value = true
        viewModelScope.launch {
            try {
                val listaUsuarios = repository.getUsuarios(token)
                _usuarios.postValue(listaUsuarios)
            } catch (e: Exception) {
                _erro.postValue("Falha ao carregar utilizadores: ${e.message}")
            } finally {
                _carregando.postValue(false)
            }
        }
    }

    /**
     * Cria um novo utilizador.
     */
    fun adicionarUsuario(nome: String, email: String, telemovel: String, tipo: String) {
        _carregando.value = true
        viewModelScope.launch {
            try {
                val novoUsuario = NovoUsuario(nome, email, telemovel, tipo)
                val result = repository.criarUsuario(novoUsuario)
                result.onSuccess {
                    _mensagem.postValue("Utilizador \"${it.nome}\" criado com sucesso.")
                    carregarUsuarios() // Atualiza a lista automaticamente
                }
                result.onFailure {
                    _erro.postValue("Erro ao criar utilizador: ${it.message}")
                }
            } catch (e: Exception) {
                _erro.postValue("Erro inesperado: ${e.message}")
            } finally {
                _carregando.postValue(false)
            }
        }
    }

    /**
     * Apaga um utilizador (requer autenticação).
     */
    fun apagarUsuario(usuario: Usuario) {
        val token = getToken()
        if (token == null) {
            _erro.postValue("Sessão inválida.")
            return
        }

        viewModelScope.launch {
            _carregando.postValue(true)
            try {
                repository.deletarUsuario(token, usuario.id)
                _mensagem.postValue("Utilizador apagado com sucesso")
                carregarUsuarios() // Atualiza a lista
            } catch (e: Exception) {
                _erro.postValue("Falha ao apagar utilizador: ${e.message}")
            } finally {
                _carregando.postValue(false)
            }
        }
    }

    /**
     * Atualiza o perfil do utilizador (requer autenticação).
     */
    fun updateUser(user: User) {
        val token = getToken()
        if (token == null) {
            _erro.postValue("Sessão inválida.")
            return
        }
        viewModelScope.launch {
            try {
                repository.updateUser(token, user)
                _mensagem.postValue("Perfil atualizado com sucesso!")
            } catch (e: Exception) {
                _erro.postValue("Falha ao atualizar perfil: ${e.message}")
            }
        }
    }

    /**
     * Sincroniza os dados do utilizador logado com o servidor (requer autenticação).
     */
    fun refreshUser() {
        val token = getToken()
        val userId = _user.value?.id
        if (token == null || userId == null) {
            return // Não faz nada se não houver sessão
        }
        viewModelScope.launch {
            repository.refreshUser(token, userId)
        }
    }
}