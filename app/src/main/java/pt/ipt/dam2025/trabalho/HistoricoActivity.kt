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
import kotlinx.coroutines.launch

class HistoricoActivity : AppCompatActivity() {

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

        // Observa os dados e submete-os ao adapter
        historicoViewModel.allHistoricoItems.asLiveData().observe(this) { items ->
            items?.let { adapter.submitList(it) }
        }

        // Configura o botão para adicionar novos itens
        val fab = findViewById<FloatingActionButton>(R.id.fab_add_historico)
        fab.setOnClickListener {
            showAddHistoricoDialog()
        }

        // Configura o gesto de arrastar para apagar
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false // Não queremos tratar do gesto de mover/reordenar
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val historicoItem = adapter.currentList[position]
                historicoViewModel.delete(historicoItem)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun showAddHistoricoDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_add_historico, null)

        val etData = dialogView.findViewById<EditText>(R.id.etData)
        val etDescricao = dialogView.findViewById<EditText>(R.id.etDescricao)

        builder.setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val data = etData.text.toString()
                val descricao = etDescricao.text.toString()

                if (data.isNotEmpty() && descricao.isNotEmpty()) {
                    historicoViewModel.insert(HistoricoItem(data = data, descricao = descricao))
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.cancel()
            }

        builder.create().show()
    }
}

class HistoricoViewModel(private val dao: HistoricoDao) : ViewModel() {

    val allHistoricoItems: kotlinx.coroutines.flow.Flow<List<HistoricoItem>> = dao.getAll()

    fun insert(item: HistoricoItem) = viewModelScope.launch {
        dao.insert(item)
    }

    fun delete(item: HistoricoItem) = viewModelScope.launch {
        dao.delete(item)
    }
}

class HistoricoViewModelFactory(private val dao: HistoricoDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoricoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoricoViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
