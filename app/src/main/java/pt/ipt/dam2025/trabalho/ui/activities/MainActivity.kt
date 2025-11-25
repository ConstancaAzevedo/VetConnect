package pt.ipt.dam2025.trabalho.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.api.ApiClient
import pt.ipt.dam2025.trabalho.data.AppDatabase
import pt.ipt.dam2025.trabalho.model.NovoUsuario

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val autentButton = findViewById<Button>(R.id.autent_button)
        val aboutButton = findViewById<Button>(R.id.about_button)
        val testButton = findViewById<Button>(R.id.test_button) // Botão de teste

        autentButton.setOnClickListener { /*...*/ }
        aboutButton.setOnClickListener { /*...*/ }
        testButton.setOnClickListener { /*...*/ }

        // Chama a sequência completa de testes da API
        testApiCompleto()
    }

    private fun testApiCompleto() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // 1. POST - Criar um novo utilizador
                Log.d("API_TEST", "1. A criar novo utilizador...")
                val novoUsuario = NovoUsuario(nome = "Utilizador Teste", email = "teste@email.com", password = "123", tipo = "tutor")
                val utilizadorCriado = ApiClient.apiService.criarUsuario(novoUsuario)
                Log.d("API_TEST", "   -> Sucesso! Utilizador criado: $utilizadorCriado")
                val userId = utilizadorCriado.id

                // 2. GET (Todos) - Listar utilizadores
                Log.d("API_TEST", "2. A listar todos os utilizadores...")
                val todosUtilizadores = ApiClient.apiService.getUsuarios()
                Log.d("API_TEST", "   -> Sucesso! Lista: $todosUtilizadores")

                // 3. GET (por ID) - Obter o utilizador específico
                Log.d("API_TEST", "3. A obter o utilizador com ID: $userId...")
                val utilizadorEspecifico = ApiClient.apiService.getUsuario(userId)
                Log.d("API_TEST", "   -> Sucesso! Utilizador encontrado: $utilizadorEspecifico")

                // 4. PUT - Atualizar o utilizador
                Log.d("API_TEST", "4. A atualizar o utilizador com ID: $userId...")
                val dadosUpdate = NovoUsuario(nome = "Utilizador Teste Alterado", email = "teste@email.com", password = "123", tipo = "tutor")
                val utilizadorAtualizado = ApiClient.apiService.atualizarUsuario(userId, dadosUpdate)
                Log.d("API_TEST", "   -> Sucesso! Utilizador atualizado: $utilizadorAtualizado")

                // 5. DELETE - Apagar o utilizador
                Log.d("API_TEST", "5. A apagar o utilizador com ID: $userId...")
                ApiClient.apiService.deletarUsuario(userId)
                Log.d("API_TEST", "   -> Sucesso! Utilizador apagado.")

                // 6. GET (Final) - Confirmar que a lista está vazia
                Log.d("API_TEST", "6. A verificar se a lista está vazia...")
                val listaFinal = ApiClient.apiService.getUsuarios()
                Log.d("API_TEST", "   -> Sucesso! Lista final: $listaFinal")

                Log.d("API_TEST", "\n✅ TESTE COMPLETO DA API CONCLUÍDO COM SUCESSO! ✅")

            } catch (e: Exception) {
                Log.e("API_TEST", "❌ Ocorreu um erro durante o teste da API!", e)
            }
        }
    }
}
