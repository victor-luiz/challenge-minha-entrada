package br.com.minhaentrada.victor.challenge.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import br.com.minhaentrada.victor.challenge.R
import br.com.minhaentrada.victor.challenge.data.SessionManager
import br.com.minhaentrada.victor.challenge.databinding.ActivityLoginBinding
import br.com.minhaentrada.victor.challenge.ui.main.MainActivity
import br.com.minhaentrada.victor.challenge.ui.register.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.loginButton.setOnClickListener {
            handleLoginClick()
        }

        binding.goToRegisterButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handleLoginClick() {
        clearFieldErrors()
        val email = binding.loginEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        if (!isInputValid(email, password)) return

        loginViewModel.loginUser(email, password)
    }

    private fun isInputValid(email: String, password: String): Boolean {
        var isValid = true
        if (email.isEmpty()) {
            binding.loginInputLayout.error = getString(R.string.error_empty_field)
            isValid = false
        }
        if (password.isEmpty()) {
            binding.passwordInputLayout.error = getString(R.string.error_empty_field)
            isValid = false
        }
        return isValid
    }

    private fun observeViewModel() {
        loginViewModel.loginStatus.observe(this) { state ->
            setLoading(state is LoginViewModel.LoginState.Loading)

            when (state) {
                is LoginViewModel.LoginState.Success -> {
                    sessionManager.saveSession(true, state.user.id)
                    goToMainActivity()
                }
                is LoginViewModel.LoginState.UserNotFound -> {
                    binding.loginInputLayout.error = getString(R.string.error_user_not_found)
                }
                is LoginViewModel.LoginState.InvalidPassword -> {
                    binding.passwordInputLayout.error = getString(R.string.error_invalid_password)
                }
                else -> {
                }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.loginButton.isEnabled = !isLoading
        binding.goToRegisterButton.isEnabled = !isLoading
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun clearFieldErrors() {
        binding.loginInputLayout.error = null
        binding.passwordInputLayout.error = null
    }
}