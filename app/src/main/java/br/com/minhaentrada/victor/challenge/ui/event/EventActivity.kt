package br.com.minhaentrada.victor.challenge.ui.event

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import br.com.minhaentrada.victor.challenge.data.AppDatabase
import br.com.minhaentrada.victor.challenge.data.EventRepository
import br.com.minhaentrada.victor.challenge.databinding.ActivityEventBinding
import java.text.SimpleDateFormat
import java.util.*
import br.com.minhaentrada.victor.challenge.R

class EventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEventBinding

    private val viewModel: EventViewModel by viewModels {
        EventViewModelFactory(
            EventRepository(AppDatabase.getDatabase(applicationContext).eventDao())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarEvent)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val eventId = intent.getLongExtra("EVENT_ID", -1L)
        viewModel.loadEvent(eventId)
        observeViewModel()
        setupFragmentResultListener()
    }

    private fun observeViewModel() {
        viewModel.event.observe(this) { event ->
            event?.let {
                binding.toolbarEvent.title = it.title
                binding.categoryChip.setCategory(it.category)
                binding.locationTextView.text = "${it.city} - ${it.state}"
                binding.descriptionTextView.text = it.description

                val dateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR"))
                val timeFormat = SimpleDateFormat("HH:mm", Locale("pt", "BR"))

                binding.dateTextView.text = dateFormat.format(it.eventDate)
                binding.timeTextView.text = timeFormat.format(it.eventDate)
            }
        }
        viewModel.deleteComplete.observe(this) { isComplete ->
            if (isComplete) {
                Toast.makeText(this, "Evento excluído com sucesso.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.event_menu, menu)
        try {
            val deleteItem = menu?.findItem(R.id.action_delete_event)
            deleteItem?.icon?.setTint(ContextCompat.getColor(this, R.color.color_icon_delete))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit_event -> {
                viewModel.event.value?.let { currentEvent ->
                    AddEditEventDialogFragment.newInstance(currentEvent.userId, currentEvent.id)
                        .show(supportFragmentManager, "EditEventDialog")
                }
                true
            }
            R.id.action_delete_event -> {
                showDeleteConfirmationDialog()
                true
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Excluir Evento")
            .setMessage("Tem certeza que deseja excluir este evento?")
            .setPositiveButton("Sim, Excluir") { _, _ ->
                viewModel.deleteEvent()
            }
            .setNegativeButton("Cancelar", null)
            .setIcon(R.drawable.ic_delete)
            .show()
    }

    private fun setupFragmentResultListener() {
        supportFragmentManager.setFragmentResultListener("event_saved", this) { _, _ ->
            val eventId = intent.getLongExtra("EVENT_ID", -1L)
            viewModel.loadEvent(eventId)
            Toast.makeText(this, "Evento atualizado com sucesso!", Toast.LENGTH_SHORT).show()
        }
    }
}