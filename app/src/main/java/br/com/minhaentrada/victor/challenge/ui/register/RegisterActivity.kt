package br.com.minhaentrada.victor.challenge.ui.register

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import br.com.minhaentrada.victor.challenge.data.AppDatabase
import br.com.minhaentrada.victor.challenge.data.UserRepository
import br.com.minhaentrada.victor.challenge.databinding.ActivityRegisterBinding

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
            val username = binding.usernameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 6) {
                Toast.makeText(this, "A senha deve ter no mínimo 6 caracteres.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerViewModel.registerUser(username, email, password)
        }
    }

    private fun observeRegistrationStatus() {
        registerViewModel.registrationStatus.observe(this) { state ->
            when (state) {
                is RegisterViewModel.RegistrationState.Success -> {
                    Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_LONG).show()
                    finish() // Fecha a tela de cadastro e volta
                }
                is RegisterViewModel.RegistrationState.EmailAlreadyExists -> {
                    Toast.makeText(this, "Este e-mail já está em uso.", Toast.LENGTH_LONG).show()
                }
                is RegisterViewModel.RegistrationState.Loading -> {
                    // Aqui poderíamos mostrar um ProgressBar
                }
            }
        }
    }
}