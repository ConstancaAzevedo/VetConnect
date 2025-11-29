package pt.ipt.dam2025.trabalho.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.data.AppDatabase
import pt.ipt.dam2025.trabalho.model.NovoUsuario
import pt.ipt.dam2025.trabalho.model.User
import pt.ipt.dam2025.trabalho.model.Usuario
import pt.ipt.dam2025.trabalho.repository.UsuarioRepository

class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UsuarioRepository
    private val userDao = AppDatabase.getDatabase(application).userDao()

    // LiveData para o utilizador individual (PerfilTutorActivity)
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    // Para a lista de utilizadores (UserListActivity)
    private val _usuarios = MutableLiveData<List<Usuario>>()
    val usuarios: LiveData<List<Usuario>> = _usuarios

    // Para feedback à UI
    private val _carregando = MutableLiveData<Boolean>()
    val carregando: LiveData<Boolean> = _carregando

    private val _mensagem = MutableLiveData<String>()
    val mensagem: LiveData<String> = _mensagem

    private val _erro = MutableLiveData<String>()
    val erro: LiveData<String> = _erro

    init {
        repository = UsuarioRepository(userDao)
        loadCurrentUser() // Carrega o utilizador ao iniciar
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            val sharedPrefs = getApplication<Application>().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val userId = sharedPrefs.getInt("LOGGED_IN_USER_ID", -1)
            if (userId != -1) {
                repository.getUser(userId).filterNotNull().asLiveData().observeForever {
                    _user.postValue(it)
                }
            } else {
                _user.postValue(null) // Limpa o utilizador se não houver ID
            }
        }
    }

    fun carregarUsuarios() {
        _carregando.value = true
        viewModelScope.launch {
            try {
                val listaUsuarios = repository.getUsuarios()
                _usuarios.postValue(listaUsuarios)
            } catch (e: Exception) {
                _erro.postValue("Falha ao carregar usuários: ${e.message}")
            } finally {
                _carregando.postValue(false)
            }
        }
    }

    fun adicionarUsuario(nome: String, email: String, telemovel: String?, tipo: String) {
        _carregando.value = true
        viewModelScope.launch {
            try {
                val novoUsuario = NovoUsuario(nome = nome, email = email, telemovel = telemovel, tipo = tipo)
                val result = repository.criarUsuario(novoUsuario)
                result.onSuccess {
                    _mensagem.postValue("Usuário \"${it.nome}\" criado com sucesso.")
                    carregarUsuarios() // Recarrega a lista
                }
                result.onFailure {
                    _erro.postValue("Erro ao criar usuário: ${it.message}")
                }
            } catch (e: Exception) {
                _erro.postValue("Erro inesperado: ${e.message}")
            } finally {
                _carregando.postValue(false)
            }
        }
    }

    fun updateUser(user: User) = viewModelScope.launch {
        try {
            repository.updateUser(user)
            _mensagem.postValue("Perfil atualizado com sucesso!")
        } catch (e: Exception) {
            _erro.postValue("Falha ao atualizar perfil: ${e.message}")
        }
    }

    fun refreshUser() = viewModelScope.launch {
        user.value?.id?.let {
            repository.refreshUser(it)
        }
    }
}
