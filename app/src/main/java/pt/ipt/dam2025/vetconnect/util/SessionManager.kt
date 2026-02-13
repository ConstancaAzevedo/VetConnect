package pt.ipt.dam2025.vetconnect.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Gestor de sessão para guardar e obter o token de autenticação
 * Usa SharedPreferences para persistir o token no dispositivo
 */
class SessionManager(context: Context) {

    // usa o nome "prefs" que é o consistente na aplicação
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    // as chaves DEVEM ser exatamente iguais às usadas no login e outras partes
    companion object {
        private const val AUTH_TOKEN = "AUTH_TOKEN"
        private const val USER_ID = "USER_ID"
        private const val ANIMAL_ID = "ANIMAL_ID"
    }

    // guarda e obtém o token de autenticação
    fun saveAuthToken(token: String) {
        sharedPreferences.edit {
            putString(AUTH_TOKEN, token)
        }
    }

    // obtém o token de autenticação
    fun getAuthToken(): String? {
        return sharedPreferences.getString(AUTH_TOKEN, null)
    }

    // guarda e obtém o ID do utilizador
    fun saveUserId(userId: Int) {
        sharedPreferences.edit {
            putInt(USER_ID, userId)
        }
    }

    // obtém o ID do utilizador
    // −1 -> valor padrão para ID não existe ou é inválido (porque IDs nunca são negativos)
    fun getUserId(): Int {
        return sharedPreferences.getInt(USER_ID, -1)
    }

    // guarda e obtém o ID do animal
    fun saveAnimalId(animalId: Int) {
        sharedPreferences.edit {
            putInt(ANIMAL_ID, animalId)
        }
    }

    // obtém o ID do animal
    fun getAnimalId(): Int {
        return sharedPreferences.getInt(ANIMAL_ID, -1)
    }

    // limpa todos os dados de sessão
    fun clearAuth() {
        sharedPreferences.edit {
            // Limpa todos os dados de sessão
            remove(AUTH_TOKEN)
            remove(USER_ID)
            remove(ANIMAL_ID)
        }
    }
}