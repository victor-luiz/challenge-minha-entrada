package br.com.minhaentrada.victor.challenge.ui.main

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import br.com.minhaentrada.victor.challenge.data.AppDatabase
import br.com.minhaentrada.victor.challenge.data.User
import br.com.minhaentrada.victor.challenge.data.UserRepository
import br.com.minhaentrada.victor.challenge.databinding.ActivityMainBinding
import br.com.minhaentrada.victor.challenge.ui.login.LoginActivity
import br.com.minhaentrada.victor.challenge.ui.profile.EditProfileDialogFragment
import br.com.minhaentrada.victor.challenge.util.DateUtils
import br.com.minhaentrada.victor.challenge.R

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory(
            UserRepository(
                AppDatabase.Companion.getDatabase(applicationContext).userDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        mainViewModel.loadUserData(sharedPreferences)
        setupActionButtons(sharedPreferences)
        observeViewModel()
        setupFragmentResultListener()
    }

    private fun setupActionButtons(sharedPreferences: SharedPreferences) {
        binding.editButton.setOnClickListener {
            onEditProfileClicked()
        }
        binding.deleteButton.setOnClickListener {
            onDeleteAccountClicked()
        }
    }

    private fun onEditProfileClicked() {
        mainViewModel.user.value?.let { user ->
            val editDialog = EditProfileDialogFragment.newInstance(user.id)
            editDialog.show(supportFragmentManager, "EditProfileDialog")
        }
    }

    private fun onDeleteAccountClicked() {
        AlertDialog.Builder(this)
            .setTitle("Excluir Conta")
            .setMessage("Tem certeza que deseja excluir sua conta?")
            .setPositiveButton("Excluir") { _, _ ->
                mainViewModel.user.value?.let { user ->
                    val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
                    mainViewModel.deleteUser(user, sharedPreferences)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun observeViewModel() {
        mainViewModel.user.observe(this) { user ->
            if (user != null) {
                bindUser(user)
            } else {
                goToLoginActivity()
            }
        }
        mainViewModel.deleteComplete.observe(this) { isComplete ->
            if (isComplete) {
                Toast.makeText(this, "Conta excluída com sucesso.", Toast.LENGTH_LONG).show()
                goToLoginActivity()
            }
        }
        mainViewModel.logoutComplete.observe(this) { isComplete ->
            if (isComplete) {
                goToLoginActivity()
            }
        }
    }

    private fun setupFragmentResultListener() {
        supportFragmentManager.setFragmentResultListener("profileEdited", this) { requestKey, bundle ->
            val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
            mainViewModel.loadUserData(sharedPreferences)
        }
    }

    private fun bindUser(user: User) {
        binding.usernameTextView.text = user.username
        binding.emailTextView.text = user.email
        val age = DateUtils.calculateAge(user.birthDate)
        displayAge(user.birthDate)
        binding.locationTextView.text = "${user.city} - ${user.state}"
    }

    private fun displayAge(birthDate: String?) {
        val age = DateUtils.calculateAge(birthDate)
        if (age != null) {
            binding.ageTextView.text = "Idade: $age anos"
        } else {
            binding.ageTextView.text = ""
        }
    }

    private fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
                mainViewModel.logout(sharedPreferences)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}