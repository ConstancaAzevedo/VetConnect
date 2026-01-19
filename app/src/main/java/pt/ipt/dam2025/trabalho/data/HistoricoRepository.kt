package pt.ipt.dam2025.trabalho.data

import kotlinx.coroutines.flow.Flow
import pt.ipt.dam2025.trabalho.model.Exame
import pt.ipt.dam2025.trabalho.model.Receita
import pt.ipt.dam2025.trabalho.model.Vacina

class HistoricoRepository(
    private val receitaDao: ReceitaDao,
    private val exameDao: ExameDao,
    private val vacinaDao: VacinaDao
) {

    val todasReceitas: Flow<List<Receita>> = receitaDao.getAllReceitas()
    val todosExames: Flow<List<Exame>> = exameDao.getAllExames()
    val todasVacinas: Flow<List<Vacina>> = vacinaDao.getAllVacinas()

    suspend fun insertReceita(receita: Receita) {
        receitaDao.insert(receita)
    }

    suspend fun insertExame(exame: Exame) {
        exameDao.insert(exame)
    }

    suspend fun insertVacina(vacina: Vacina) {
        vacinaDao.insert(vacina)
    }
}
