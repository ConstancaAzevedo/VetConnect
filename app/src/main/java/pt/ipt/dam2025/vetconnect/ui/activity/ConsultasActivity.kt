package pt.ipt.dam2025.vetconnect.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2025.vetconnect.databinding.ActivityConsultasBinding

/**
 * Activity para a página de consultas marcadas
 */

class ConsultasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConsultasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConsultasBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
