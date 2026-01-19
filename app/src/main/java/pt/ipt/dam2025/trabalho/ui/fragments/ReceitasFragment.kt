package pt.ipt.dam2025.trabalho.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.ui.adapters.ReceitaAdapter
import pt.ipt.dam2025.trabalho.viewmodel.HistoricoViewModel

class ReceitasFragment : Fragment() {

    private val viewModel: HistoricoViewModel by activityViewModels()
    private lateinit var receitaAdapter: ReceitaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_document_list, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_documents)

        receitaAdapter = ReceitaAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = receitaAdapter

        viewModel.receitas.observe(viewLifecycleOwner) {
            receitaAdapter.updateData(it)
        }

        return view
    }
}