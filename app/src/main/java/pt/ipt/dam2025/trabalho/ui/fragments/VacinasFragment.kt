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
import pt.ipt.dam2025.trabalho.model.Vacina
import pt.ipt.dam2025.trabalho.ui.activities.DetalhesVacinaActivity
import pt.ipt.dam2025.trabalho.ui.adapters.VacinaAdapter
import pt.ipt.dam2025.trabalho.viewmodel.HistoricoViewModel

// Fragment para a lista de vacinas
class VacinasFragment : Fragment() {

    private val viewModel: HistoricoViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VacinaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_vacinas, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_vacinas)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = VacinaAdapter(mutableListOf()) { vacina ->
            val intent = Intent(requireContext(), DetalhesVacinaActivity::class.java).apply {
                putExtra("VACINA_EXTRA", vacina)
            }
            startActivity(intent)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.allVacinas.observe(viewLifecycleOwner, Observer { vacinas ->
            vacinas?.let { adapter.updateData(it) }
        })
    }
}