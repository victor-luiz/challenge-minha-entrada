package br.com.minhaentrada.victor.challenge.ui.main

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.content.ContextCompat
import br.com.minhaentrada.victor.challenge.databinding.ActivityMainBinding
import br.com.minhaentrada.victor.challenge.ui.login.LoginActivity
import br.com.minhaentrada.victor.challenge.ui.profile.EditProfileDialogFragment
import br.com.minhaentrada.victor.challenge.R
import br.com.minhaentrada.victor.challenge.ui.event.AddEditEventDialogFragment
import br.com.minhaentrada.victor.challenge.ui.event.EventActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var eventAdapter: EventAdapter
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupObservers()

        mainViewModel.loadInitialData()
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        setupRecyclerView()
        setupFab()
        setupFragmentResultListener()
    }

    private fun setupObservers() {
        mainViewModel.user.observe(this) { user ->
            user?.let { binding.userInfoHeader.bindUser(it) } ?: goToLoginActivity()
        }
        mainViewModel.events.observe(this) { eventList ->
            eventAdapter.submitList(eventList)
            updateEmptyState(eventList.isEmpty())
        }
        mainViewModel.logoutComplete.observe(this) { isComplete ->
            if (isComplete) goToLoginActivity()
        }
        mainViewModel.deleteComplete.observe(this) { isComplete ->
            if (isComplete) {
                Toast.makeText(this, "Conta excluída com sucesso.", Toast.LENGTH_LONG).show()
                goToLoginActivity()
            }
        }
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter { event ->
            val intent = Intent(this, EventActivity::class.java)
            intent.putExtra("EVENT_ID", event.id)
            startActivity(intent)
        }
        binding.eventsRecyclerView.adapter = eventAdapter
    }

    private fun setupFab() {
        binding.fabAddEvent.setOnClickListener {
            mainViewModel.user.value?.let { user ->
                AddEditEventDialogFragment.newInstance(user.id, 0L)
                    .show(supportFragmentManager, "AddEditEventDialog")
            }
        }
    }

    private fun observeViewModel() {
        mainViewModel.user.observe(this) { user ->
            user?.let { binding.userInfoHeader.bindUser(it) } ?: goToLoginActivity()
        }
        mainViewModel.deleteComplete.observe(this) { isComplete ->
            if (isComplete) {
                Toast.makeText(this, "Conta excluída com sucesso.", Toast.LENGTH_LONG).show()
                goToLoginActivity()
            }
        }
        mainViewModel.logoutComplete.observe(this) { isComplete ->
            if (isComplete) goToLoginActivity()
        }

        mainViewModel.events.observe(this) { eventList ->
            eventAdapter.submitList(eventList)
            if (eventList.isEmpty()) {
                binding.eventsRecyclerView.visibility = View.GONE
                binding.emptyListTextView.visibility = View.VISIBLE
            } else {
                binding.eventsRecyclerView.visibility = View.VISIBLE
                binding.emptyListTextView.visibility = View.GONE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        (menu as? MenuBuilder)?.setOptionalIconsVisible(true)

        try {
            colorMenuItem(menu?.findItem(R.id.action_edit_profile), ContextCompat.getColor(this, R.color.color_icon_edit))
            colorMenuItem(menu?.findItem(R.id.action_delete_account), ContextCompat.getColor(this, R.color.color_icon_delete))
            colorMenuItem(menu?.findItem(R.id.action_logout), ContextCompat.getColor(this, R.color.color_icon_logout))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                mainViewModel.logout()
                true
            }
            R.id.action_edit_profile -> {
                mainViewModel.user.value?.let { user ->
                    val editDialog = EditProfileDialogFragment.newInstance(user.id)
                    editDialog.show(supportFragmentManager, "EditProfileDialog")
                }
                true
            }
            R.id.action_delete_account -> {
                AlertDialog.Builder(this)
                    .setTitle("Excluir Conta")
                    .setMessage("Tem certeza que deseja excluir sua conta?")
                    .setPositiveButton("Sim, Excluir") { _, _ ->
                        mainViewModel.user.value?.let { user ->
                            val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
                            mainViewModel.deleteUser(user)
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateEmptyState(isListEmpty: Boolean) {
        binding.eventsRecyclerView.visibility = if (isListEmpty) View.GONE else View.VISIBLE
        binding.emptyListTextView.visibility = if (isListEmpty) View.VISIBLE else View.GONE
    }

    private fun colorMenuItem(item: MenuItem?, color: Int) {
        item?.let {
            val spannableString = SpannableString(it.title)
            spannableString.setSpan(ForegroundColorSpan(color), 0, spannableString.length, 0)
            it.title = spannableString
            it.icon?.setTint(color)
        }
    }

    private fun setupFragmentResultListener() {
        supportFragmentManager.setFragmentResultListener("profileEdited", this) { requestKey, bundle ->
            mainViewModel.loadInitialData()
        }
        supportFragmentManager.setFragmentResultListener("event_saved", this) { _, _ ->
            Toast.makeText(this, "Evento salvo com sucesso!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}