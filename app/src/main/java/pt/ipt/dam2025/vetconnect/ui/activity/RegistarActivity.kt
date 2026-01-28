package pt.ipt.dam2025.vetconnect.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2025.vetconnect.databinding.ActivityRegistarBinding

/**
 * Activity para a página de registo de perfil do tutor
 */

class RegistarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistarBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
