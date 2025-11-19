package pt.ipt.dam2025.trabalho

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoricoActivity : AppCompatActivity() {

    // usa o viewModels para obter uma instância do ViewModel.
    private val historicoViewModel: HistoricoViewModel by viewModels {
        HistoricoViewModelFactory((application as VetConnectApplication).database.historicoDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historico)

        val recyclerView = findViewById<RecyclerView>(R.id.rvHistorico)
        val adapter = HistoricoAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // observa o Flow de dados do ViewModel.
        // sempre que os dados mudarem, o adapter.submitList é chamado.
        historicoViewModel.allHistoricoItems.asLiveData().observe(this) {
            items -> items?.let { adapter.submitList(it) }
        }

        // configura o botão flutuante para adicionar um novo item.
        val fab = findViewById<FloatingActionButton>(R.id.fab_add_historico)
        fab.setOnClickListener {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val currentDate = sdf.format(Date())
            historicoViewModel.insert(HistoricoItem(data = currentDate, descricao = "Novo evento adicionado"))
        }
    }
}

// ViewModel para a HistoricoActivity
// ele sobrevive a mudanças de configuração e gere a lógica de negócio
class HistoricoViewModel(private val dao: HistoricoDao) : ViewModel() {

    val allHistoricoItems: kotlinx.coroutines.flow.Flow<List<HistoricoItem>> = dao.getAll()

    fun insert(item: HistoricoItem) = viewModelScope.launch {
        dao.insert(item)
    }
}

// Factory para criar a instância do HistoricoViewModel com os parâmetros necessários.
class HistoricoViewModelFactory(private val dao: HistoricoDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoricoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoricoViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
