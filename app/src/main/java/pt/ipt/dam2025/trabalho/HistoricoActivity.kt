package pt.ipt.dam2025.trabalho

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


//tela do histórico médico do animal
class HistoricoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historico)

        // 1. obter a referência para a RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.rvHistorico)

        // 2. criar dados de exemplo
        val historicoList = mutableListOf<HistoricoItem>()
        historicoList.add(HistoricoItem("2024-05-10", "Consulta de rotina. Tudo OK."))
        historicoList.add(HistoricoItem("2024-03-15", "Vacinação anual."))
        historicoList.add(HistoricoItem("2023-11-20", "Tratamento para pulgas e carrapatos."))
        historicoList.add(HistoricoItem("2023-08-01", "Cirurgia menor - remoção de cisto."))
        historicoList.add(HistoricoItem("2023-02-25", "Exames de sangue de rotina."))


        // 3. criar e definir o adapter
        val adapter = HistoricoAdapter(historicoList)
        recyclerView.adapter = adapter

        // 4. definir o layout manager
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}
