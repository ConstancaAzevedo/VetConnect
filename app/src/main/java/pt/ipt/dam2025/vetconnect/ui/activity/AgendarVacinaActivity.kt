package pt.ipt.dam2025.vetconnect.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2025.vetconnect.databinding.ActivityAgendarVacinaBinding

/**
 * Activity para a página de agendar vacinas
 */

class  AgendarVacinaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgendarVacinaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgendarVacinaBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
