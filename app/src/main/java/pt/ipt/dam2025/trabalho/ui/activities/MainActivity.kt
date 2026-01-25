package pt.ipt.dam2025.trabalho.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.util.SessionManager

// Activity principal do aplicativo
class MainActivity : AppCompatActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Snackbar.make(findViewById(android.R.id.content), "As notificações estão desativadas.", Snackbar.LENGTH_SHORT).show()
        }
    }

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this)

        val loginButton = findViewById<Button>(R.id.button_login)
        val registerButton = findViewById<Button>(R.id.button_register)
        val aboutButton = findViewById<Button>(R.id.about_button)

        loginButton.setOnClickListener {
            // Vai sempre para o ecrã de login para escolher uma conta
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }

        registerButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, EscolhaActivity::class.java))
        }

        aboutButton.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }

        askNotificationPermission()
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
