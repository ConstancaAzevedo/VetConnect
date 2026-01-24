package pt.ipt.dam2025.trabalho.ui.activities

import android.Manifest
import android.content.Context
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

// Activity principal do aplicativo
class MainActivity : AppCompatActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Snackbar.make(findViewById(android.R.id.content), "As notificações estão desativadas.", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton = findViewById<Button>(R.id.button_login)
        val registerButton = findViewById<Button>(R.id.button_register)
        val aboutButton = findViewById<Button>(R.id.about_button)
        val rootView = findViewById<android.view.View>(android.R.id.content)

        loginButton.setOnClickListener {
            val sharedPrefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
            val userId = sharedPrefs.getInt("USER_ID", -1)

            if (userId != -1) {
                // Se houver um utilizador logado, vai para a HomeActivity.
                 val intent = Intent(this@MainActivity, HomeActivity::class.java)
                 intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                 startActivity(intent)
            } else {
                // Se não, vai para o ecrã de login para escolher uma conta
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            }
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