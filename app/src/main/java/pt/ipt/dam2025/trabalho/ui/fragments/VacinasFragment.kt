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
import pt.ipt.dam2025.trabalho.ui.adapters.VacinaAdapter
import pt.ipt.dam2025.trabalho.viewmodel.HistoricoViewModel

class VacinasFragment : Fragment() {

    private val viewModel: HistoricoViewModel by activityViewModels()
    private lateinit var vacinaAdapter: VacinaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_document_list, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_documents)

        vacinaAdapter = VacinaAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = vacinaAdapter

        viewModel.vacinas.observe(viewLifecycleOwner) {
            vacinaAdapter.updateData(it)
        }

        return view
    }
}