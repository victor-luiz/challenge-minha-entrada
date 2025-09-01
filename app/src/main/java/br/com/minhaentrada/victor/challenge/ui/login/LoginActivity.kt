package br.com.minhaentrada.victor.challenge.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
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
            clearFieldErrors()
            val email = binding.loginEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) {
                if (email.isEmpty()) {
                    binding.loginInputLayout.error = getString(R.string.error_empty_field)
                }
                if (password.isEmpty()) {
                    binding.passwordInputLayout.error = getString(R.string.error_empty_field)
                }
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
            setLoading(state is LoginViewModel.LoginState.Loading)
            when (state) {
                is LoginViewModel.LoginState.Success -> {
                    saveUserSession(state.user.id)
                    goToMainActivity()
                }
                is LoginViewModel.LoginState.UserNotFound -> {
                    binding.loginInputLayout.error = getString(R.string.error_user_not_found)
                }
                is LoginViewModel.LoginState.InvalidPassword -> {
                    binding.passwordInputLayout.error = getString(R.string.error_invalid_password)
                }
                else -> {}
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.loginButton.isEnabled = false
            binding.goToRegisterButton.isEnabled = false
        } else {
            binding.progressBar.visibility = View.GONE
            binding.loginButton.isEnabled = true
            binding.goToRegisterButton.isEnabled = true
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

    private fun clearFieldErrors() {
        binding.loginInputLayout.error = null
        binding.passwordInputLayout.error = null
    }
}