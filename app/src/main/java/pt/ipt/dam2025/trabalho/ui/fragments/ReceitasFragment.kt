package pt.ipt.dam2025.trabalho.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.model.Receita
import pt.ipt.dam2025.trabalho.ui.activities.DetalhesReceitaActivity
import pt.ipt.dam2025.trabalho.ui.adapters.ReceitaAdapter
import pt.ipt.dam2025.trabalho.viewmodel.HistoricoViewModel

// Fragment para a lista de receitas
class ReceitasFragment : Fragment() {

    private val viewModel: HistoricoViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReceitaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_receitas, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_receitas)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ReceitaAdapter(mutableListOf()) { receita ->
            val intent = Intent(requireContext(), DetalhesReceitaActivity::class.java).apply {
                putExtra("RECEITA_EXTRA", receita)
            }
            startActivity(intent)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.allReceitas.observe(viewLifecycleOwner, Observer { receitas ->
            receitas?.let { adapter.updateData(it) }
        })
    }
}