package pt.ipt.dam2025.trabalho.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam2025.trabalho.api.ApiClient
import pt.ipt.dam2025.trabalho.data.HistoricoDao
import pt.ipt.dam2025.trabalho.model.HistoricoItem

/**
 * Repositório para gerir os dados do histórico, abstraindo a origem dos dados (API ou DB local).
 */
class HistoricoRepository(private val historicoDao: HistoricoDao) {

    // Expõe o Flow da base de dados local. A UI irá observar este Flow.
    val allHistoricoItems: Flow<List<HistoricoItem>> = historicoDao.getAll()

    /**
     * Atualiza os dados locais com os dados da API.
     * Esta função é chamada para sincronizar os dados.
     */
    suspend fun refreshHistorico() {
        try {
            // 1. Ir buscar os dados mais recentes à API
            val itemsFromApi = ApiClient.apiService.getHistorico()
            Log.d("HistoricoRepository", "Dados da API recebidos: $itemsFromApi")

            // 2. Opcional: Inserir ou atualizar os dados na base de dados local
            // Uma estratégia comum é limpar a tabela e inserir os novos dados.
            // Para isso, precisaríamos de uma função deleteAll() no DAO.
            itemsFromApi.forEach { item ->
                historicoDao.insert(item) // O OnConflictStrategy.REPLACE irá atualizar se já existir
            }
            Log.d("HistoricoRepository", "Base de dados local atualizada com sucesso.")

        } catch (e: Exception) {
            Log.e("HistoricoRepository", "Erro ao atualizar o histórico a partir da API", e)
            // Em caso de erro de rede, a app continua a funcionar com os dados locais que já tinha.
        }
    }
}
