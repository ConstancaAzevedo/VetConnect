package pt.ipt.dam2025.vetconnect.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2025.vetconnect.databinding.ActivityMainBinding

/**
 * Activity principal que serve como anfitriã para a navegação de Fragments.
 * O seu único papel é carregar o layout que contém o NavHostFragment.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
