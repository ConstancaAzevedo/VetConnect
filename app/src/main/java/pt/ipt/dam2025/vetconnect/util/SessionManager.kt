package pt.ipt.dam2025.vetconnect.util

import android.content.Context // Importa o contexto da aplicação
import android.content.SharedPreferences // Importa a classe para armazenamento de preferências
import androidx.core.content.edit // Importa a função de extensão para editar SharedPreferences

/**
 * Gestor de sessão para guardar e obter dados da sessão do utilizador
 * Usa SharedPreferences para persistir os dados no dispositivo
 */
class SessionManager(context: Context) { // Declara a classe que requer um Contexto para ser instanciada

    // Inicializa o SharedPreferences com um nome privado para a aplicação
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    /**
     * Companion object para guardar as chaves usadas no SharedPreferences
     * Garante que as chaves são as mesmas em toda a aplicação
     */
    companion object {
        // Chave para o token de autenticação
        private const val AUTH_TOKEN = "AUTH_TOKEN"
        // Chave para o ID do utilizador
        private const val USER_ID = "USER_ID"
        // Chave para o ID do animal selecionado
        private const val ANIMAL_ID = "ANIMAL_ID"
    }

    // Guarda o token de autenticação no SharedPreferences
    fun saveAuthToken(token: String) {
        sharedPreferences.edit {
            // Adiciona ou atualiza o valor do token
            putString(AUTH_TOKEN, token)
        }
    }

    // Obtém o token de autenticação do SharedPreferences
    fun getAuthToken(): String? {
        // Lê a string do token se existir senão retorna null
        return sharedPreferences.getString(AUTH_TOKEN, null)
    }

    // Guarda o ID do utilizador no SharedPreferences
    fun saveUserId(userId: Int) {
        sharedPreferences.edit {
            // Adiciona ou atualiza o valor do ID do utilizador
            putInt(USER_ID, userId)
        }
    }

    // Obtém o ID do utilizador do SharedPreferences
    fun getUserId(): Int {
        // Lê o inteiro do ID se existir senão retorna −1
        // −1 é um valor padrão para indicar que o ID não foi encontrado
        return sharedPreferences.getInt(USER_ID, -1)
    }

    // Guarda o ID do animal selecionado no SharedPreferences
    fun saveAnimalId(animalId: Int) {
        sharedPreferences.edit {
            // Adiciona ou atualiza o valor do ID do animal
            putInt(ANIMAL_ID, animalId)
        }
    }

    // Obtém o ID do animal selecionado do SharedPreferences
    fun getAnimalId(): Int {
        // Lê o inteiro do ID se existir senão retorna -1
        return sharedPreferences.getInt(ANIMAL_ID, -1)
    }

    // Limpa todos os dados da sessão guardados
    fun clearAuth() {
        sharedPreferences.edit {
            // Remove o token de autenticação
            remove(AUTH_TOKEN)
            // Remove o ID do utilizador
            remove(USER_ID)
            // Remove o ID do animal
            remove(ANIMAL_ID)
        }
    }
}
