package br.com.minhaentrada.victor.challenge.ui.register

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import br.com.minhaentrada.victor.challenge.data.AppDatabase
import br.com.minhaentrada.victor.challenge.data.UserRepository
import br.com.minhaentrada.victor.challenge.databinding.ActivityRegisterBinding
import br.com.minhaentrada.victor.challenge.R
import java.util.Calendar

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

        setupBirthDateField()
        setupRegisterButton()
        observeRegistrationStatus()
    }

    private fun setupBirthDateField() {
        binding.birthdateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                this,
                {_, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    binding.birthdateEditText.setText(formattedDate)
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }
    }


    private fun setupRegisterButton() {
        binding.registerButton.setOnClickListener {
            clearFieldErrors()
            val username = binding.usernameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val birthDate = binding.birthdateEditText.text.toString().trim()
            val state = binding.locationSelector.selectedState
            val city = binding.locationSelector.selectedCity
            if (validateInput(username, email, password, birthDate, state, city)) {
                registerViewModel.registerUser(username, email, password, birthDate, state, city)
            }
        }
    }

    private fun validateInput(
        username: String,
        email: String,
        password: String,
        birthDate: String,
        state: String,
        city: String
    ): Boolean {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || birthDate.isEmpty() || state.isEmpty() || city.isEmpty()) {
            if (username.isEmpty()) {
                binding.usernameInputLayout.error = getString(R.string.error_empty_field)
            }
            if (email.isEmpty()) {
                binding.emailInputLayout.error = getString(R.string.error_empty_field)
            }
            if (password.isEmpty()) {
                binding.passwordInputLayout.error = getString(R.string.error_empty_field)
            }
            binding.locationSelector.validateFields()
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInputLayout.error = getString(R.string.error_invalid_email)
            return false
        }
        if (password.length < 6) {
            binding.passwordInputLayout.error = getString(R.string.error_weak_password)
            return false
        }
        if (birthDate.isEmpty()) {
            binding.birthdateInputLayout.error = getString(R.string.error_empty_field)
            return false
        }

        return true
    }

    private fun clearFieldErrors() {
        binding.usernameInputLayout.error = null
        binding.emailInputLayout.error = null
        binding.passwordInputLayout.error = null
        binding.birthdateInputLayout.error = null
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