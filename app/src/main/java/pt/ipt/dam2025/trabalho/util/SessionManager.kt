package pt.ipt.dam2025.trabalho.util

import android.content.Context
import android.content.SharedPreferences

// Classe de gerenciamento de sessão
class SessionManager(context: Context) {

    // Usar o nome "prefs" que é o consistente na aplicação
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    // As chaves DEVEM ser exatamente iguais às usadas no Login e outras partes
    companion object {
        private const val AUTH_TOKEN = "AUTH_TOKEN"
        private const val USER_ID = "USER_ID"
        private const val ANIMAL_ID = "ANIMAL_ID"
    }

    fun saveAuthToken(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString(AUTH_TOKEN, token)
        editor.apply()
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString(AUTH_TOKEN, null)
    }

    fun saveUserId(userId: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(USER_ID, userId)
        editor.apply()
    }

    fun getUserId(): Int {
        return sharedPreferences.getInt(USER_ID, -1)
    }

    fun saveAnimalId(animalId: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(ANIMAL_ID, animalId)
        editor.apply()
    }

    fun getAnimalId(): Int {
        return sharedPreferences.getInt(ANIMAL_ID, -1)
    }

    fun clearAuth() {
        val editor = sharedPreferences.edit()
        // Limpa todos os dados de sessão
        editor.remove(AUTH_TOKEN)
        editor.remove(USER_ID)
        editor.remove(ANIMAL_ID)
        editor.apply()
    }
}
