package br.com.minhaentrada.victor.challenge.ui.register

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import br.com.minhaentrada.victor.challenge.data.AppDatabase
import br.com.minhaentrada.victor.challenge.data.UserRepository
import br.com.minhaentrada.victor.challenge.databinding.ActivityRegisterBinding
import br.com.minhaentrada.victor.challenge.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val registerViewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory(
            UserRepository(
                AppDatabase.getDatabase(applicationContext).userDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRegisterButton()
        observeRegistrationStatus()
    }

    private fun setupRegisterButton() {
        binding.registerButton.setOnClickListener {
            clearFieldErrors()
            val username = binding.usernameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            if (validateInput(username, email, password)) {
                registerViewModel.registerUser(username, email, password)
            }
        }
    }

    private fun validateInput(username: String, email: String, password: String): Boolean {
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
        return true
    }

    private fun clearFieldErrors() {
        binding.usernameInputLayout.error = null
        binding.emailInputLayout.error = null
        binding.passwordInputLayout.error = null
    }

    private fun observeRegistrationStatus() {
        registerViewModel.registrationStatus.observe(this) { state ->
            when (state) {
                is RegisterViewModel.RegistrationState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.registerButton.isEnabled = true
                    Toast.makeText(this, getString(R.string.success_register), Toast.LENGTH_LONG).show()
                    finish()
                }
                is RegisterViewModel.RegistrationState.EmailAlreadyExists -> {
                    binding.progressBar.visibility = View.GONE
                    binding.registerButton.isEnabled = true
                    binding.emailInputLayout.error = getString(R.string.error_email_in_use)
                }
                is RegisterViewModel.RegistrationState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.registerButton.isEnabled = false
                }
            }
        }
    }
}