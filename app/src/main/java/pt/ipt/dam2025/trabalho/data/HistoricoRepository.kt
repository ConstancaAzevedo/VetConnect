package pt.ipt.dam2025.trabalho.data

import kotlinx.coroutines.flow.Flow
import pt.ipt.dam2025.trabalho.model.Exame
import pt.ipt.dam2025.trabalho.model.Receita
import pt.ipt.dam2025.trabalho.model.Vacina

// Repositório para as funcionalidades de histórico
class HistoricoRepository(
    // Injeção de dependência dos DAOs
    private val receitaDao: ReceitaDao,
    private val exameDao: ExameDao,
    private val vacinaDao: VacinaDao
) {

    // Funções para aceder os dados na base de dados
    val todasReceitas: Flow<List<Receita>> = receitaDao.getAllReceitas()
    val todosExames: Flow<List<Exame>> = exameDao.getAllExames()
    val todasVacinas: Flow<List<Vacina>> = vacinaDao.getAllVacinas()

    // Funções para inserir dados na base de dados
    suspend fun insertReceitas(receitas: List<Receita>) {
        receitaDao.insertAll(receitas)
    }

    // Funções para inserir dados no base de dados
    suspend fun insertExames(exames: List<Exame>) {
        exameDao.insertAll(exames)
    }

    suspend fun insertVacinas(vacinas: List<Vacina>) {
        vacinaDao.insertAll(vacinas)
    }
}
