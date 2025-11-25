package pt.ipt.dam2025.trabalho.ui.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.VetConnectApplication
import pt.ipt.dam2025.trabalho.model.HistoricoItem
import pt.ipt.dam2025.trabalho.ui.adapters.HistoricoAdapter
import pt.ipt.dam2025.trabalho.viewmodel.HistoricoViewModel
import pt.ipt.dam2025.trabalho.viewmodel.HistoricoViewModelFactory
import java.util.Calendar

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

        historicoViewModel.allHistoricoItems.asLiveData().observe(this) { items ->
            items?.let { adapter.submitList(it) }
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab_add_historico)
        fab.setOnClickListener {
            showAddHistoricoDialog()
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val historicoItem = adapter.currentList[position]
                    historicoViewModel.delete(historicoItem)

                    Snackbar.make(recyclerView, "Item apagado", Snackbar.LENGTH_LONG)
                        .setAction("ANULAR") {
                            historicoViewModel.insert(historicoItem)
                        }
                        .show()
                }
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

        etData.isFocusable = false
        etData.isFocusableInTouchMode = false

        etData.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
                etData.setText(formattedDate)
            }, year, month, day)

            datePickerDialog.show()
        }

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
