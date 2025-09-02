package br.com.minhaentrada.victor.challenge.ui.event

import android.app.AlertDialog
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import br.com.minhaentrada.victor.challenge.databinding.ActivityEventBinding
import java.text.SimpleDateFormat
import java.util.*
import br.com.minhaentrada.victor.challenge.R
import br.com.minhaentrada.victor.challenge.data.event.Event
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEventBinding
    private val viewModel: EventViewModel by viewModels()
    private val dateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR"))
    private val timeFormat = SimpleDateFormat("HH:mm", Locale("pt", "BR"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbarEvent)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val eventId = intent.getLongExtra("EVENT_ID", -1L)
        viewModel.loadEvent(eventId)

        setupFragmentResultListener()
    }

    private fun observeViewModel() {
        viewModel.event.observe(this) { event ->
            event?.let { bindEventData(it) }
        }

        viewModel.deleteComplete.observe(this) { isComplete ->
            if (isComplete) {
                Toast.makeText(this, "Evento excluído com sucesso.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    private fun bindEventData(event: Event) {
        binding.toolbarEvent.title = event.title
        binding.categoryChip.setCategory(event.category)
        binding.locationTextView.text = "${event.city} - ${event.state}"
        binding.descriptionTextView.text = event.description
        binding.dateTextView.text = dateFormat.format(event.eventDate)
        binding.timeTextView.text = timeFormat.format(event.eventDate)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.event_menu, menu)
        try {
            val editColor = ContextCompat.getColor(this, R.color.color_icon_edit)
            val deleteColor = ContextCompat.getColor(this, R.color.color_icon_delete)
            colorMenuItem(menu?.findItem(R.id.action_edit_event), editColor)
            colorMenuItem(menu?.findItem(R.id.action_delete_event), deleteColor)
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

    private fun colorMenuItem(item: MenuItem?, color: Int) {
        item?.let {
            val spannableString = SpannableString(it.title)
            spannableString.setSpan(ForegroundColorSpan(color), 0, spannableString.length, 0)
            it.title = spannableString
            it.icon?.setTint(color)
        }
    }
}