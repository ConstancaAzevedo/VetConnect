package pt.ipt.dam2025.vetconnect.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2025.vetconnect.databinding.ActivityHistoricoBinding

/**
 * Activity para a página do historico medico
 */

class HistoricoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoricoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoricoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
