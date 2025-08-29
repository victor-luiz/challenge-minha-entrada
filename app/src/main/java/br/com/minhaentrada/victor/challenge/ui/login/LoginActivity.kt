package br.com.minhaentrada.victor.challenge.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import br.com.minhaentrada.victor.challenge.ui.main.MainActivity
import br.com.minhaentrada.victor.challenge.data.AppDatabase
import br.com.minhaentrada.victor.challenge.data.UserRepository
import br.com.minhaentrada.victor.challenge.databinding.ActivityLoginBinding
import br.com.minhaentrada.victor.challenge.ui.register.RegisterActivity
import androidx.core.content.edit
import br.com.minhaentrada.victor.challenge.R

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel : LoginViewModel by viewModels {
        LoginViewModelFactory(
            UserRepository(
                AppDatabase.getDatabase(applicationContext).userDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeLoginStatus()
    }

    private fun setupListeners() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            loginViewModel.loginUser(email, password)
        }

        binding.goToRegisterButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeLoginStatus() {
        loginViewModel.loginStatus.observe(this) { state ->
            when (state) {
                is LoginViewModel.LoginState.Success -> {
                    Toast.makeText(this, getString(R.string.success_login, state.user.username), Toast.LENGTH_SHORT).show()
                    saveUserSession(state.user.id)
                    goToMainActivity()
                }
                is LoginViewModel.LoginState.UserNotFound -> {
                    Toast.makeText(this, getString(R.string.error_user_not_found), Toast.LENGTH_SHORT).show()
                }
                is LoginViewModel.LoginState.InvalidPassword -> {
                    Toast.makeText(this, getString(R.string.error_invalid_password), Toast.LENGTH_LONG).show()
                }
                is LoginViewModel.LoginState.Loading -> {
                    // Aqui poderíamos mostrar uma ProgressBar
                }
            }
        }
    }

    private fun saveUserSession(userId: Long) {
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        sharedPreferences.edit {
            putBoolean("IS_LOGGED_IN", true)
            putLong("LOGGED_IN_USER_ID", userId)
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}