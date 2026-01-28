package pt.ipt.dam2025.vetconnect.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2025.vetconnect.databinding.ActivityMarcarConsultaBinding

/**
 * Activity para a página de marcar consulta
 */

class MarcarConsultaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMarcarConsultaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarcarConsultaBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
