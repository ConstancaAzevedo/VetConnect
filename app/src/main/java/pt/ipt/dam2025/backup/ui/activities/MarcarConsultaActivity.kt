package pt.ipt.dam2025.backup.ui.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2025.backup.R

/**
 * Activity para a página de marcar consulta
 */

class MarcarConsultaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_marcar_consulta)
    }
}
