package br.com.minhaentrada.victor.challenge.ui.event

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import br.com.minhaentrada.victor.challenge.R
import br.com.minhaentrada.victor.challenge.data.AppDatabase
import br.com.minhaentrada.victor.challenge.data.EventCategory
import br.com.minhaentrada.victor.challenge.data.EventRepository
import br.com.minhaentrada.victor.challenge.ui.components.LocationSelectorView
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class AddEditEventDialogFragment : DialogFragment() {

    private val viewModel: AddEditEventViewModel by viewModels {
        AddEditEventViewModelFactory(
            EventRepository(AppDatabase.getDatabase(requireContext()).eventDao())
        )
    }

    private val selectedCalendar = Calendar.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_add_edit_event, null)

        val titleEditText = view.findViewById<TextInputEditText>(R.id.title_edit_text)
        val descriptionEditText = view.findViewById<TextInputEditText>(R.id.description_edit_text)
        val dateEditText = view.findViewById<TextInputEditText>(R.id.date_edit_text)
        val timeEditText = view.findViewById<TextInputEditText>(R.id.time_edit_text)
        val categoryAutocomplete = view.findViewById<AutoCompleteTextView>(R.id.category_autocomplete)
        val locationSelector = view.findViewById<LocationSelectorView>(R.id.location_selector_event)

        val eventId = arguments?.getLong(ARG_EVENT_ID) ?: 0L
        val userId = arguments?.getLong(ARG_USER_ID) ?: -1L

        setupCategoryDropdown(categoryAutocomplete)
        setupDateTimePickers(dateEditText, timeEditText)

        if (eventId != 0L) {
            builder.setTitle("Editar Evento")
            viewModel.loadEvent(eventId)
        } else {
            builder.setTitle("Criar Novo Evento")
        }

        viewModel.event.observe(this) { event ->
            event?.let {
                titleEditText.setText(it.title)
                descriptionEditText.setText(it.description)
                categoryAutocomplete.setText(getString(it.category.displayNameResId), false)
                locationSelector.setInitialLocation(it.state, it.city)

                selectedCalendar.time = it.eventDate
                updateDateText(dateEditText)
                updateTimeText(timeEditText)
            }
        }

        builder.setView(view)
            .setPositiveButton("Salvar") { _, _ ->
                val title = titleEditText.text.toString().trim()
                val description = descriptionEditText.text.toString().trim()
                val selectedCategoryString = categoryAutocomplete.text.toString()
                val category = EventCategory.entries.find {
                    getString(it.displayNameResId) == selectedCategoryString
                } ?: EventCategory.PARTY
                val state = locationSelector.selectedState
                val city = locationSelector.selectedCity

                // TODO: Adicionar validação de campos vazios

                viewModel.saveEvent(userId, title, description, selectedCalendar.time, category, city, state)
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.cancel()
            }

        viewModel.saveComplete.observe(this) { isComplete ->
            if (isComplete) {
                parentFragmentManager.setFragmentResult("event_saved", Bundle.EMPTY)
                dismiss()
            }
        }
        return builder.create()
    }

    private fun setupCategoryDropdown(categoryAutocomplete: AutoCompleteTextView) {
        val categories = EventCategory.entries.map { getString(it.displayNameResId) }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        categoryAutocomplete.setAdapter(adapter)
    }

    private fun setupDateTimePickers(dateEditText: EditText, timeEditText: EditText) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        dateEditText.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    selectedCalendar.set(Calendar.YEAR, year)
                    selectedCalendar.set(Calendar.MONTH, month)
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, day)
                    updateDateText(dateEditText)
                },
                selectedCalendar.get(Calendar.YEAR),
                selectedCalendar.get(Calendar.MONTH),
                selectedCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        timeEditText.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                { _, hour, minute ->
                    selectedCalendar.set(Calendar.HOUR_OF_DAY, hour)
                    selectedCalendar.set(Calendar.MINUTE, minute)
                    updateTimeText(timeEditText)
                },
                selectedCalendar.get(Calendar.HOUR_OF_DAY),
                selectedCalendar.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    private fun updateDateText(dateEditText: EditText) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateEditText.setText(dateFormat.format(selectedCalendar.time))
    }

    private fun updateTimeText(timeEditText: EditText) {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        timeEditText.setText(timeFormat.format(selectedCalendar.time))
    }

    companion object {
        private const val ARG_EVENT_ID = "event_id"
        private const val ARG_USER_ID = "user_id"

        fun newInstance(userId: Long, eventId: Long = 0L): AddEditEventDialogFragment {
            val args = Bundle()
            args.putLong(ARG_USER_ID, userId)
            args.putLong(ARG_EVENT_ID, eventId)
            val fragment = AddEditEventDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }
}

