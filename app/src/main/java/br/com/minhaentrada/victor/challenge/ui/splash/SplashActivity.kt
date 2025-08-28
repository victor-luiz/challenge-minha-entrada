package br.com.minhaentrada.victor.challenge.ui.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.com.minhaentrada.victor.challenge.ui.main.MainActivity
import br.com.minhaentrada.victor.challenge.R
import br.com.minhaentrada.victor.challenge.ui.login.LoginActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        lifecycleScope.launch {
            delay(2500)
            val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
            val isLoggedIn = sharedPreferences.getBoolean("IS_LOGGED_IN", false)
            if (isLoggedIn) {
                goToMainActivity()
            } else {
                goToLoginActivity()
            }
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}