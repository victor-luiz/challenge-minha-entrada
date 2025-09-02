package br.com.minhaentrada.victor.challenge.ui.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.minhaentrada.victor.challenge.data.event.Event
import br.com.minhaentrada.victor.challenge.data.event.EnumEventCategory
import br.com.minhaentrada.victor.challenge.data.event.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddEditEventViewModel @Inject constructor(
    private val repository: EventRepository
) : ViewModel() {
    private val _event = MutableLiveData<Event?>()
    val event: LiveData<Event?> = _event

    private val _saveComplete = MutableLiveData<Boolean>()
    val saveComplete: LiveData<Boolean> = _saveComplete

    fun loadEvent(eventId: Long) {
        if (eventId == 0L) return
        viewModelScope.launch {
            _event.value = repository.getEventById(eventId)
        }
    }

    fun saveEvent(
        userId: Long,
        title: String,
        description: String,
        eventDate: Date,
        category: EnumEventCategory,
        city: String,
        state: String
    ) {
        val currentEvent = _event.value
        viewModelScope.launch {
            if (currentEvent == null) {
                val newEvent = Event(
                    userId = userId,
                    title = title,
                    description = description,
                    eventDate = eventDate,
                    category = category,
                    city = city,
                    state = state
                )
                repository.insert(newEvent)
            } else {
                val updatedEvent = currentEvent.copy(
                    title = title,
                    description = description,
                    eventDate = eventDate,
                    category = category,
                    city = city,
                    state = state
                )
                repository.update(updatedEvent)
            }
            _saveComplete.value = true
        }
    }
}