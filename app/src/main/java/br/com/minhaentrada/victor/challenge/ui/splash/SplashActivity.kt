package br.com.minhaentrada.victor.challenge.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.com.minhaentrada.victor.challenge.R
import br.com.minhaentrada.victor.challenge.data.SessionManager
import br.com.minhaentrada.victor.challenge.databinding.ActivitySplashBinding
import br.com.minhaentrada.victor.challenge.ui.login.LoginActivity
import br.com.minhaentrada.victor.challenge.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startAnimation()
        navigateToNextScreen()
    }

    private fun startAnimation() {
        val pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_pulse_animation)
        binding.splashBlush.startAnimation(pulseAnimation)
    }

    private fun navigateToNextScreen() {
        lifecycleScope.launch {
            delay(3000)
            val intent = if (sessionManager.isLoggedIn()) {
                Intent(this@SplashActivity, MainActivity::class.java)
            } else {
                Intent(this@SplashActivity, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }
}