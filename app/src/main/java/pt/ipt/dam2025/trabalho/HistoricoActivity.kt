package pt.ipt.dam2025.trabalho

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


// tela do histórico
class HistoricoActivity : AppCompatActivity() {

    // obtém o ViewModel
    // o 'by viewModels' garante que o ViewModel sobrevive a mudanças de configuração
    private val historicoViewModel: HistoricoViewModel by viewModels {
        HistoricoViewModelFactory((application as VetConnectApplication).database.historicoDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historico)

        // configura a RecyclerView (a lista) e o seu Adapter
        val recyclerView = findViewById<RecyclerView>(R.id.rvHistorico)
        val adapter = HistoricoAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // observa os dados do ViewModel. Esta é uma ligação reativa:
        // sempre que os dados na base de dados mudarem, a UI é atualizada automaticamente.
        historicoViewModel.allHistoricoItems.asLiveData().observe(this) { items ->
            items?.let { adapter.submitList(it) }
        }

        // botão de adicionar que abre a janela de diálogo
        val fab = findViewById<FloatingActionButton>(R.id.fab_add_historico)
        fab.setOnClickListener {
            showAddHistoricoDialog()
        }

        // ação de apagar (arrastar)
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            // não precisamos de implementar a ação de mover (reordenar)
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            // quando um item é arrastado para um dos lados
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition // obtém a posição do item
                // verificação de segurança para evitar crashes
                if (position != RecyclerView.NO_POSITION) {

                    val historicoItem = adapter.currentList[position] // obtém o item a ser apagado
                    historicoViewModel.delete(historicoItem) // manda o ViewModel apagar o item

                    // mostra uma Snackbar com a opção de anular
                    Snackbar.make(recyclerView, "Item apagado", Snackbar.LENGTH_LONG)
                        .setAction("ANULAR") {
                            historicoViewModel.insert(historicoItem) // se o utilizador clicar insere o item de volta
                        }
                        .show()
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback) // cria o "ajudante" com as nossas regras
        itemTouchHelper.attachToRecyclerView(recyclerView) // liga o "ajudante" à lista para ativar o gesto
    }

    private fun showAddHistoricoDialog() {
        val builder = AlertDialog.Builder(this) // inicia a construção da janela
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_add_historico, null) // carrega o nosso layout XML

        val etData = dialogView.findViewById<EditText>(R.id.etData)
        val etDescricao = dialogView.findViewById<EditText>(R.id.etDescricao)

        builder.setView(dialogView) // define o nosso layout como o conteúdo da janela
            .setPositiveButton("Guardar") { _, _ -> // cria o botão "Guardar"
                val data = etData.text.toString()
                val descricao = etDescricao.text.toString()

                // se os campos de texto não estiverem vazios, guarda os dados
                if (data.isNotEmpty() && descricao.isNotEmpty()) {
                    historicoViewModel.insert(HistoricoItem(data = data, descricao = descricao))
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ -> // cria o botão "Cancelar"
                dialog.cancel() // fecha a janela
            }

        builder.create().show() // constrói e mostra a janela de diálogo
    }
}


// ViewModel: sobrevive a mudanças de configuração e gere a lógica de negócio.
class HistoricoViewModel(private val dao: HistoricoDao) : ViewModel() {

    // Expõe a lista de itens para a Activity poder observar.
    val allHistoricoItems: kotlinx.coroutines.flow.Flow<List<HistoricoItem>> = dao.getAll()

    // as operações na base de dados devem ser feitas numa thread de segundo plano para não congelar a UI.
    // o viewModelScope faz isto automaticamente.
    fun insert(item: HistoricoItem) = viewModelScope.launch {
        dao.insert(item)
    }

    fun delete(item: HistoricoItem) = viewModelScope.launch {
        dao.delete(item)
    }
}


// Factory: "ensina" o Android a criar o nosso ViewModel, passando o DAO como parâmetro.
class HistoricoViewModelFactory(private val dao: HistoricoDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoricoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoricoViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
