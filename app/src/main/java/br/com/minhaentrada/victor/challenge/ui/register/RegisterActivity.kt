package br.com.minhaentrada.victor.challenge.ui.register

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import br.com.minhaentrada.victor.challenge.R
import br.com.minhaentrada.victor.challenge.databinding.ActivityRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
    }

    private fun setupUI() {
        setupListeners()
        observeViewModel()
    }
    private fun setupListeners() {
        binding.registerButton.setOnClickListener {
            handleRegisterClick()
        }

        binding.goToLoginButton.setOnClickListener {
            finish()
        }
    }

    private fun observeViewModel() {
        registerViewModel.registrationStatus.observe(this) { state ->
            setLoading(state is RegisterViewModel.RegistrationState.Loading)
            when (state) {
                is RegisterViewModel.RegistrationState.Success -> {
                    Toast.makeText(this, getString(R.string.success_register), Toast.LENGTH_LONG).show()
                    finish()
                }
                is RegisterViewModel.RegistrationState.EmailAlreadyExists -> {
                    binding.emailInputLayout.error = getString(R.string.error_email_in_use)
                }
                is RegisterViewModel.RegistrationState.UsernameAlreadyExists -> {
                    binding.usernameInputLayout.error = getString(R.string.error_username_in_use)
                }
                else -> {}
            }
        }
    }

    private fun handleRegisterClick() {
        clearFieldErrors()

        val username = binding.usernameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()

        if (isInputValid(username, email, password, confirmPassword)) {
            registerViewModel.registerUser(username, email, password)
        }
    }
    private fun isInputValid(
        username: String,
        email: String,
        password: String,
        confirm: String
    ): Boolean {
        if (username.isEmpty()) {
            binding.usernameInputLayout.error = getString(R.string.error_empty_field)
            return false
        }
        if (email.isEmpty()) {
            binding.emailInputLayout.error = getString(R.string.error_empty_field)
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInputLayout.error = getString(R.string.error_invalid_email)
            return false
        }
        if (password.isEmpty()) {
            binding.passwordInputLayout.error = getString(R.string.error_empty_field)
            return false
        }
        if (password.length < 6) {
            binding.passwordInputLayout.error = getString(R.string.error_weak_password)
            return false
        }
        if (confirm.isEmpty()) {
            binding.confirmPasswordInputLayout.error = getString(R.string.error_empty_field)
            return false
        }
        if (password != confirm) {
            binding.confirmPasswordInputLayout.error = getString(R.string.error_passwords_do_not_match)
            return false
        }
        return true
    }

    private fun clearFieldErrors() {
        binding.usernameInputLayout.error = null
        binding.emailInputLayout.error = null
        binding.passwordInputLayout.error = null
        binding.confirmPasswordInputLayout.error = null
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.registerButton.isEnabled = !isLoading
        binding.goToLoginButton.isEnabled = !isLoading
    }
}