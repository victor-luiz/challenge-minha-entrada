package br.com.minhaentrada.victor.challenge

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import br.com.minhaentrada.victor.challenge.data.AppDatabase
import br.com.minhaentrada.victor.challenge.data.UserRepository
import br.com.minhaentrada.victor.challenge.databinding.ActivityMainBinding
import br.com.minhaentrada.victor.challenge.ui.login.LoginActivity
import br.com.minhaentrada.victor.challenge.ui.main.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory(
            UserRepository(
                AppDatabase.getDatabase(applicationContext).userDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        mainViewModel.loadUserData(sharedPreferences)
        setupLogoutButton(sharedPreferences)
        observeViewModel()
    }

    private fun setupLogoutButton(sharedPreferences: SharedPreferences) {
        binding.logoutButton.setOnClickListener {
            mainViewModel.logout(sharedPreferences)
        }
    }

    private fun observeViewModel() {
        mainViewModel.user.observe(this) { user ->
            if (user != null) {
                binding.usernameTextView.text = user.username
                binding.emailTextView.text = user.email
            } else {
                goToLoginActivity()
            }
        }

        mainViewModel.logoutComplete.observe(this) { isComplete ->
            if (isComplete) {
                goToLoginActivity()
            }
        }
    }

    private fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}