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
import pt.ipt.dam2025.trabalho.model.Exame
import pt.ipt.dam2025.trabalho.ui.activities.DetalhesExameActivity
import pt.ipt.dam2025.trabalho.ui.adapters.ExameAdapter
import pt.ipt.dam2025.trabalho.viewmodel.HistoricoViewModel

// Fragment para a lista de exames
class ExamesFragment : Fragment() {

    private val viewModel: HistoricoViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExameAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_exames, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_exames)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ExameAdapter(mutableListOf()) { exame ->
            val intent = Intent(requireContext(), DetalhesExameActivity::class.java).apply {
                putExtra("EXAME_EXTRA", exame)
            }
            startActivity(intent)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.allExames.observe(viewLifecycleOwner, Observer { exames ->
            exames?.let { adapter.updateData(it) }
        })
    }
}