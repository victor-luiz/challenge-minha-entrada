package br.com.minhaentrada.victor.challenge.ui.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import br.com.minhaentrada.victor.challenge.R
import br.com.minhaentrada.victor.challenge.databinding.ActivitySplashBinding
import br.com.minhaentrada.victor.challenge.ui.login.LoginActivity
import br.com.minhaentrada.victor.challenge.ui.main.MainActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_pulse_animation)

        // 2. APLICA A ANIMAÇÃO APENAS NO BLUSH! O logo PNG fica estático.
        binding.splashBlush.startAnimation(pulseAnimation)

        Handler(Looper.getMainLooper()).postDelayed({
            checkSessionAndNavigate()
        }, 3000)
    }

    private fun checkSessionAndNavigate() {
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("IS_LOGGED_IN", false)
        val intent = if (isLoggedIn) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}