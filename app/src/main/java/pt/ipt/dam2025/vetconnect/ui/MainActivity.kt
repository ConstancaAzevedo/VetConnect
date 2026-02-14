package pt.ipt.dam2025.vetconnect.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2025.vetconnect.databinding.ActivityMainBinding

/**
 * Activity principal que serve como anfitriã para a navegação de Fragments
 * O seu único propósito é carregar o layout que contém o NavHostFragment
 */
class MainActivity : AppCompatActivity() { // Declara a classe que herda de AppCompatActivity

    // Declaração da variável para o objeto de ViewBinding que ligará o código ao layout
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
