package pt.ipt.dam2025.vetconnect.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2025.vetconnect.databinding.ActivityVacinasBinding

/**
 * Activity para a página das vacinas já registadas
 */

class VacinasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVacinasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVacinasBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
