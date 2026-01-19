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
     * Insere um novo item na base de dados local.
     */
    suspend fun insert(item: HistoricoItem) {
        historicoDao.insert(item)
    }

    /**
     * Atualiza os dados locais com os dados da API para um animal específico.
     */
    suspend fun refreshHistorico(token: String, animalId: Int) {
        try {
            // 1. Ir buscar os dados mais recentes à API
            val response = ApiClient.apiService.getDocumentosDoAnimal("Bearer $token", animalId)

            if (response.isSuccessful) {
                val documentosResponse = response.body()
                if (documentosResponse != null) {
                    val historicoItems = mutableListOf<HistoricoItem>()

                    // Mapear Receitas para HistoricoItem
                    documentosResponse.receitas.forEach {
                        historicoItems.add(
                            HistoricoItem(
                                data = it.data, // Assumindo que 'data' é a data da prescrição
                                descricao = "Receita: ${it.medicamento}"
                            )
                        )
                    }

                    // Mapear Vacinas para HistoricoItem
                    documentosResponse.vacinas.forEach {
                        historicoItems.add(
                            HistoricoItem(
                                data = it.data, // Assumindo que 'data' é a data da aplicação
                                descricao = "Vacina: ${it.nomeVacina}"
                            )
                        )
                    }

                    // Mapear Exames para HistoricoItem
                    documentosResponse.exames.forEach {
                        historicoItems.add(
                            HistoricoItem(
                                data = it.data, // Assumindo que 'data' é a data do exame
                                descricao = "Exame: ${it.tipoExame}"
                            )
                        )
                    }

                    // 2. Limpar a tabela local e inserir os novos dados
                    historicoDao.deleteAll() // Nota: Esta função precisa existir no seu HistoricoDao
                    historicoItems.forEach { historicoDao.insert(it) }

                    Log.d("HistoricoRepository", "Base de dados local atualizada com sucesso.")
                }
            } else {
                Log.e("HistoricoRepository", "Erro na resposta da API: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("HistoricoRepository", "Erro ao atualizar o histórico a partir da API", e)
            // Em caso de erro de rede, a app continua a funcionar com os dados locais que já tinha.
        }
    }
}